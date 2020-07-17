package vn.candicode.repository;

import org.springframework.data.domain.Page;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.ContestEntity;
import vn.candicode.entity.StudentEntity;
import vn.candicode.entity.TutorialEntity;
import vn.candicode.payload.request.ChallengePaginatedRequest;
import vn.candicode.payload.request.ContestPaginatedRequest;
import vn.candicode.payload.request.TutorialPaginatedRequest;
import vn.candicode.payload.request.UserPaginatedRequest;

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

    Page<ChallengeEntity> findAll(ChallengePaginatedRequest criteria);

    Page<TutorialEntity> findAllByAuthorId(Long authorId, TutorialPaginatedRequest criteria);

    Page<TutorialEntity> findAll(TutorialPaginatedRequest criteria);

    Page<ContestEntity> findAllByAuthorId(Long authorId, ContestPaginatedRequest criteria);

    Page<ContestEntity> findAll(ContestPaginatedRequest criteria, boolean isAdmin);

    Page<StudentEntity> findAll(UserPaginatedRequest criteria);
}
