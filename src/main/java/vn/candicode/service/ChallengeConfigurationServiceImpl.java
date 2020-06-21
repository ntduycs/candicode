package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.core.StorageService;
import vn.candicode.payload.request.NewChallengeRequest;
import vn.candicode.payload.response.SubmissionSummary;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.security.UserPrincipal;

@Service
@Log4j2
public class ChallengeConfigurationServiceImpl implements ChallengeConfigurationService {
    private final ChallengeRepository challengeRepository;

    private final StorageService storageService;

    public ChallengeConfigurationServiceImpl(ChallengeRepository challengeRepository, StorageService storageService) {
        this.challengeRepository = challengeRepository;
        this.storageService = storageService;
    }

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
    @Override
    public SubmissionSummary addSupportedLanguage(Long challengeId, NewChallengeRequest payload, UserPrincipal currentUser) {
        return null;
    }

    /**
     * Delete configuration of challenge by <code>language</code> and all related files
     *
     * @param challengeId
     * @param language    which language that want/need to remove
     * @return true if removed successfully
     */
    @Override
    public Boolean removeSupportedLanguage(Long challengeId, String language) {
        return null;
    }
}
