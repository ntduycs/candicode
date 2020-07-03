package vn.candicode.service;

public interface NotificationService {
    /**
     * This service is called when:
     * <ul>
     *     <li>Someone likes/dislikes your challenge</li>
     * </ul>
     */
    void sendChallengeNotification(Long challengeId);

    /**
     * When someone like your tutorial
     *
     * @param tutorialId
     */
    void sendTutorialNotification(Long tutorialId);

    /**
     * This service is called when:
     * <ul>
     *     <li>The contest round that you've registered/created is about to starting</li>
     * </ul>
     */
    void sendContestRoundNotification(Long contestRoundId);

    void sendUpgradePlanNotification(Long userId);
}
