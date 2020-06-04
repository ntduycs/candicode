package vn.candicode.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.candicode.models.UserEntity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@EqualsAndHashCode(of = {"userId", "email", "authorities"})
public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String email;
    @JsonIgnore
    private final String password;
    @JsonIgnore
    private final Boolean enable;
    private final String firstName;
    private final String lastName;

    private final UserEntity entityRef;

    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal from(UserEntity userEntity) {
        List<GrantedAuthority> authorities = userEntity.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());

        return UserPrincipal.builder()
            .userId(userEntity.getUserId())
            .email(userEntity.getEmail())
            .password(userEntity.getPassword())
            .firstName(userEntity.getFirstName())
            .lastName(userEntity.getLastName())
            .entityRef(userEntity)
            .enable(userEntity.getEnable())
            .authorities(authorities)
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
        return enable;
    }
}
