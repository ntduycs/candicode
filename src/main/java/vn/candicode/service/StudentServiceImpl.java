package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.candicode.common.FileAuthor;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.StudentEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.payload.request.UpdateStudentRoleRequest;
import vn.candicode.payload.response.SubmissionHistory;
import vn.candicode.payload.response.UserProfile;
import vn.candicode.repository.StudentRepository;
import vn.candicode.repository.UserRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommonService.Role;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static vn.candicode.service.CommonService.Role.getByRoleId;

@Service
@Log4j2
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    private final StorageService storageService;
    private final CommonService commonService;
    private final SubmissionService submissionService;

    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(UserRepository userRepository, StudentRepository studentRepository, StorageService storageService, CommonService commonService, SubmissionService submissionService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.commonService = commonService;
        this.submissionService = submissionService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create new student account and init required student directories
     *
     * @param payload
     * @return id of new student
     * @see NewStudentRequest
     */
    @Override
    @Transactional
    public Long createAccount(NewStudentRequest payload) throws IOException {
        final String encodedPassword = passwordEncoder.encode(payload.getPassword());

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new PersistenceException("User has already existing with email " + payload.getEmail());
        }

        StudentEntity student = new StudentEntity();
        student.setEmail(payload.getEmail());
        student.setPassword(encodedPassword);
        student.setFirstName(payload.getFirstName());
        student.setLastName(payload.getLastName());

        student.addRole(commonService.getStudentRoles().get(Role.STUDENT));
        student.setStudentPlan(commonService.getPlans().get("basic"));

        Long studentId = studentRepository.save(student).getUserId();

        storageService.initDirectoriesForUser(studentId, FileAuthor.STUDENT);

        return studentId;
    }

    /**
     * Update roles for student when he has just upgrade/downgrade his package (plan)
     *
     * @param payload
     */
    @Override
    @Transactional
    public void updateRole(Long studentId, UpdateStudentRoleRequest payload, UserPrincipal me) {
        if (payload.getRoles() == null || payload.getRoles().isEmpty()) {
            log.info("No role was provided to update student role. Ignore request");
            return;
        }

        StudentEntity student = studentRepository.findByUserIdFetchRoles(studentId)
            .orElseThrow(() -> new ResourceNotFoundException(StudentEntity.class, "id", studentId));

        Set<Long> newRoleIds = payload.getRoles().stream()
            .filter(id -> getByRoleId(id).isPresent())
            .collect(Collectors.toSet());

        Set<Long> existingRoleIds = student.getRoles().stream()
            .map(item -> item.getRole().getRoleId())
            .collect(Collectors.toSet());

        student.getRoles().removeIf(item -> !newRoleIds.contains(item.getRole().getRoleId()));
        newRoleIds.removeAll(existingRoleIds);

        // Do add new roles for admin
        if (!newRoleIds.isEmpty()) {
            newRoleIds.forEach(id -> getByRoleId(id).ifPresent(role -> student.addRole(commonService.getStudentRoles().get(role))));
        }
    }

    @Override
    @Transactional
    public UserProfile getStudentProfile(Long studentId) {
        UserProfile profile = new UserProfile();

        StudentEntity student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException(StudentEntity.class, "id", studentId));

        profile.setAvatar(storageService.resolvePath(student.getAvatar(), FileStorageType.AVATAR, studentId));
        profile.setCompany(student.getCompany());
        profile.setFacebook(student.getFacebook());
        profile.setFirstName(student.getFirstName());
        profile.setFullName(student.getFullName());
        profile.setGithub(student.getGithub());
        profile.setLastName(student.getLastName());
        profile.setLinkedin(student.getLinkedin());
        profile.setLocation(student.getLocation());
        profile.setSlogan(student.getSlogan());
        profile.setUniversity(student.getUniversity());
        profile.setUserId(student.getUserId());
        profile.setGainedPoint(student.getGainedPoint());

        List<SubmissionHistory> submissions = submissionService.getRecentSubmissionByUserId(studentId);

        profile.setRecentSubmissions(submissions);

        return profile;
    }
}
