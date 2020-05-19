package vn.candicode.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.models.Coder;
import vn.candicode.models.enums.CoderPlan;
import vn.candicode.models.enums.UserType;
import vn.candicode.payloads.requests.RegisterRequest;
import vn.candicode.repositories.CoderRepository;

@Service
public class CoderServiceImpl implements CoderService {
    private final CoderRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final StorageService storageService;

    public CoderServiceImpl(CoderRepository coderRepository,
                            PasswordEncoder passwordEncoder,
                            StorageService storageService) {
        this.repository = coderRepository;
        this.passwordEncoder = passwordEncoder;
        this.storageService = storageService;
    }

    /**
     * @param registerRequest
     * @return ID of new coder account
     */
    @Override
    @Transactional
    public Long createNewCoderAccount(RegisterRequest registerRequest) {
        final String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        Coder coder = new Coder(
            registerRequest.getEmail(),
            encodedPassword,
            registerRequest.getFirstName(),
            registerRequest.getLastName(),
            UserType.CODER,
            CoderPlan.BASIC
        );

        Long id = repository.save(coder).getId();

        storageService.createUserStorageLocation(id);

        return id;
    }
}
