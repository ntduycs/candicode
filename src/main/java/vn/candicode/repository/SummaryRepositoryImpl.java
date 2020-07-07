package vn.candicode.repository;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class SummaryRepositoryImpl implements SummaryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SummaryRepositoryImpl() {
    }

    @Override
    public Map<Long, Long> countNumCommentsGroupByChallengeId(List<Long> challengeIds) {
        return entityManager.createQuery(
            "SELECT c.challenge.challengeId as challengeId, count(c.commentId) as numComments " +
                "FROM ChallengeCommentEntity c " +
                "WHERE c.challenge.challengeId IN (:ids) " +
                "GROUP BY c.challenge.challengeId", Tuple.class)
            .setParameter("ids", challengeIds)
            .getResultStream()
            .collect(Collectors.toMap(
                tuple -> tuple.get("challengeId", Long.class),
                tuple -> tuple.get("numComments", Long.class))
            );
    }

    @Override
    public Map<Long, Long> countNumSubmissionsGroupByChallengeId(List<Long> challengeIds) {
        return entityManager.createQuery(
            "SELECT s.challenge.challengeId as challengeId, count(s.submissionId) as numSubmissions " +
                "FROM SubmissionEntity s " +
                "WHERE s.challenge.challengeId IN (:ids) " +
                "GROUP BY s.challenge.challengeId", Tuple.class)
            .setParameter("ids", challengeIds)
            .getResultStream()
            .collect(Collectors.toMap(
                tuple -> tuple.get("challengeId", Long.class),
                tuple -> tuple.get("numSubmissions", Long.class))
            );
    }

    @Override
    public Map<Long, List<String>> findAllLanguagesByChallengeId(List<Long> challengeIds) {
        TypedQuery<Tuple> query = entityManager.createQuery(
            "SELECT cf.challenge.challengeId as challengeId, cf.language.name as language " +
                "FROM ChallengeConfigurationEntity cf " +
                "WHERE cf.challenge.challengeId IN (:cids)", Tuple.class);

        query.setParameter("cids", challengeIds);

        List<Tuple> tuples = query.getResultList();

        Map<Long, List<String>> ret = new HashMap<>();

        for (Tuple tuple : tuples) {
            if (ret.containsKey(tuple.get("challengeId", Long.class))) {
                ret.get(tuple.get("challengeId", Long.class)).add(tuple.get("language", String.class));
            } else {
                ret.put(tuple.get("challengeId", Long.class), Lists.newArrayList(tuple.get("language", String.class)));
            }
        }

        return ret;
    }

    @Override
    public Map<Long, List<String>> findAllCategoriesByChallengeId(List<Long> challengeIds) {
        TypedQuery<Tuple> query = entityManager.createQuery(
            "SELECT cc.challenge.challengeId as challengeId, cc.category.name as category " +
                "FROM ChallengeCategoryEntity cc " +
                "WHERE cc.challenge.challengeId IN (:cids)", Tuple.class);

        query.setParameter("cids", challengeIds);

        List<Tuple> tuples = query.getResultList();

        Map<Long, List<String>> ret = new HashMap<>();

        for (Tuple tuple : tuples) {
            if (ret.containsKey(tuple.get("challengeId", Long.class))) {
                ret.get(tuple.get("challengeId", Long.class)).add(tuple.get("category", String.class));
            } else {
                ret.put(tuple.get("challengeId", Long.class), Lists.newArrayList(tuple.get("category", String.class)));
            }
        }

        return ret;
    }
}
