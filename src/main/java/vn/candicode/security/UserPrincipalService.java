package vn.candicode.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.StudentEntity;
import vn.candicode.entity.UserEntity;
import vn.candicode.repository.UserRepository;
import vn.candicode.service.ContestService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ContestService contestService;

    public UserPrincipalService(UserRepository userRepository, StorageService storageService, ContestService contestService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.contestService = contestService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return loadUserByEmail(s);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailFetchRoles(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not fount with email - " + email));

        List<String> roles = user.getRoles().stream().map(item -> item.getRole().getName()).collect(Collectors.toList());

        UserPrincipal principal = UserPrincipal.build(user, roles);

        if (user instanceof StudentEntity) {
            principal.setGainedPoint(((StudentEntity) user).getGainedPoint());
            principal.getIncomingContests().addAll(contestService.getRegisteredIncomingContests(user.getUserId()));
        }

        principal.setAvatar(storageService.resolvePath(user.getAvatar(), FileStorageType.AVATAR, user.getUserId()));

        return principal;
    }
}
