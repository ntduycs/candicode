package vn.candicode.utils;

import org.springframework.stereotype.Service;
import vn.candicode.models.LanguageEntity;
import vn.candicode.models.PlanEntity;
import vn.candicode.models.enums.LanguageName;
import vn.candicode.models.enums.PlanName;
import vn.candicode.repositories.LanguageRepository;
import vn.candicode.repositories.PlanRepository;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PreloadEntities {
    private final Map<LanguageName, LanguageEntity> languageEntities;

    private final Map<PlanName, PlanEntity> planEntities;

    public PreloadEntities(LanguageRepository languageRepository, PlanRepository planRepository) {
        this.languageEntities = languageRepository.findAll().stream()
            .collect(Collectors.toMap(LanguageEntity::getText, entity -> entity));

        this.planEntities = planRepository.findAll().stream()
            .collect(Collectors.toMap(PlanEntity::getText, entity -> entity));
    }

    public Map<LanguageName, LanguageEntity> getLanguageEntities() {
        return languageEntities;
    }

    public Map<PlanName, PlanEntity> getPlanEntities() {
        return planEntities;
    }
}
