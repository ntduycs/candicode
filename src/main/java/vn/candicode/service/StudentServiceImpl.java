package vn.candicode.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.candicode.common.FileAuthor;
import vn.candicode.core.StorageService;
import vn.candicode.entity.RoleEntity;
import vn.candicode.entity.StudentEntity;
import vn.candicode.payload.request.NewStudentRequest;
import vn.candicode.repository.RoleRepository;
import vn.candicode.repository.StudentRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static vn.candicode.common.FileAuthor.STUDENT;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    private final StorageService storageService;

    private final PasswordEncoder passwordEncoder;

    private final Map<String, RoleEntity> availableRoles;

    public StudentServiceImpl(StudentRepository studentRepository, RoleRepository roleRepository, StorageService storageService, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.passwordEncoder = passwordEncoder;

        this.availableRoles = roleRepository
            .findAllByNameIn(List.of("student", "challenge creator", "contest creator", "tutorial creator"))
            .stream().collect(Collectors.toMap(RoleEntity::getName, role -> role));
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

        StudentEntity student = new StudentEntity();
        student.setEmail(payload.getEmail());
        student.setPassword(encodedPassword);
        student.setFirstName(payload.getFirstName());
        student.setLastName(payload.getLastName());

        student.addRole(availableRoles.get("student"));

        Long studentId = studentRepository.save(student).getUserId();

        storageService.initDirectoriesForUser(studentId, STUDENT);

        return studentId;
    }
}
