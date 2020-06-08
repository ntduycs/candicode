package vn.candicode.payloads.services.impl;

import org.springframework.stereotype.Service;
import vn.candicode.payloads.services.UniqueDBRecordValidator;
import vn.candicode.repositories.ChallengeRepository;

import java.util.List;

import static vn.candicode.payloads.validators.GenericValidator.NO_NEED_VALIDATE;

@Service
public class UniqueChallengeValidator implements UniqueDBRecordValidator {
    private static final List<String> UNIQUE_COLUMNS = List.of("title");

    private final ChallengeRepository challengeRepository;

    public UniqueChallengeValidator(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public boolean isUnique(String column, Object value) {
        if ("title".equals(column)) {
            return !challengeRepository.existsByTitle(value.toString());
        }

        return NO_NEED_VALIDATE;
    }
}
