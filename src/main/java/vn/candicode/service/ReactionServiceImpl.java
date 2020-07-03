package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.common.ReactionSubject;
import vn.candicode.entity.ChallengeEntity;
import vn.candicode.entity.TutorialEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.ReactionRequest;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.TutorialRepository;
import vn.candicode.security.UserPrincipal;

import static vn.candicode.common.ReactionSubject.CHALLENGE;
import static vn.candicode.common.ReactionSubject.TUTORIAL;

@Service
@Log4j2
public class ReactionServiceImpl implements ReactionService {
    private final ChallengeRepository challengeRepository;
    private final TutorialRepository tutorialRepository;

    public ReactionServiceImpl(ChallengeRepository challengeRepository, TutorialRepository tutorialRepository) {
        this.challengeRepository = challengeRepository;
        this.tutorialRepository = tutorialRepository;
    }

    @Override
    @Transactional
    public void storeReaction(ReactionRequest payload, UserPrincipal me) {
        if (payload.getType().equals(CHALLENGE)) {
            ChallengeEntity challenge = challengeRepository.findByChallengeId(payload.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ChallengeEntity.class, "id", payload.getId()));

            if (payload.getLike()) {
                challenge.setLikes(challenge.getLikes() + 1);
            } else {
                challenge.setDislikes(challenge.getDislikes() + 1);
            }
        } else if (payload.getType().equals(TUTORIAL)) {
            TutorialEntity tutorial = tutorialRepository.findByTutorialId(payload.getId())
                .orElseThrow(() -> new ResourceNotFoundException(TutorialEntity.class, "id", payload.getId()));

            if (payload.getLike()) {
                tutorial.setLikes(tutorial.getLikes() + 1);
            } else {
                tutorial.setDislikes(tutorial.getDislikes() + 1);
            }
        } else {
            throw new BadRequestException("Reaction type must be challenge or tutorial");
        }
    }
}
