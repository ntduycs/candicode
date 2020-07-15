package vn.candicode.repository;

import org.springframework.data.domain.Page;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.request.ChallengePaginatedRequest;

import java.util.List;
import java.util.Map;

/**
 * This repository contain all named query that used when querying on multiple tables
 */
public interface CommonRepository {

    Map<Long, Long> countNumCommentsGroupByChallengeId(List<Long> challengeIds);

    Map<Long, Long> countNumSubmissionsGroupByChallengeId(List<Long> challengeIds);

    Map<Long, List<String>> findAllLanguagesByChallengeId(List<Long> challengeIds);

    Map<Long, List<String>> findAllCategoriesByChallengeId(List<Long> challengeIds);

    Page<ChallengeEntity> findAllByAuthorId(Long authorId, ChallengePaginatedRequest criteria);
}
