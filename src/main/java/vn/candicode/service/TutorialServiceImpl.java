package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.StorageService;
import vn.candicode.entity.CategoryEntity;
import vn.candicode.entity.TutorialEntity;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewTutorialRequest;
import vn.candicode.payload.request.UpdateTutorialRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.TutorialDetails;
import vn.candicode.payload.response.TutorialSummary;
import vn.candicode.repository.CategoryRepository;
import vn.candicode.repository.TutorialRepository;
import vn.candicode.security.UserPrincipal;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TutorialServiceImpl implements TutorialService {
    private final TutorialRepository tutorialRepository;

    private final StorageService storageService;

    private final Map<String, CategoryEntity> availableCategories;

    public TutorialServiceImpl(TutorialRepository tutorialRepository, CategoryRepository categoryRepository, StorageService storageService) {
        this.tutorialRepository = tutorialRepository;
        this.storageService = storageService;

        this.availableCategories = categoryRepository.findAll().stream().collect(Collectors.toMap(CategoryEntity::getName, cate -> cate));
    }

    /**
     * @param payload
     * @param me
     * @return id of new tutorial
     */
    @Override
    public Long createTutorial(NewTutorialRequest payload, UserPrincipal me) {
        String bannerPath = null;
        try {
            if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
                bannerPath = storageService.store(payload.getBanner(), FileStorageType.TUTORIAL, me.getUserId());
            }
        } catch (IOException e) {
            log.error("Cannot store tutorial banner. Message - {}", e.getLocalizedMessage());
        }

        TutorialEntity tutorial = new TutorialEntity();

        tutorial.setAuthor(me.getEntityRef());
        tutorial.setBanner(bannerPath);
        tutorial.setBrieflyContent(payload.getDescription());
        tutorial.setContent(payload.getContent());
        tutorial.setDislikes(0);
        tutorial.setLikes(0);
        tutorial.setTags(payload.getTags());
        tutorial.setTitle(payload.getTitle());

        payload.getCategories().forEach(e -> tutorial.addCategory(availableCategories.get(e)));

        tutorialRepository.save(tutorial);

        return tutorial.getTutorialId();
    }

    /**
     * @param pageable
     * @return paginated list of tutorials
     */
    @Override
    public PaginatedResponse<TutorialSummary> getTutorialList(Pageable pageable) {
        return null;
    }

    /**
     * @param pageable
     * @param myId
     * @return paginated list of my tutorials
     */
    @Override
    public PaginatedResponse<TutorialSummary> getMyTutorialList(Pageable pageable, Long myId) {
        return null;
    }

    /**
     * @param tutorialId
     * @param me
     * @return details of tutorial with given id
     */
    @Override
    public TutorialDetails getTutorialDetails(Long tutorialId, UserPrincipal me) {
        return null;
    }

    /**
     * @param tutorialId
     * @param payload
     * @param me
     */
    @Override
    public void updateTutorial(Long tutorialId, UpdateTutorialRequest payload, UserPrincipal me) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialIdFetchCategories(tutorialId)
            .orElseThrow(() -> new ResourceNotFoundException(TutorialEntity.class, "id", tutorialId));

        if (payload.getTitle() != null && !tutorial.getTitle().equals(payload.getTitle())) {
            if (tutorialRepository.existsByTitle(payload.getTitle())) {
                throw new PersistenceException("Tutorial has been already exist with tile" + payload.getTitle());
            }
            tutorial.setTitle(payload.getTitle());
        }

        if (payload.getContent() != null) {
            tutorial.setContent(payload.getContent());
        }

    }

    /**
     * @param tutorialId
     * @param me
     */
    @Override
    public void removeTutorial(Long tutorialId, UserPrincipal me) {

    }
}
