package vn.candicode.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.candicode.models.UserEntity;

import java.util.Collection;

@Builder
@Getter
@EqualsAndHashCode(of = {"userId", "email"})
public class UserPrincipal implements UserDetails {
    private final Long userId;
    private final String email;
    @JsonIgnore
    private final String password;
    @JsonIgnore
    private final Boolean enable;
    private final String firstName;
    private final String lastName;

    @JsonIgnore
    private final UserEntity entityRef;

    private final Collection<? extends GrantedAuthority> roles;

    public static UserPrincipal from(UserEntity userEntity) {
        return UserPrincipal.builder()
                .userId(userEntity.getUserId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .entityRef(userEntity)
                .enable(userEntity.getEnable())
                .roles(userEntity.getRoles())
            .build();
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return enable;
    }
}
