package vn.candicode.services;

import org.springframework.stereotype.Service;
import vn.candicode.models.Challenge;
import vn.candicode.models.enums.ChallengeLevel;
import vn.candicode.payloads.requests.ChallengeRequest;
import vn.candicode.payloads.responses.ChallengeSummary;
import vn.candicode.repositories.ChallengeRepository;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private final ChallengeRepository repository;

    private final InMemoryService inMemoryService;

    public ChallengeServiceImpl(ChallengeRepository repository, InMemoryService inMemoryService) {
        this.repository = repository;
        this.inMemoryService = inMemoryService;
    }

    @Override
    public ChallengeSummary createChallenge(ChallengeRequest request) {
        ChallengeLevel level = ChallengeLevel.valueOf(request.getLevel().toUpperCase());

        int point = getPointByLevel(level);

        Challenge challenge = new Challenge(request.getTitle(), level, request.getDescription(), point);
        challenge.setBannerPath(request.ge);
        return null;
    }

    private int getPointByLevel(ChallengeLevel level) {
        switch (level) {
            case EASY:
                return 100;
            case MODERATE:
                return 200;
            case HARD:
                return 300;
            default:
                throw new IllegalArgumentException("Challenge level not found");
        }
    }
}
