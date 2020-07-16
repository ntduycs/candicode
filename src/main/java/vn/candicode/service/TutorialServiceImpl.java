package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.core.StorageService;
import vn.candicode.entity.TutorialEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.payload.request.NewTutorialRequest;
import vn.candicode.payload.request.TutorialPaginatedRequest;
import vn.candicode.payload.request.UpdateTutorialRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.TutorialDetails;
import vn.candicode.payload.response.TutorialSummary;
import vn.candicode.repository.CommonRepository;
import vn.candicode.repository.TutorialRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.TutorialBeanUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static vn.candicode.common.FileStorageType.BANNER;

@Service
@Log4j2
public class TutorialServiceImpl implements TutorialService {
    private final TutorialRepository tutorialRepository;
    private final CommonRepository commonRepository;

    private final StorageService storageService;
    private final CommonService commonService;

    public TutorialServiceImpl(TutorialRepository tutorialRepository, CommonRepository commonRepository, StorageService storageService, CommonService commonService) {
        this.tutorialRepository = tutorialRepository;
        this.commonRepository = commonRepository;
        this.storageService = storageService;
        this.commonService = commonService;
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
                bannerPath = storageService.store(payload.getBanner(), BANNER, me.getUserId());
            }
        } catch (IOException e) {
            log.error("Cannot store tutorial banner. Message - {}", e.getLocalizedMessage());
        }

        TutorialEntity tutorial = new TutorialEntity();

        tutorial.setAuthor(me.getEntityRef());
        tutorial.setAuthorName(me.getFullName());
        tutorial.setBanner(storageService.simplifyPath(bannerPath, BANNER, me.getUserId()));
        tutorial.setBrieflyContent(payload.getDescription());
        tutorial.setContent(payload.getContent());
        tutorial.setDislikes(0);
        tutorial.setLikes(0);
        tutorial.setTags(payload.getTags());
        tutorial.setTitle(payload.getTitle());

        if (payload.getCategories() != null) {
            payload.getCategories().forEach(e -> {
                if (commonService.getCategories().containsKey(e)) {
                    tutorial.addCategory(commonService.getCategories().get(e));
                }
            });
        }

        tutorialRepository.save(tutorial);

        return tutorial.getTutorialId();
    }

    /**
     * @param criteria
     * @return paginated list of tutorials
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TutorialSummary> getTutorialList(TutorialPaginatedRequest criteria) {
        Page<TutorialEntity> items = commonRepository.findAll(criteria);

        List<TutorialSummary> summaries = items.map(TutorialBeanUtils::summarize).getContent();

        return PaginatedResponse.<TutorialSummary>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    /**
     * @param criteria
     * @param myId
     * @return paginated list of my tutorials
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TutorialSummary> getMyTutorialList(TutorialPaginatedRequest criteria, Long myId) {
        Page<TutorialEntity> items = commonRepository.findAllByAuthorId(myId, criteria);

        List<TutorialSummary> summaries = items.map(TutorialBeanUtils::summarize).getContent();

        return PaginatedResponse.<TutorialSummary>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    /**
     * @param tutorialId
     * @param me
     * @return details of tutorial with given id
     */
    @Override
    @Transactional(readOnly = true)
    public TutorialDetails getTutorialDetails(Long tutorialId, UserPrincipal me) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialId(tutorialId)
            .orElseThrow(() -> new ResourceNotFoundException(TutorialEntity.class, "id", tutorialId));

        return TutorialBeanUtils.details(tutorial);
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

        if (!tutorial.getAuthor().getUserId().equals(me.getUserId())) {
            throw new BadRequestException("You are not the owner of this tutorial");
        }

        if (payload.getTitle() != null && !tutorial.getTitle().equals(payload.getTitle())) {
            if (tutorialRepository.existsByTitle(payload.getTitle())) {
                throw new PersistenceException("Tutorial has been already exist with tile" + payload.getTitle());
            }
            tutorial.setTitle(payload.getTitle());
        }

        if (payload.getContent() != null) {
            tutorial.setContent(payload.getContent());
        }

        if (payload.getDescription() != null) {
            tutorial.setBrieflyContent(payload.getDescription());
        }


        try {
            if (payload.getBanner() != null && !payload.getBanner().isEmpty()) {
                String bannerPath = storageService.store(payload.getBanner(), BANNER, me.getUserId());
                tutorial.setBanner(storageService.simplifyPath(bannerPath, BANNER, me.getUserId()));
            }
        } catch (IOException e) {
            log.error("Cannot store tutorial banner. Message - {}", e.getLocalizedMessage());
        }

        if (payload.getTags() != null && !payload.getTags().isEmpty()) {
            tutorial.setTags(payload.getTags());
        }

        if (payload.getCategories() != null) {
            Set<String> existingCategories = !tutorial.getCategories().isEmpty()
                ? tutorial.getCategories().stream()
                .map(c -> c.getCategory().getName()).collect(Collectors.toSet())
                : new HashSet<>();

            Set<String> newCategories = payload.getCategories().stream().filter(c -> !existingCategories.contains(c)).collect(Collectors.toSet());

            existingCategories.stream()
                .filter(c -> !payload.getCategories().contains(c)) // Filter existing categories that be included in this update
                .forEach(c -> {
                    if (commonService.getCategories().containsKey(c)) {
                        tutorial.removeCategory(commonService.getCategories().get(c));
                    }
                }); // Remove them

            newCategories.forEach(c -> {
                if (commonService.getCategories().containsKey(c)) {
                    tutorial.addCategory(commonService.getCategories().get(c));
                }
            });
        }

        tutorialRepository.save(tutorial);
    }

    /**
     * @param tutorialId
     * @param me
     */
    @Override
    public void removeTutorial(Long tutorialId, UserPrincipal me) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialIdFetchCategories(tutorialId)
            .orElseThrow(() -> new ResourceNotFoundException(TutorialEntity.class, "id", tutorialId));

        if (!tutorial.getAuthor().getUserId().equals(me.getUserId())) {
            throw new BadRequestException("You are not the owner of this tutorial");
        }

        tutorialRepository.delete(tutorial);
    }
}
