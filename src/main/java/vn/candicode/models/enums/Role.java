package vn.candicode.models.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GenericEnum, GrantedAuthority {
    Student, Admin, Partner, ChallengeCreator, PostCreator, ContestCreator, SuperAdmin, OperatingAdmin, ContentAdmin;

    @Override
    public String getAuthority() {
        return name();
    }
}
