package vn.candicode.util;

import com.github.slugify.Slugify;
import vn.candicode.entity.ContestEntity;
import vn.candicode.payload.response.ContestSummary;

import java.time.LocalDateTime;

public class ContestBeanUtils {
    private static final Slugify SLUGIFY = new Slugify();

    public static ContestSummary summarize(ContestEntity contest) {
        ContestSummary summary = new ContestSummary();

        summary.setContestId(contest.getContestId());
        summary.setBanner(contest.getBanner());
        summary.setDescription(contest.getDescription());
        summary.setRegistrationDeadline(contest.getRegistrationDeadline().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        summary.setTags(contest.getTags());
        summary.setTitle(contest.getTitle());
        summary.setSlug(SLUGIFY.slugify(contest.getTitle()));
        summary.setStatus("ongoing");

        return summary;
    }

    private static String contestStatus(LocalDateTime regDeadline) {
        return null;
    }
}
