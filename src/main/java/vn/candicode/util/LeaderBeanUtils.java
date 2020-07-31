package vn.candicode.util;

import vn.candicode.entity.SubmissionEntity;
import vn.candicode.payload.response.sub.Leader;

public class LeaderBeanUtils {
    public static Leader toLeader(SubmissionEntity entity) {
        Leader leader = new Leader();

        leader.setFistName(entity.getAuthor().getFirstName());
        leader.setLastName(entity.getAuthor().getLastName());
        leader.setFullName(entity.getAuthor().getFullName());
        leader.setGainedScore(entity.getPoint());
        leader.setMaxScore(entity.getChallenge().getMaxPoint());
        leader.setSubmitAt(entity.getSubmitAt());
        leader.setTime(entity.getDoneWithin());
        leader.setUserId(entity.getAuthor().getUserId());
        leader.setAvatar(entity.getAuthor().getAvatar());

        return leader;
    }
}
