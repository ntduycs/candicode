package vn.candicode.payloads.services.impl;

import org.springframework.stereotype.Service;
import vn.candicode.payloads.services.ExistDBRecordValidator;
import vn.candicode.payloads.validators.GenericValidator;
import vn.candicode.repositories.UserRepository;

import java.util.List;

@Service
public class ExistUserValidator implements ExistDBRecordValidator {
    private static final List<String> UNIQUE_COLUMNS = List.of("email");

    private final UserRepository userRepository;

    public ExistUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isExist(String column, Object value) {
        if ("email".equals(column)) {
            return userRepository.existsByEmail(value.toString());
        }

        return GenericValidator.NO_NEED_VALIDATE;
    }


}
