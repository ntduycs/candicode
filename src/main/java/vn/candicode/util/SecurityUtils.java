package vn.candicode.util;

import vn.candicode.entity.ChallengeEntity;
import vn.candicode.security.UserPrincipal;

public class SecurityUtils {
    public static boolean isOwner(UserPrincipal me, ChallengeEntity challenge) {
        return me.getUserId().equals(challenge.getAuthor().getUserId());
    }
}
