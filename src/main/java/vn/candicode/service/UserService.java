package vn.candicode.service;

import vn.candicode.payload.request.PasswordRequest;
import vn.candicode.payload.request.UpdateUserProfileRequest;
import vn.candicode.security.UserPrincipal;

/**
 * The services are shared with students and admins
 */
public interface UserService {
    /**
     * Change user's password and send a notification via registered user's email
     *
     * @param payload
     * @param currentUser Only account's owner can do this operation
     */
    void challengePassword(PasswordRequest payload, UserPrincipal currentUser);

    /**
     * Send user's reset password request via mail to confirm the operation
     *
     * @param currentUser user that want to reset password
     */
    void requiredResetPassword(UserPrincipal currentUser);

    /**
     * This service is called when account's owner confirm the reset password request that sent to his mailbox.
     * It will then send new randomly generated password to his mailbox.
     *
     * @param userId
     */
    void doResetPassword(Long userId);

    /**
     * @param userId
     * @param payload
     * @param currentUser Only account's owner can update his profile
     */
    void updateProfile(Long userId, UpdateUserProfileRequest payload, UserPrincipal currentUser);
}
