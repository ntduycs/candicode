package vn.candicode.repository;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.candicode.controller.Controller;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.request.ChallengePaginatedRequest;
import vn.candicode.util.DatetimeUtils;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class CommonRepositoryImpl implements CommonRepository {
    private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("\\d+");

    @PersistenceContext
    private EntityManager entityManager;

    public CommonRepositoryImpl() {
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

    @Override
    public Page<ChallengeEntity> findAllByAuthorId(Long authorId, ChallengePaginatedRequest criteria) {
        String selectClause = " SELECT c FROM ChallengeEntity c ";

        StringBuilder whereClause = new StringBuilder(" WHERE c.author.userId = :authorId ");

        List<String> languages = new ArrayList<>();
        int numLang = 0;
        if (StringUtils.hasText(criteria.getLanguage())) {
            languages = Arrays.stream(criteria.getLanguage().split(","))
                .map(lang -> lang.trim().toLowerCase())
                .collect(Collectors.toList());

            numLang = languages.size();

            if (languages.size() > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0, languagesSize = languages.size(); i < languagesSize; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" c.languages LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR c.languages LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        List<String> tags = new ArrayList<>();
        int numTags = 0;
        if (StringUtils.hasText(criteria.getTag())) {
            tags = Arrays.stream(criteria.getTag().split(","))
                .map(lang -> lang.trim().toLowerCase())
                .collect(Collectors.toList());

            numTags = tags.size();

            if (tags.size() > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0, tagSize = tags.size(); i < tagSize; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" c.tags LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR c.tags LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        if (StringUtils.hasText(criteria.getLevel())) {
            whereClause.append(" AND c.level = LOWER(:level) ");
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', :title, '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        whereClause.append(" AND c.contestChallenge = :contestChallenge ");

        TypedQuery<ChallengeEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), ChallengeEntity.class);

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numLang; i++) {
            query.setParameter("lang" + i, languages.get(i));
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        query.setParameter("authorId", authorId);

        if (StringUtils.hasText(criteria.getLevel())) {
            query.setParameter("level", criteria.getLevel());
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setParameter("contestChallenge", criteria.getContestChallenge());

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.challengeId) FROM ChallengeEntity c", Long.class);

        List<ChallengeEntity> result = query.getResultList();

        Collections.sort(result, (thisChallenge, thatChallenge) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisChallenge, null);
                Object thatValue = getter.invoke(thatChallenge, null);

                if (thisValue.getClass().isPrimitive() || thisValue instanceof String || thisValue instanceof LocalDateTime) {
                    int comparisionResult = thisValue.toString().compareTo(thatValue.toString());
                    return criteria.getDirection().equals("desc") ?
                        -comparisionResult :
                        comparisionResult;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
            return 0;
        });

        return new PageImpl<>(result, Controller.getPaginationConfig(criteria), countQuery.getSingleResult());
    }

    @Override
    public Page<ChallengeEntity> findAll(ChallengePaginatedRequest criteria) {
        String selectClause = " SELECT c FROM ChallengeEntity c ";

        StringBuilder whereClause = new StringBuilder(" WHERE TRUE = TRUE ");

        if (StringUtils.hasText(criteria.getAuthor())) {
            whereClause.append(" AND LOWER(c.authorName) LIKE CONCAT('%', LOWER(:authorName), '%') ");
        }

        List<String> languages = new ArrayList<>();
        int numLang = 0;
        if (StringUtils.hasText(criteria.getLanguage())) {
            languages = Arrays.stream(criteria.getLanguage().split(","))
                .map(lang -> lang.trim().toLowerCase())
                .collect(Collectors.toList());

            numLang = languages.size();

            if (languages.size() > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0, languagesSize = languages.size(); i < languagesSize; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" c.languages LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR c.languages LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        List<String> tags = new ArrayList<>();
        int numTags = 0;
        if (StringUtils.hasText(criteria.getTag())) {
            tags = Arrays.stream(criteria.getTag().split(","))
                .map(lang -> lang.trim().toLowerCase())
                .collect(Collectors.toList());

            numTags = tags.size();

            if (tags.size() > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0, tagSize = tags.size(); i < tagSize; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" c.tags LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR c.tags LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        if (StringUtils.hasText(criteria.getLevel())) {
            whereClause.append(" AND c.level = LOWER(:level) ");
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', :title, '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        whereClause.append(" AND c.contestChallenge = :contestChallenge AND c.available = TRUE AND c.deleted = FALSE ");

        TypedQuery<ChallengeEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), ChallengeEntity.class);

        if (StringUtils.hasText(criteria.getAuthor())) {
            query.setParameter("authorName", criteria.getAuthor());
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numLang; i++) {
            query.setParameter("lang" + i, languages.get(i));
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        if (StringUtils.hasText(criteria.getLevel())) {
            query.setParameter("level", criteria.getLevel());
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setParameter("contestChallenge", criteria.getContestChallenge());

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.challengeId) FROM ChallengeEntity c", Long.class);

        List<ChallengeEntity> result = query.getResultList();

        Collections.sort(result, (thisChallenge, thatChallenge) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisChallenge, null);
                Object thatValue = getter.invoke(thatChallenge, null);

                if (thisValue.getClass().isPrimitive() || thisValue instanceof String || thisValue instanceof LocalDateTime) {
                    int comparisionResult = thisValue.toString().compareTo(thatValue.toString());
                    return criteria.getDirection().equals("desc") ?
                        -comparisionResult :
                        comparisionResult;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            }
            return 0;
        });

        return new PageImpl<>(result, Controller.getPaginationConfig(criteria), countQuery.getSingleResult());
    }

    private String getAttributeNameFromGetter(Method getter) {
        String firstLetterOfAttributeName = String.valueOf(getter.getName().charAt("get".length())).toLowerCase();

        return firstLetterOfAttributeName + getter.getName().substring("get".length() + 1);
    }

    private String getGetterFromAttribute(String attr) {
        return "get" + String.valueOf(attr.charAt(0)).toUpperCase() + attr.substring(1);
    }

    private boolean isInteger(String s) {
        return POSITIVE_INTEGER_PATTERN.matcher(s).matches();
    }
}
