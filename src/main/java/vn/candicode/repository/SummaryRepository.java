package vn.candicode.repository;

import org.springframework.data.domain.Pageable;
import vn.candicode.payload.response.ChallengeSummary;

import java.util.List;

/**
 * This repository contain all named query that used when querying on multiple tables
 */
public interface SummaryRepository {

    List<ChallengeSummary> getChallengeSummaryList(Pageable payload);
}
