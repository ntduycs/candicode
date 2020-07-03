package vn.candicode.service;

public interface NotificationService {
    /**
     * This service is called when:
     * <ul>
     *     <li>Someone likes/dislikes your challenge</li>
     *     <li>Someone participated in solving your challenge</li>
     * </ul>
     */
    void sendChallengeNotification(Long challengeId);

    /**
     * This service is called when:
     * <ul>
     *     <li>Someone registered to join your contest</li>
     * </ul>
     */
    void sendContestNotification(Long contestId);

    /**
     * This service is called when:
     * <ul>
     *     <li>The contest round that you've registered/created is about to starting</li>
     *     <li>The contest round that you've joined/created has just ended (notify user to check result)</li>
     * </ul>
     */
    void sendContestRoundNotification(Long contestRoundId);

    /**
     * This service is called when:
     * <ul>
     *     <li>Someone likes/dislikes your comment</li>
     *     <li>Someone replied your comment</li>
     * </ul>
     */
    void sendCommentNotification(Long commentId);
}
