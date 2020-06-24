package vn.candicode.util;

import com.github.slugify.Slugify;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.response.ChallengeSummary;

import java.util.stream.Collectors;

public class ChallengeBeanUtils {
    private static final Slugify SLUGIFY = new Slugify();

    public static ChallengeSummary summarize(ChallengeEntity entity) {
        ChallengeSummary summary = new ChallengeSummary();

        summary.setLikes(entity.getLikes());
        summary.setDislikes(entity.getDislikes());
        summary.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        summary.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        summary.setChallengeId(entity.getChallengeId());
        summary.setBanner(entity.getBanner());
        summary.setAuthor(entity.getAuthor().getFullName());
        summary.setTitle(entity.getTitle());
        summary.setLevel(entity.getLevel());
        summary.setPoint(entity.getMaxPoint());
        summary.setSlug(SLUGIFY.slugify(entity.getTitle()));
        summary.setTags(entity.getTags());
        summary.setCategories(entity.getCategories().stream().map(c -> c.getCategory().getName()).collect(Collectors.toList()));
        summary.setNumComments((long) entity.getComments().size());
//        summary.setNumAttendees(entity.ge);

        return summary;
    }
}
