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
     * </ul>
     *
     * @param challengeId
     */
    @Override
    public void sendChallengeNotification(Long challengeId) {

    }

    /**
     * When someone like your tutorial
     *
     * @param tutorialId
     */
    @Override
    public void sendTutorialNotification(Long tutorialId) {

    }

    /**
     * This service is called when:
     * <ul>
     *     <li>The contest round that you've registered/created is about to starting</li>
     * </ul>
     *
     * @param contestRoundId
     */
    @Override
    public void sendContestRoundNotification(Long contestRoundId) {

    }

    @Override
    public void sendUpgradePlanNotification(Long userId) {

    }
}
