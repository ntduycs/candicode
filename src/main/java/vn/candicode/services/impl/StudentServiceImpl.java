package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.models.StudentEntity;
import vn.candicode.payloads.requests.StudentRequest;
import vn.candicode.repositories.StudentRepository;
import vn.candicode.services.StorageService;
import vn.candicode.services.StudentService;
import vn.candicode.utils.PreloadEntities;

import java.util.Set;

import static vn.candicode.models.enums.PlanName.Basic;
import static vn.candicode.models.enums.Role.Student;

@Service
@Log4j2
public class StudentServiceImpl implements StudentService {
    private final StorageService storageService;

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    private final PreloadEntities preloadEntities;

    public StudentServiceImpl(StorageService storageService,
                              StudentRepository studentRepository,
                              PasswordEncoder passwordEncoder,
                              PreloadEntities preloadEntities) {
        this.storageService = storageService;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.preloadEntities = preloadEntities;
    }

    /**
     * Create new student account and init required student directories
     *
     * @param payload
     * @return Newly created student id
     */
    @Override
    @Transactional
    public Long createStudentAccount(StudentRequest payload) {
        String encodedPassword = passwordEncoder.encode(payload.getPassword());

        StudentEntity studentEntity = new StudentEntity(
            payload.getEmail(),
            encodedPassword,
            payload.getFirstName(),
            payload.getLastName()
        );

        studentEntity.setPlan(preloadEntities.getPlanEntities().get(Basic));

        studentEntity.setRoles(Set.of(Student));

        studentEntity = studentRepository.save(studentEntity);

        storageService.initStudentDirectories(studentEntity.getUserId());

        return studentEntity.getUserId();
    }
}
