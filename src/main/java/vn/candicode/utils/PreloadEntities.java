package vn.candicode.utils;

import org.springframework.stereotype.Service;
import vn.candicode.models.LanguageEntity;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.repositories.LanguageRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreloadEntities {
    private final LanguageRepository languageRepository;

    private final Map<LanguageName, LanguageEntity> languageEntities;

    public PreloadEntities(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;

        this.languageEntities = this.languageRepository.findAll().stream()
            .collect(Collectors.toMap(LanguageEntity::getName, entity -> entity));
    }

    public Map<LanguageName, LanguageEntity> getLanguageEntities() {
        return languageEntities;
    }
}
