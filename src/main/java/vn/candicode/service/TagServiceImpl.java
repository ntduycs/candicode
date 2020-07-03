package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.entity.dto.Tag;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.Tags;
import vn.candicode.repository.ChallengeRepository;
import vn.candicode.repository.ContestRepository;
import vn.candicode.repository.TutorialRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TagServiceImpl implements TagService {
    private final ChallengeRepository challengeRepository;
    private final TutorialRepository tutorialRepository;
    private final ContestRepository contestRepository;

    public TagServiceImpl(ChallengeRepository challengeRepository, TutorialRepository tutorialRepository, ContestRepository contestRepository) {
        this.challengeRepository = challengeRepository;
        this.tutorialRepository = tutorialRepository;
        this.contestRepository = contestRepository;
    }

    @Override
    // TODO: Always store tags as lowercase form and return tag in response in Anh Em form
    public Tags getPopularTags(PaginatedRequest payload) {
        List<Tag> challengeTags = challengeRepository.findAllChallengeTags();
        List<Tag> tutorialTags = tutorialRepository.findAllTutorialTags();
        List<Tag> contestTags = contestRepository.findAllContestTags();

        Map<String, Integer> popularChallengeTags = new HashMap<>();

        challengeTags.stream().flatMap(item -> item.getTags().stream()).forEach(tag -> {
            if (popularChallengeTags.containsKey(tag)) {
                int count = popularChallengeTags.get(tag);
                popularChallengeTags.put(tag, count + 1);
            } else {
                popularChallengeTags.put(tag, 1);
            }
        });

        Map<String, Integer> popularTutorialTags = new HashMap<>();

        tutorialTags.stream().flatMap(item -> item.getTags().stream()).forEach(tag -> {
            if (popularTutorialTags.containsKey(tag)) {
                int count = popularTutorialTags.get(tag);
                popularTutorialTags.put(tag, count + 1);
            } else {
                popularTutorialTags.put(tag, 1);
            }
        });

        Map<String, Integer> popularContestTags = new HashMap<>();

        contestTags.stream().flatMap(item -> item.getTags().stream()).forEach(tag -> {
            if (popularContestTags.containsKey(tag)) {
                int count = popularContestTags.get(tag);
                popularContestTags.put(tag, count + 1);
            } else {
                popularContestTags.put(tag, 1);
            }
        });

        Map<String, Integer> popularTags = new HashMap<>(popularChallengeTags);

        popularTutorialTags.forEach((k, v) -> {
            if (popularTags.containsKey(k)) {
                int count = popularTags.get(k);
                popularTags.put(k, count + v);
            } else {
                popularTags.put(k, v);
            }
        });

        popularContestTags.forEach((k, v) -> {
            if (popularTags.containsKey(k)) {
                int count = popularTags.get(k);
                popularTags.put(k, count + v);
            } else {
                popularTags.put(k, v);
            }
        });

        return new Tags(popularTags, payload.getSize());
    }
}
