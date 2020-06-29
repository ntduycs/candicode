package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.payload.request.PasswordRequest;
import vn.candicode.payload.request.UpdateUserProfileRequest;
import vn.candicode.security.UserPrincipal;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    /**
     * User send change password request to system. The request is stored and system sends verification email to him.
     *
     * @param payload
     * @param currentUser Only account's owner can do this operation
     * @see #doChangePassword(Long)
     */
    @Override
    public void requireChangePassword(PasswordRequest payload, UserPrincipal currentUser) {

    }

    /**
     * After user has verified the change password request (via email), do truly change password task
     *
     * @param userId
     */
    @Override
    public void doChangePassword(Long userId) {

    }

    /**
     * Send user's reset password request via mail to confirm the operation
     *
     * @param currentUser user that want to reset password
     */
    @Override
    public void requireResetPassword(UserPrincipal currentUser) {

    }

    /**
     * This service is called when account's owner confirm the reset password request that sent to his mailbox.
     * It will then send new randomly generated password to his mailbox.
     *
     * @param userId
     */
    @Override
    public void doResetPassword(Long userId) {

    }

    /**
     * @param userId
     * @param payload
     * @param currentUser Only account's owner can update his profile
     */
    @Override
    public void updateProfile(Long userId, UpdateUserProfileRequest payload, UserPrincipal currentUser) {

    }
}
