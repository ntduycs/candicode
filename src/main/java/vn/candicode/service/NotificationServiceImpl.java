package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class NotificationServiceImpl implements NotificationService {
    /**
     * This service is called when:
     * <ul>
     *     <li>Someone likes/dislikes your challenge</li>
     *     <li>Someone participated in solving your challenge</li>
     * </ul>
     *
     * @param challengeId
     */
    @Override
    public void sendChallengeNotification(Long challengeId) {

    }

    /**
     * This service is called when:
     * <ul>
     *     <li>Someone registered to join your contest</li>
     * </ul>
     *
     * @param contestId
     */
    @Override
    public void sendContestNotification(Long contestId) {

    }

    /**
     * This service is called when:
     * <ul>
     *     <li>The contest round that you've registered/created is about to starting</li>
     *     <li>The contest round that you've joined/created has just ended (notify user to check result)</li>
     * </ul>
     *
     * @param contestRoundId
     */
    @Override
    public void sendContestRoundNotification(Long contestRoundId) {

    }

    /**
     * This service is called when:
     * <ul>
     *     <li>Someone likes/dislikes your comment</li>
     *     <li>Someone replied your comment</li>
     * </ul>
     *
     * @param commentId
     */
    @Override
    public void sendCommentNotification(Long commentId) {

    }
}
