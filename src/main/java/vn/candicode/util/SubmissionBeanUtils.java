package vn.candicode.util;

import vn.candicode.entity.SubmissionEntity;
import vn.candicode.payload.response.SubmissionHistory;

public class SubmissionBeanUtils {
    public static SubmissionHistory summarize(SubmissionEntity entity) {
        SubmissionHistory history = new SubmissionHistory();

        history.setSubmissionId(entity.getSubmissionId());
        history.setDoneWithin(entity.getDoneWithin());
        history.setExecTime(entity.getExecTime());
        history.setCompiled(entity.getCompiled());
        history.setAuthor(entity.getAuthorName());
        history.setChallengeId(entity.getChallenge().getChallengeId());
        history.setChallengeTitle(entity.getChallenge().getTitle());
        history.setPoint(entity.getPoint());
        history.setPassedTestcases(entity.getPassedTestcases());
        history.setTotalTestcases(entity.getTotalTestcases());
        history.setCreatedAt(entity.getCreatedAt().format(DatetimeUtils.JSON_DATETIME_FORMAT));
        history.setContestChallenge(entity.getChallenge().getContestChallenge());
        history.setSubmitAt(entity.getSubmitAt());
        history.setLanguage(entity.getLanguage().getName());

        return history;
    }
}
