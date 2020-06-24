package vn.candicode.repository;

/**
 * This repository contain all named query that used when querying on multiple tables
 */
public interface SummaryRepository {

    Object findLanguagesByChallengeId(Long challengeId);
}
