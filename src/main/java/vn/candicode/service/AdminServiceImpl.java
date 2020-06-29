package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.FileAuthor;
import vn.candicode.core.StorageService;
import vn.candicode.entity.AdminEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewAdminRequest;
import vn.candicode.payload.request.UpdateAdminRoleRequest;
import vn.candicode.repository.AdminRepository;
import vn.candicode.repository.UserRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.service.CommonService.Role;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    private final StorageService storageService;
    private final CommonService commonService;

    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository, AdminRepository adminRepository, StorageService storageService, CommonService commonService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.storageService = storageService;
        this.commonService = commonService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param payload
     * @param currentUser Only master admin can do this task
     * @return id of new admin
     */
    @Override
    @Transactional
    public Long createAccount(NewAdminRequest payload, UserPrincipal currentUser) throws IOException {
        final String encodedPassword = passwordEncoder.encode(payload.getPassword());

        if (userRepository.existsByEmail(payload.getEmail())) {
            throw new PersistenceException("User has already existing with email " + payload.getEmail());
        }

        AdminEntity admin = new AdminEntity();
        admin.setEmail(payload.getEmail());
        admin.setPassword(encodedPassword);
        admin.setFirstName(payload.getFirstName());
        admin.setLastName(payload.getLastName());

        for (Long roleId : payload.getRoles()) {
            Role.getByRoleId(roleId).ifPresent(role -> admin.addRole(commonService.getAdminRoles().get(role)));
        }

        Long adminId = adminRepository.save(admin).getUserId();

        storageService.initDirectoriesForUser(adminId, FileAuthor.ADMIN);

        return adminId;
    }

    /**
     * @param payload
     * @param currentUser Only master admin can do this task
     */
    @Override
    @Transactional
    public void updateRole(Long adminId, UpdateAdminRoleRequest payload, UserPrincipal currentUser) {
        AdminEntity admin = adminRepository.findByUserIdFetchRoles(adminId)
            .orElseThrow(() -> new ResourceNotFoundException(AdminEntity.class, "id", adminId));

        if (payload.getRoles() == null || payload.getRoles().isEmpty()) {
            return;
        }

        Set<Long> newRoleIds = payload.getRoles().stream()
            .filter(id -> Role.getByRoleId(id).isPresent())
            .collect(Collectors.toSet());

        Set<Long> existingRoleIds = admin.getRoles().stream()
            .map(item -> item.getRole().getRoleId())
            .collect(Collectors.toSet());

        admin.getRoles().removeIf(item -> !newRoleIds.contains(item.getRole().getRoleId()));
        newRoleIds.removeAll(existingRoleIds);

        // Do add new roles for admin
        if (!newRoleIds.isEmpty()) {
            newRoleIds.forEach(id -> Role.getByRoleId(id).ifPresent(role -> admin.addRole(commonService.getAdminRoles().get(role))));
        }
    }
}
