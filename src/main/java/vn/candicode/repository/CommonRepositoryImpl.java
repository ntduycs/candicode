package vn.candicode.repository;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.candicode.controller.Controller;
import vn.candicode.entity.*;
import vn.candicode.payload.request.ChallengePaginatedRequest;
import vn.candicode.payload.request.ContestPaginatedRequest;
import vn.candicode.payload.request.TutorialPaginatedRequest;
import vn.candicode.payload.request.UserPaginatedRequest;
import vn.candicode.payload.response.sub.Leader;
import vn.candicode.util.DatetimeUtils;

import javax.persistence.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        String selectClause = StringUtils.hasText(criteria.getCategory()) ?
            " SELECT c FROM ChallengeEntity c, ChallengeCategoryEntity cc " :
            " SELECT c FROM ChallengeEntity c ";

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
                        orClause.append(" LOWER(c.languages) LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.languages) LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
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
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }


        List<String> categories = new ArrayList<>();
        int numCategories = 0;
        if (StringUtils.hasText(criteria.getCategory())) {
            categories = Arrays.stream(criteria.getCategory().split(","))
                .map(category -> category.trim().toLowerCase())
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

            numCategories = categories.size();

            if (numCategories > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0; i < numCategories; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" LOWER(cc.category.name) LIKE CONCAT('%', LOWER(:category")
                            .append(i)
                            .append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(cc.category.name) LIKE CONCAT('%', LOWER(:category")
                            .append(i)
                            .append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
                whereClause.append(" AND c.challengeId = cc.challenge.challengeId ");
            }
        }

        if (StringUtils.hasText(criteria.getLevel())) {
            whereClause.append(" AND LOWER(c.level) = LOWER(:level) ");
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', LOWER(:title), '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        if (criteria.getContestChallenge() != null) {
            whereClause.append(" AND c.contestChallenge = :contestChallenge ");
        }

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

        for (int i = 0; i < numCategories; i++) {
            query.setParameter("category" + i, categories.get(i));
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

        if (criteria.getContestChallenge() != null) {
            query.setParameter("contestChallenge", criteria.getContestChallenge());
        }

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
        String selectClause = StringUtils.hasText(criteria.getCategory()) ?
            " SELECT c FROM ChallengeEntity c, ChallengeCategoryEntity cc " :
            " SELECT c FROM ChallengeEntity c ";

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
                        orClause.append(" LOWER(c.languages) LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.languages) LIKE CONCAT('%', LOWER(:lang").append(i).append("), '%') ");
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
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

            numTags = tags.size();

            if (numTags > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0, tagSize = tags.size(); i < tagSize; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        List<String> categories = new ArrayList<>();
        int numCategories = 0;
        if (StringUtils.hasText(criteria.getCategory())) {
            categories = Arrays.stream(criteria.getCategory().split(","))
                .map(category -> category.trim().toLowerCase())
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());

            numCategories = categories.size();

            if (numCategories > 0) {
                whereClause.append(" AND ( ");
                StringBuilder orClause = new StringBuilder();
                for (int i = 0; i < numCategories; i++) {
                    if (orClause.length() == 0) {
                        orClause.append(" LOWER(cc.category.name) LIKE CONCAT('%', LOWER(:category")
                            .append(i)
                            .append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(cc.category.name) LIKE CONCAT('%', LOWER(:category")
                            .append(i)
                            .append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
                whereClause.append(" AND c.challengeId = cc.challenge.challengeId ");
            }
        }

        if (StringUtils.hasText(criteria.getLevel())) {
            whereClause.append(" AND LOWER(c.level) = LOWER(:level) ");
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', LOWER(:title), '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        whereClause.append(" AND c.contestChallenge = FALSE AND c.available = TRUE AND c.deleted = FALSE ");

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

        for (int i = 0; i < numCategories; i++) {
            query.setParameter("category" + i, categories.get(i));
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

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.challengeId) FROM ChallengeEntity c", Long.class);

        List<ChallengeEntity> result = query.getResultList();

        result.sort((thisChallenge, thatChallenge) -> {
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
    public Page<TutorialEntity> findAllByAuthorId(Long authorId, TutorialPaginatedRequest criteria) {
        String selectClause = " SELECT c FROM TutorialEntity c ";

        StringBuilder whereClause = new StringBuilder(" WHERE c.author.userId = :authorId ");

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
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', LOWER(:title), '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        TypedQuery<TutorialEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), TutorialEntity.class);

        query.setParameter("authorId", authorId);

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.tutorialId) FROM TutorialEntity c", Long.class);

        List<TutorialEntity> result = query.getResultList();

        result.sort((thisTutorial, thatTutorial) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisTutorial, null);
                Object thatValue = getter.invoke(thatTutorial, null);

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
    public Page<TutorialEntity> findAll(TutorialPaginatedRequest criteria) {
        String selectClause = " SELECT c FROM TutorialEntity c ";

        StringBuilder whereClause = new StringBuilder(" WHERE TRUE = TRUE ");

        if (StringUtils.hasText(criteria.getAuthor())) {
            whereClause.append(" AND LOWER(c.authorName) LIKE CONCAT('%', LOWER(:authorName), '%') ");
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
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
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

        TypedQuery<TutorialEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), TutorialEntity.class);

        if (StringUtils.hasText(criteria.getAuthor())) {
            query.setParameter("authorName", criteria.getAuthor());
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.tutorialId) FROM TutorialEntity c", Long.class);

        List<TutorialEntity> result = query.getResultList();

        result.sort((thisTutorial, thatTutorial) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisTutorial, null);
                Object thatValue = getter.invoke(thatTutorial, null);

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
    public Page<ContestEntity> findAllByAuthorId(Long authorId, ContestPaginatedRequest criteria) {
        String selectClause = " SELECT c FROM ContestEntity c ";

        StringBuilder whereClause = new StringBuilder(" WHERE c.author.userId = :authorId ");

        if (StringUtils.hasText(criteria.getStatus())) {
            whereClause.append(" AND LOWER(c.status) = LOWER(:status) ");
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
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', LOWER(:title), '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        TypedQuery<ContestEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), ContestEntity.class);

        query.setParameter("authorId", authorId);

        if (StringUtils.hasText(criteria.getStatus())) {
            query.setParameter("status", criteria.getStatus());
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.contestId) FROM ContestEntity c", Long.class);

        List<ContestEntity> result = query.getResultList();

        result.sort((thisTutorial, thatTutorial) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisTutorial, null);
                Object thatValue = getter.invoke(thatTutorial, null);

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
    public Page<ContestEntity> findAll(ContestPaginatedRequest criteria, boolean isAdmin) {
        String selectClause = " SELECT c FROM ContestEntity c ";

        StringBuilder whereClause = new StringBuilder();
        if (isAdmin) {
            whereClause.append(" WHERE TRUE = TRUE ");
        } else {
            whereClause.append(" WHERE c.available = TRUE ");
        }

        if (StringUtils.hasText(criteria.getAuthor())) {
            whereClause.append(" AND LOWER(c.authorName) LIKE CONCAT('%', LOWER(:authorName), '%') ");
        }

        if (StringUtils.hasText(criteria.getStatus())) {
            whereClause.append(" AND LOWER(c.status) = LOWER(:status) ");
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
                        orClause.append(" LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    } else {
                        orClause.append(" OR LOWER(c.tags) LIKE CONCAT('%', LOWER(:tag").append(i).append("), '%') ");
                    }
                }
                whereClause.append(orClause);
                whereClause.append(" ) ");
            }
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            whereClause.append(" AND LOWER(c.title) LIKE CONCAT('%', LOWER(:title), '%') ");
        }

        if (criteria.getStart() != null && criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) BETWEEN TRUNC(:start) AND TRUNC(:end) ");
        } else if (criteria.getStart() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) >= TRUNC(:start) ");
        } else if (criteria.getEnd() != null) {
            whereClause.append(" AND TRUNC(c.createdAt) <= TRUNC(:end) ");
        }

        TypedQuery<ContestEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), ContestEntity.class);

        if (StringUtils.hasText(criteria.getAuthor())) {
            query.setParameter("authorName", criteria.getAuthor());
        }

        if (StringUtils.hasText(criteria.getStatus())) {
            query.setParameter("status", criteria.getStatus());
        }

        if (StringUtils.hasText(criteria.getTitle())) {
            query.setParameter("title", criteria.getTitle());
        }

        for (int i = 0; i < numTags; i++) {
            query.setParameter("tag" + i, tags.get(i));
        }

        if (criteria.getStart() != null) {
            Date startDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getStart()));
            query.setParameter("start", startDate, TemporalType.DATE);
        }

        if (criteria.getEnd() != null) {
            Date endDate = DatetimeUtils.asDate(LocalDate.now().minusDays(criteria.getEnd()));
            query.setParameter("end", endDate, TemporalType.DATE);
        }

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.contestId) FROM ContestEntity c", Long.class);

        List<ContestEntity> result = query.getResultList();

        result.sort((thisContest, thatContest) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisContest, null);
                Object thatValue = getter.invoke(thatContest, null);

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
    public Page<UserEntity> findAll(UserPaginatedRequest criteria) {
        String selectClause = " SELECT c FROM UserEntity c ";

        StringBuilder whereClause = new StringBuilder();

        whereClause.append(" WHERE TRUE = TRUE ");

        if (StringUtils.hasText(criteria.getType())
            && Lists.newArrayList("admin", "student").contains(criteria.getType().toLowerCase())) {
            whereClause.append(" AND TYPE(c) = LOWER(:type) ");
        }

        if (StringUtils.hasText(criteria.getFirstName())) {
            whereClause.append(" AND LOWER(c.firstName) LIKE CONCAT('%', LOWER(:firstName), '%') ");
        }

        if (StringUtils.hasText(criteria.getLastName())) {
            whereClause.append(" AND LOWER(c.lastName) = LOWER(:lastName) ");
        }

        TypedQuery<UserEntity> query = entityManager.createQuery(selectClause + whereClause.toString(), UserEntity.class);

        if (StringUtils.hasText(criteria.getType())
            && Lists.newArrayList("admin", "student").contains(criteria.getType().toLowerCase())) {
            query.setParameter("type", criteria.getType());
        }

        if (StringUtils.hasText(criteria.getFirstName())) {
            query.setParameter("firstName", criteria.getFirstName());
        }

        if (StringUtils.hasText(criteria.getLastName())) {
            query.setParameter("lastName", criteria.getLastName());
        }

        query.setMaxResults(criteria.getSize());
        query.setFirstResult((criteria.getPage() - 1) * criteria.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(c.userId) FROM UserEntity c", Long.class);

        List<UserEntity> result = query.getResultList();

        result.sort((thisStudent, thatStudent) -> {
            try {
                Method getter = ChallengeEntity.class.getMethod(getGetterFromAttribute(criteria.getSort()));
                Object thisValue = getter.invoke(thisStudent, null);
                Object thatValue = getter.invoke(thatStudent, null);

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

    @Override
    public boolean isFinish(Long contestId) {
        TypedQuery<ContestRoundEntity> query = entityManager.createQuery(
            "SELECT r FROM ContestRoundEntity r WHERE r.contest.contestId = :id ORDER BY r.startsAt DESC"
            , ContestRoundEntity.class).setMaxResults(1);

        query.setParameter("id", contestId);

        try {
            ContestRoundEntity lastRound = query.getSingleResult();

            return lastRound.getStartsAt().plusMinutes(lastRound.getDuration()).isBefore(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Leader> getLeaders(Long contestId) {
        TypedQuery<RoundScore> query = entityManager.createQuery("" +
            "SELECT new vn.candicode.repository.RoundScore(s.author, SUM(s.point), r.contestRoundId, SUM(s.doneWithin)) " +
            "FROM SubmissionEntity s, ContestRoundEntity r " +
            "WHERE r.contest.contestId = :id " +
            "GROUP BY r.contestRoundId, s.author.userId, s.point, s.doneWithin " +
            "ORDER BY r.contestRoundId DESC ", RoundScore.class);

        query.setParameter("id", contestId);

        List<RoundScore> results = query.getResultList();

        Long maxScoreOfContest = entityManager.createQuery("" +
            "SELECT SUM(c.maxPoint) " +
            "FROM ContestRoundEntity r, ChallengeEntity c, ContestChallengeEntity cc " +
            "WHERE r.contest.contestId = :id AND " +
            "       cc.challenge.challengeId = c.challengeId AND " +
            "       cc.contestRound.contestRoundId = r.contestRoundId " +
            "GROUP BY c.maxPoint", Long.class).setParameter("id", contestId).getSingleResult();

        Map<UserEntity, Pair<Long /*totalScore*/, Double /*totalTimeInNano*/> /*totalScore*/> totalScoreByUserId = new HashMap<>();

        for (RoundScore roundScore : results) {
            if (totalScoreByUserId.containsKey(roundScore.getUser())) {
                totalScoreByUserId.computeIfPresent(roundScore.getUser(), (userID, pair) -> Pair.of(pair.getFirst() + roundScore.getScore(), pair.getSecond() + roundScore.getTime()));
            } else {
                totalScoreByUserId.put(roundScore.getUser(), Pair.of(roundScore.getScore(), roundScore.getTime()));
            }
        }

        Map<UserEntity, Pair<Long, Double>> sortedTotalScoreByUserId = new LinkedHashMap<>(10);

        totalScoreByUserId.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue((o1, o2) -> {
                if (!o1.getFirst().equals(o2.getFirst())) {
                    return o1.getFirst().compareTo(o2.getFirst());
                } else {
                    return o1.getSecond().compareTo(o2.getSecond());
                }
            }))
            .limit(10)
            .forEachOrdered(rc -> sortedTotalScoreByUserId.put(rc.getKey(), rc.getValue()));

        List<Leader> leaders = sortedTotalScoreByUserId.entrySet().stream()
            .map(rc -> new Leader(rc.getValue().getFirst(), maxScoreOfContest, rc.getKey().getUserId(), rc.getKey().getFullName(),
                rc.getKey().getFirstName(), rc.getKey().getLastName(), rc.getKey().getAvatar(), rc.getValue().getSecond(), null))
            .collect(Collectors.toList());

        return leaders;
    }



    @Override
    public List<SubmissionEntity> getRecentSubmissionsByUserId(Long userId) {
        TypedQuery<SubmissionEntity> query = entityManager.createQuery(
            "SELECT s FROM SubmissionEntity s WHERE s.author.userId = :id ORDER BY s.createdAt DESC", SubmissionEntity.class);

        query.setParameter("id", userId);
        query.setMaxResults(10);

        return query.getResultList();
    }

    @Override
    public List<ContestEntity> getRegisteredIncomingContests(Long userId) {
        TypedQuery<ContestEntity> incomingContests = entityManager.createQuery(
            "SELECT c " +
                "FROM ContestEntity c, ContestRegistrationEntity cr " +
                "WHERE cr.contest.contestId = c.contestId " +
                "AND cr.student.userId = :id " +
                "AND EXISTS (SELECT r FROM ContestRoundEntity r WHERE r.contest.contestId = c.contestId AND r.startsAt > :now) " +
                "AND EXISTS (SELECT reg FROM ContestRegistrationEntity reg WHERE reg.student.userId = :id AND reg.contest.contestId = c.contestId)"
            , ContestEntity.class);

        incomingContests.setParameter("id", userId);
        incomingContests.setParameter("now", LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));

        return incomingContests.getResultList();
    }
}
