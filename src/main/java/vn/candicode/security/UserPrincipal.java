package vn.candicode.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.candicode.entity.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@JsonIgnoreProperties({
    "username", "password", "enabled", "entityRef", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "authorities"
})
public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String email;
    private final String password;
    private final Boolean enabled;
    private final String firstName;
    private final String lastName;
    private final String fullName;
    private final List<String> roles;

    private final Collection<? extends GrantedAuthority> authorities;

    private final UserEntity entityRef;

    public static UserPrincipal build(UserEntity user, List<String> roles) {
        return UserPrincipal.builder()
            .userId(user.getUserId())
            .email(user.getEmail())
            .password(user.getPassword())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .fullName(user.getFirstName() + " " + user.getLastName())
            .enabled(user.isEnabled())
            .entityRef(user)
            .authorities(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
            .roles(roles)
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
