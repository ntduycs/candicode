package vn.candicode.service;

public class EmailServiceImpl implements EmailService {

    /**
     * Notify user that his password has just changed
     *
     * @param userId
     */
    @Override
    public void sendChangePasswordEmail(Long userId) {

    }

    /**
     * Require the user's confirmation when he or someone want to reset his account's password
     *
     * @param userId
     */
    @Override
    public void sendResetPasswordConfirmationEmail(Long userId) {

    }

    @Override
    public void sendNewGeneratedPasswordEmail(Long userId) {

    }

    /**
     * Notify user that his email has just used to register with Candicode system. It also serves as welcome email.
     *
     * @param userId
     */
    @Override
    public void sendRegistrationEmail(Long userId) {

    }

    /**
     * Notify user that his account has just upgrade to higher package (plan)
     *
     * @param userId
     */
    @Override
    public void sendUpgradePlanEmail(Long userId) {

    }

    /**
     * Notify users about the upcoming contest round
     *
     * @param userId
     * @param contestRoundId
     */
    @Override
    public void sendContestNotificationEmail(Long userId, Long contestRoundId) {

    }
}
