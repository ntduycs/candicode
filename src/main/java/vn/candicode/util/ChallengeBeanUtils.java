package vn.candicode.util;

import com.github.slugify.Slugify;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.payload.response.ChallengeDetails;
import vn.candicode.payload.response.ChallengeSummary;
import vn.candicode.payload.response.sub.TestcaseFormat;

import java.util.stream.Collectors;

public class ChallengeBeanUtils {
    private static final Slugify SLUGIFY = new Slugify();

    private static StorageService storageService;

    public static ChallengeSummary summarize(ChallengeEntity entity) {
        ChallengeSummary summary = new ChallengeSummary();

        summary.setLikes(entity.getLikes());
        summary.setDislikes(entity.getDislikes());
        summary.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        summary.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        summary.setChallengeId(entity.getChallengeId());
        summary.setBanner(storageService.resolvePath(entity.getBanner(), FileStorageType.BANNER, entity.getAuthor().getUserId()));
        summary.setAuthor(entity.getAuthorName());
        summary.setTitle(entity.getTitle());
        summary.setLevel(entity.getLevel());
        summary.setPoint(entity.getMaxPoint());
        summary.setSlug(SLUGIFY.slugify(entity.getTitle()));
        summary.setTags(entity.getTags());
        summary.setAvailable(entity.getAvailable());
        summary.setLanguages(entity.getLanguages());

        return summary;
    }

    public static ChallengeDetails details(ChallengeEntity entity) {
        ChallengeDetails details = new ChallengeDetails();

        details.setLikes(entity.getLikes());
        details.setDislikes(entity.getDislikes());
        details.setUpdatedAt(entity.getUpdatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        details.setChallengeId(entity.getChallengeId());
        details.setBanner(storageService.resolvePath(entity.getBanner(), FileStorageType.BANNER, entity.getAuthor().getUserId()));
        details.setAuthor(entity.getAuthorName());
        details.setTitle(entity.getTitle());
        details.setLevel(entity.getLevel());
        details.setPoint(entity.getMaxPoint());
        details.setSlug(SLUGIFY.slugify(entity.getTitle()));
        details.setTags(entity.getTags());
        details.setAvailable(entity.getAvailable());

        details.setCategories(entity.getCategories().stream().map(c -> c.getCategory().getName()).collect(Collectors.toList()));
        details.setDescription(entity.getDescription());
        details.setTcInputFormat(new TestcaseFormat(RegexUtils.resolveRegex(entity.getInputFormat())));
        details.setTcOutputFormat(new TestcaseFormat(RegexUtils.resolveRegex(entity.getOutputFormat())));
        details.setLanguages(entity.getLanguages());

        return details;
    }

    public static void setStorageService(StorageService service) {
        storageService = service;
    }
}
