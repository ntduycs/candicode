package vn.candicode.service;

import vn.candicode.security.UserPrincipal;

public interface ContestRegistrationService {
    void enrollContest(Long contestId, UserPrincipal me);
}
