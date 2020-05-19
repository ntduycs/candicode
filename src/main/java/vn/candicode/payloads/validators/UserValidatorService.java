package vn.candicode.payloads.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.candicode.repositories.UserRepository;

@Service
public class UserValidatorService implements UniqueValidatorService, ExistenceValidatorService {
    private final UserRepository userRepository;

    @Autowired
    public UserValidatorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean exists(String key, Object value) {
        if (key.equals("email")) {
            return userRepository.existsByEmail(value.toString());
        }

        throw new UnsupportedOperationException(ExistenceValidatorService.getUnsupportedFieldMessage(key));
    }

    @Override
    public boolean isUnique(String key, Object value) {
        if (key.equals("email")) {
            return !userRepository.existsByEmail(value.toString());
        }

        throw new UnsupportedOperationException(UniqueValidatorService.getUnsupportedFieldMessage(key));
    }
}
