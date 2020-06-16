package vn.candicode.services.v2.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.exceptions.EntityNotFoundException;
import vn.candicode.exceptions.FileCannotStoreException;
import vn.candicode.exceptions.PersistenceException;
import vn.candicode.models.TutorialCommentEntity;
import vn.candicode.models.TutorialEntity;
import vn.candicode.models.UserEntity;
import vn.candicode.payloads.requests.TutorialRequest;
import vn.candicode.payloads.responses.Comment;
import vn.candicode.payloads.responses.PaginatedResponse;
import vn.candicode.payloads.responses.TutorialDetails;
import vn.candicode.payloads.responses.TutorialSummary;
import vn.candicode.repositories.TutorialCommentRepository;
import vn.candicode.repositories.TutorialRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.services.v1.StorageService;
import vn.candicode.services.v2.TutorialService;
import vn.candicode.utils.DatetimeUtils;

import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TutorialServiceImpl implements TutorialService {
    private final TutorialRepository tutorialRepository;
    private final TutorialCommentRepository tutorialCommentRepository;

    private final StorageService storageService;

    public TutorialServiceImpl(TutorialRepository tutorialRepository, TutorialCommentRepository tutorialCommentRepository, StorageService storageService) {
        this.tutorialRepository = tutorialRepository;
        this.tutorialCommentRepository = tutorialCommentRepository;
        this.storageService = storageService;
    }

    @Override
    public Long createTutorial(TutorialRequest payload, UserPrincipal currentUser) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.storeChallengeBanner(payload.getBanner(), currentUser.getUserId());
            }

            TutorialEntity tutorial = new TutorialEntity();

            tutorial.setTitle(payload.getTitle());
            tutorial.setContent(payload.getContent());
            tutorial.setDescription(payload.getDescription());
            tutorial.setTags(payload.getTags());
            tutorial.setBanner(bannerPath);
            tutorial.setAuthor(currentUser.getEntityRef());

            tutorialRepository.save(tutorial);

            return tutorial.getTutorialId();
        } catch (IOException e) {
            log.error("I/O Exception. Message - {}", e.getLocalizedMessage());
            throw new FileCannotStoreException(e.getLocalizedMessage());
        } catch (EntityExistsException e) {
            log.error("Entity has already existing. Message - {}", e.getLocalizedMessage());
            throw new PersistenceException(e.getLocalizedMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TutorialDetails getTutorialDetails(Long tutorialId) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialId(tutorialId)
            .orElseThrow(() -> new EntityNotFoundException("Tutorial", "tutorialId", tutorialId));

        List<TutorialCommentEntity> comments = tutorialCommentRepository.findAllByTutorialWithLimit(tutorial, 10);
        Long totalComments = tutorialCommentRepository.countAllByTutorial(tutorial);

        TutorialDetails response = new TutorialDetails();

        response.setTutorialId(tutorialId);
        response.setTitle(tutorial.getTitle());
        response.setTags(tutorial.getTags());
        response.setBanner(tutorial.getBanner());
        response.setContent(tutorial.getContent());
        response.setDescription(tutorial.getDescription());
        response.setNumComments(totalComments);
        response.setLikes(tutorial.getLikes());
        response.setDislikes(tutorial.getDislikes());
        response.setCreatedAt(tutorial.getCreatedAt());
        response.setUpdatedAt(tutorial.getUpdatedAt());
        response.setAuthor(tutorial.getAuthor().getFullName());

        for (TutorialCommentEntity entity : comments) {
            Comment comment = new Comment();
            comment.setAuthor(entity.getAuthor());
            comment.setCommentId(entity.getTutorialCommentId());
            comment.setContent(entity.getContent());
            comment.setCreatedAt(entity.getCreatedAt());
            comment.setUpdatedAt(entity.getUpdatedAt());
            comment.setDislikes(entity.getDislikes());
            comment.setLikes(entity.getLikes());
            comment.setSubjectId(tutorialId);
            response.getComments().add(comment);
        }

        response.setCategories(tutorial.getCategories().stream()
            .map(cate -> cate.getCategory().getText().name())
            .collect(Collectors.toList()));

        return response;
    }

    @Override
    public void deleteTutorial(Long tutorialId) {
        if (tutorialRepository.existsById(tutorialId)) {
            tutorialRepository.deleteById(tutorialId);
        }
    }

    @Override
    public void editTutorial(Long tutorialId, TutorialRequest payload) {
        TutorialEntity tutorial = tutorialRepository.findByTutorialId(tutorialId)
            .orElseThrow(() -> new EntityNotFoundException("Tutorial", "tutorialId", tutorialId));

        tutorial.setTitle(payload.getTitle());
        tutorial.setTags(payload.getTags());
        tutorial.setContent(payload.getContent());
        tutorial.setDescription(payload.getDescription());

        String bannerPath;
        if (payload.getBanner() != null) {
            try {
                bannerPath = storageService.storeTutorialBanner(payload.getBanner(), tutorialId);
                tutorial.setBanner(bannerPath);
            } catch (IOException e) {
                log.error("Cannot store banner for tutorial with id {}. Message - {}", tutorialId, e.getLocalizedMessage());
                throw new FileCannotStoreException(e.getLocalizedMessage());
            }
        }

        tutorialRepository.save(tutorial);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TutorialSummary> getTutorialList(Pageable pageable) {
        Page<TutorialEntity> tutorials = tutorialRepository.findAll(pageable);

        return getTutorialSummaryPaginatedResponse(tutorials, null);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TutorialSummary> getMyTutorialList(Pageable pageable, UserPrincipal user) {
        Page<TutorialEntity> tutorialEntities = tutorialRepository.findAllByAuthor(user.getEntityRef(), pageable);

        return getTutorialSummaryPaginatedResponse(tutorialEntities, user.getEntityRef());
    }

    private PaginatedResponse<TutorialSummary> getTutorialSummaryPaginatedResponse(Page<TutorialEntity> tutorials, UserEntity author) {
        PaginatedResponse<TutorialSummary> response = new PaginatedResponse<>();

        response.setPage(tutorials.getNumber() + 1);
        response.setSize(tutorials.getSize());
        response.setTotalElements(tutorials.getTotalElements());
        response.setTotalPages(tutorials.getTotalPages());
        response.setFirst(tutorials.isFirst());
        response.setLast(tutorials.isLast());

        List<TutorialSummary> items = new ArrayList<>();

        for (TutorialEntity tutorial : tutorials) {
            TutorialSummary tutorialsummary = new TutorialSummary();
            tutorialsummary.setTutorialId(tutorial.getTutorialId());
            tutorialsummary.setDescription(tutorial.getDescription());
            tutorialsummary.setAuthor(author != null ? author.getFullName() : tutorial.getAuthor().getFullName());
            tutorialsummary.setBanner(tutorial.getBanner());
            tutorialsummary.setCreatedAt(tutorial.getCreatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            tutorialsummary.setUpdatedAt(tutorial.getUpdatedAt().format(DatetimeUtils.DEFAULT_DATETIME_FORMAT));
            tutorialsummary.setCategories(tutorial
                .getCategories().stream().map(c -> c.getCategory().getText().name()).collect(Collectors.toList()));
            tutorialsummary.setTitle(tutorial.getTitle());
            tutorialsummary.setNumComments(tutorialCommentRepository.countAllByTutorial(tutorial));
            tutorialsummary.setTags(tutorial.getTags());
            tutorialsummary.setLikes(tutorial.getLikes());
            tutorialsummary.setDislikes(tutorial.getDislikes());

            items.add(tutorialsummary);
        }

        response.setItems(items);

        return response;
    }
}
