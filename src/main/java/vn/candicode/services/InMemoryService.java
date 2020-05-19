package vn.candicode.services;

import org.springframework.stereotype.Service;
import vn.candicode.models.enums.ChallengeLanguage;
import vn.candicode.repositories.ChallengeLanguageRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryService {
    private final ChallengeLanguageRepository languageRepository;

    private final Map<ChallengeLanguage, vn.candicode.models.ChallengeLanguage> challengeLanguages = new HashMap<>();

    public InMemoryService(ChallengeLanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
        languageRepository.findAll().forEach(lang -> {
            challengeLanguages.put(lang.getName(), lang);
        });
    }

    public Map<ChallengeLanguage, vn.candicode.models.ChallengeLanguage> challengeLanguages() {
        return challengeLanguages;
    }
}
