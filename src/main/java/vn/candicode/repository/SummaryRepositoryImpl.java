package vn.candicode.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Repository
public class SummaryRepositoryImpl implements SummaryRepository {
    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SummaryRepositoryImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public List<Object[]> findLanguagesByChallengeId(Long challengeId) {
        return entityManager.createQuery(
            "SELECT c.challenge.challengeId, c.nonImplementedFile, l.name FROM ChallengeConfigurationEntity c, LanguageEntity l WHERE c.challenge.challengeId = :id AND c.language.languageId = l.languageId"
        ).setParameter("id", challengeId).getResultList();
    }

    @Override
    public List<Object[]> findLanguagesByChallengeIdIn(Set<Long> challengeIds) {
        return entityManager.createQuery(
            "SELECT c.challenge.challengeId, l.name FROM ChallengeConfigurationEntity c, LanguageEntity l WHERE c.challenge.challengeId in (:ids) AND c.language.languageId = l.languageId"
        ).setParameter("ids", challengeIds).getResultList();
    }

    @Override
    public List<Object[]> countChallengeAttendees(Set<Long> challengeIds) {
        return entityManager.createQuery(
            "SELECT s.challenge.challengeId, count(s) from SubmissionEntity s WHERE s.challenge.challengeId IN (:ids) GROUP BY s.challenge.challengeId"
        ).setParameter("ids", challengeIds).getResultList();
    }

    @Override
    public List<Object[]> countChallengeAttendees(Long challengeId) {
        return entityManager.createQuery(
            "SELECT s.challenge.challengeId, count(s) from SubmissionEntity s WHERE s.challenge.challengeId = :id GROUP BY s.challenge.challengeId"
        ).setParameter("id", challengeId).getResultList();
    }
}
