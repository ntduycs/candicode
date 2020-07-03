package vn.candicode.repository;

import java.util.List;
import java.util.Set;

/**
 * This repository contain all named query that used when querying on multiple tables
 */
public interface SummaryRepository {

    List<Object[]> findLanguagesByChallengeId(Long challengeId);

    List<Object[]> findLanguagesByChallengeIdIn(Set<Long> challengeIds);

    List<Object[]> countChallengeAttendees(Set<Long> challengeIds);

    List<Object[]> countChallengeAttendees(Long challengeId);
}
