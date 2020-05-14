package vn.candicode.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements BaseEnum, GrantedAuthority {
    CODER, ADMIN, PARTNER
    ;

    @Override
    public String getAuthority() {
        return toString();
    }
}
