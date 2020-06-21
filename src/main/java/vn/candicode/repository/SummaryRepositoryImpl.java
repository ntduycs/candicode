package vn.candicode.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.response.ChallengeSummary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class SummaryRepositoryImpl implements SummaryRepository {
    private final ChallengeRepository challengeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SummaryRepositoryImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public List<ChallengeSummary> getChallengeSummaryList(Pageable pageable) {
        Page<ChallengeEntity> challenges = getChallengeList(pageable);
        StringBuilder hql = new StringBuilder();

        return null;
    }

    private Page<ChallengeEntity> getChallengeList(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }
}
