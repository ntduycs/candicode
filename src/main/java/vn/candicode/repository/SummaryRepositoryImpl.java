package vn.candicode.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class SummaryRepositoryImpl implements SummaryRepository {
    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SummaryRepositoryImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public Object findLanguagesByChallengeId(Long challengeId) {
        Query query = entityManager.createQuery(
            "SELECT c.challenge.challengeId, l.name FROM ChallengeConfigurationEntity c, LanguageEntity l WHERE c.challenge.challengeId = :id AND c.language = l GROUP BY c.challenge.challengeId, l.name"
        );

        query.setParameter("id", challengeId);

        return query.getResultList();
    }
}
