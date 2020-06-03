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

@Service
@Log4j2
public class StudentServiceImpl implements StudentService {
    private final StorageService storageService;

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StorageService storageService,
                              StudentRepository studentRepository,
                              PasswordEncoder passwordEncoder) {
        this.storageService = storageService;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
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

        studentEntity = studentRepository.save(studentEntity);

        storageService.initStudentDirectories(studentEntity.getUserId());

        return studentEntity.getUserId();
    }
}
