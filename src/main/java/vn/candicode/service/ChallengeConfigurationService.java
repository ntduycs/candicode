package vn.candicode.service;

import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.security.UserPrincipal;

public interface ChallengeConfigurationService {
    /**
     * Execute the pre-implemented code and do evaluation on existing testcases.
     * If succeed to passed entirely, store this supported language to DB.
     * Otherwise, remove all files related to this operation.
     *
     * <p>Only challenge's owner can do this operation</p>
     *
     * @param challengeId
     * @param payload
     * @param currentUser
     * @return
     */
    SubmissionSummary addSupportedLanguage(Long challengeId, NewChallengeRequest payload, UserPrincipal currentUser);

    /**
     * Delete configuration of challenge by <code>language</code> and all related files
     *
     * @param challengeId
     * @param language    which language that want/need to remove
     * @return true if removed successfully
     */
    Boolean removeSupportedLanguage(Long challengeId, String language);
}
