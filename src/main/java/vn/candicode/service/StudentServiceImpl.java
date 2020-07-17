package vn.candicode.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.candicode.common.FileAuthor;
import vn.candicode.core.StorageService;
import vn.candicode.entity.StudentEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.payload.request.UpdateStudentRoleRequest;
import vn.candicode.repository.StudentRepository;
import vn.candicode.repository.UserRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommonService.Role;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static vn.candicode.service.CommonService.Role.getByRoleId;

@Service
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    private final StorageService storageService;
    private final CommonService commonService;

    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(UserRepository userRepository, StudentRepository studentRepository, StorageService storageService, CommonService commonService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.commonService = commonService;
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
    public void updateRole(Long studentId, UpdateStudentRoleRequest payload, UserPrincipal me) {
        StudentEntity student = studentRepository.findByUserIdFetchRoles(studentId)
            .orElseThrow(() -> new ResourceNotFoundException(StudentEntity.class, "id", studentId));

        if (payload.getRoles() == null || payload.getRoles().isEmpty()) {
            return;
        }

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
}
