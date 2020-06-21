package vn.candicode.service;

public interface EmailService {
    /**
     * Notify user that his password has just changed
     */
    void sendChangePasswordEmail(Long userId);

    /**
     * Require the user's confirmation when he or someone want to reset his account's password
     */
    void sendResetPasswordConfirmationEmail(Long userId);

    void sendNewGeneratedPasswordEmail(Long userId);

    /**
     * Notify user that his email has just used to register with Candicode system. It also serves as welcome email.
     */
    void sendRegistrationEmail(Long userId);

    /**
     * Notify user that his account has just upgrade to higher package (plan)
     */
    void sendUpgradePlanEmail(Long userId);

    /**
     * Notify users about the upcoming contest round
     */
    void sendContestNotificationEmail(Long userId, Long contestRoundId);
}
