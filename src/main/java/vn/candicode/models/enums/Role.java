package vn.candicode.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GenericEnum, GrantedAuthority {
    STUDENT, ADMIN, PARTNER, CHALLENGE_CREATOR, POST_CREATOR, MASTER_ADMIN, OPERATING_ADMIN, CONTENT_ADMIN,
    CONTEST_CREATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
