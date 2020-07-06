package vn.candicode.repository;

import java.util.List;
import java.util.Map;

/**
 * This repository contain all named query that used when querying on multiple tables
 */
public interface SummaryRepository {

    Map<Long, Long> countNumCommentsGroupByChallengeId(List<Long> challengeIds);

    Map<Long, Long> countNumSubmissionsGroupByChallengeId(List<Long> challengeIds);

    Map<Long, List<String>> findAllLanguagesByChallengeId(List<Long> challengeIds);

    Map<Long, List<String>> findAllCategoriesByChallengeId(List<Long> challengeIds);
}
