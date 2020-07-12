package vn.candicode.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.UserEntity;
import vn.candicode.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserPrincipalService implements UserDetailsService {
    private final UserRepository userRepository;
    private final StorageService storageService;

    public UserPrincipalService(UserRepository userRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return loadUserByEmail(s);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmailFetchRoles(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not fount with email - " + email));

        List<String> roles = user.getRoles().stream().map(item -> item.getRole().getName()).collect(Collectors.toList());

        UserPrincipal principal = UserPrincipal.build(user, roles);
        principal.setAvatar(storageService.resolvePath(user.getAvatar(), FileStorageType.AVATAR, user.getUserId()));

        return principal;
    }
}
