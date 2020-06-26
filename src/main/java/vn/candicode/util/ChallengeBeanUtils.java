package vn.candicode.util;

import com.github.slugify.Slugify;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.response.ChallengeDetails;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.sub.TestcaseFormat;

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

        return summary;
    }

    public static ChallengeDetails details(ChallengeEntity entity) {
        ChallengeDetails details = new ChallengeDetails();

        details.setLikes(entity.getLikes());
        details.setDislikes(entity.getDislikes());
        details.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setChallengeId(entity.getChallengeId());
        details.setBanner(entity.getBanner());
        details.setAuthor(entity.getAuthor().getFullName());
        details.setTitle(entity.getTitle());
        details.setLevel(entity.getLevel());
        details.setPoint(entity.getMaxPoint());
        details.setSlug(SLUGIFY.slugify(entity.getTitle()));
        details.setTags(entity.getTags());
        details.setCategories(entity.getCategories().stream().map(c -> c.getCategory().getName()).collect(Collectors.toList()));
        details.setNumComments((long) entity.getComments().size());
        details.setDescription(entity.getDescription());
        details.setTcInputFormat(new TestcaseFormat(RegexUtils.resolveRegex(entity.getInputFormat())));
        details.setTcOutputFormat(new TestcaseFormat(RegexUtils.resolveRegex(entity.getOutputFormat())));

        return details;
    }
}
