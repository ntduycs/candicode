package vn.candicode.service;

import com.google.common.io.BaseEncoding;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.entity.PasswordUpdateEntity;
import vn.candicode.entity.UserEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.PasswordRequest;
import vn.candicode.payload.request.UpdateUserProfileRequest;
import vn.candicode.repository.PasswordUpdateRepository;
import vn.candicode.repository.UserRepository;
import vn.candicode.security.UserPrincipal;

import java.util.Random;

@Service
@Log4j2
public class UserServiceImpl implements UserService {
    private static final long PASSWORD_UPDATE_EXPIRATION = 24 * 60; // days
    private static final Random RANDOM = new Random();

    private final PasswordUpdateRepository passwordUpdateRepository;
    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordUpdateRepository passwordUpdateRepository, UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.passwordUpdateRepository = passwordUpdateRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * User send change password request to system. The request is stored and system sends verification email to him.
     *
     * @param payload
     * @param currentUser Only account's owner can do this operation
     * @see #doChangePassword(Long)
     */
    @Override
    @Transactional
    public void requireChangePassword(PasswordRequest payload, UserPrincipal currentUser) {
        if (!payload.getNewPassword().equals(payload.getConfirmPassword())) {
            throw new BadRequestException("Confirm password does not match");
        }

        if (!passwordEncoder.matches(payload.getOldPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Old password not correct");
        }

        PasswordUpdateEntity entity = new PasswordUpdateEntity();

        entity.setNewPassword(passwordEncoder.encode(payload.getNewPassword()));
        entity.setOldPassword(currentUser.getPassword());
        entity.setUser(currentUser.getEntityRef());
        entity.setExpiredIn(PASSWORD_UPDATE_EXPIRATION);

        passwordUpdateRepository.save(entity);

        emailService.sendChangePasswordEmail(currentUser.getUserId());
    }

    /**
     * After user has verified the change password request (via email), do truly change password task
     *
     * @param userId
     */
    @Override
    @Transactional
    public void doChangePassword(Long userId) {
        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(UserEntity.class, "id", userId));

        PasswordUpdateEntity passwordUpdateEntity = passwordUpdateRepository.findMostRecentPasswordUpdateRequestByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(PasswordUpdateEntity.class, "userId", userId));

        user.setPassword(passwordUpdateEntity.getNewPassword());
    }

    /**
     * Send user's reset password request via mail to confirm the operation
     *
     * @param currentUser user that want to reset password
     */
    @Override
    public void requireResetPassword(UserPrincipal currentUser) {
        emailService.sendResetPasswordConfirmationEmail(currentUser.getUserId());
    }

    /**
     * This service is called when account's owner confirm the reset password request that sent to his mailbox.
     * It will then send new randomly generated password to his mailbox.
     *
     * @param userId
     */
    @Override
    @Transactional
    public void doResetPassword(Long userId) {
        byte[] buffer = new byte[8];

        RANDOM.nextBytes(buffer);

        String randomPassword = BaseEncoding.base64Url().omitPadding().encode(buffer);

        UserEntity user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException(UserEntity.class, "id", userId));

        String encodedPassword = passwordEncoder.encode(randomPassword);
        user.setPassword(encodedPassword);

        emailService.sendNewGeneratedPasswordEmail(userId, randomPassword);
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
