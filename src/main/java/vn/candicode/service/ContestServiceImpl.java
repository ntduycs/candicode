package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.candicode.core.StorageService;
import vn.candicode.entity.ContestEntity;
import vn.candicode.exception.BadRequestException;
import vn.candicode.exception.PersistenceException;
import vn.candicode.exception.ResourceNotFoundException;
import vn.candicode.exception.StorageException;
import vn.candicode.payload.request.ContestPaginatedRequest;
import vn.candicode.payload.request.NewContestRequest;
import vn.candicode.payload.request.UpdateContestRequest;
import vn.candicode.payload.response.ContestDetails;
import vn.candicode.payload.response.ContestSummary;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.repository.CommonRepository;
import vn.candicode.repository.ContestRegistrationRepository;
import vn.candicode.repository.ContestRepository;
import vn.candicode.security.UserPrincipal;
import vn.candicode.util.ContestBeanUtils;
import vn.candicode.util.DatetimeUtils;

import javax.persistence.EntityExistsException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static vn.candicode.common.FileStorageType.BANNER;

@Service
@Log4j2
public class ContestServiceImpl implements ContestService {
    private final ContestRepository contestRepository;
    private final CommonRepository commonRepository;
    private final ContestRegistrationRepository contestRegistrationRepository;

    private final StorageService storageService;

    public ContestServiceImpl(ContestRepository contestRepository, CommonRepository commonRepository, ContestRegistrationRepository contestRegistrationRepository, StorageService storageService) {
        this.contestRepository = contestRepository;
        this.commonRepository = commonRepository;
        this.contestRegistrationRepository = contestRegistrationRepository;
        this.storageService = storageService;
    }

    /**
     * @param payload
     * @param author
     * @return id of new contest
     */
    @Override
    public Long createContest(NewContestRequest payload, UserPrincipal author) {
        try {
            String bannerPath;

            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.store(payload.getBanner(), BANNER, author.getUserId());
            }

            ContestEntity contest = new ContestEntity();

            contest.setTitle(payload.getTitle());
            contest.setBanner(bannerPath);
            contest.setAuthor(author.getEntityRef());
            contest.setAuthorName(author.getFullName());
            contest.setDescription(payload.getDescription());
            contest.setMaxRegister(payload.getMaxRegister());
            contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.JSON_DATETIME_FORMAT));
            contest.setTags(payload.getTags());
            contest.setContent(payload.getContent());
            contest.setAvailable(false); // a contest that has no round was considered as not available

            contestRepository.save(contest);

            return contest.getContestId();
        } catch (IOException e) {
            log.error("I/O error - cannot store contest banner. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        } catch (EntityExistsException e) {
            log.error("Entity has already existing. Message - {}", e.getLocalizedMessage());
            throw new PersistenceException(e.getLocalizedMessage());
        }
    }

    /**
     * @param contestId
     * @param payload
     * @param author
     */
    @Override
    @Transactional
    public void updateContest(Long contestId, UpdateContestRequest payload, UserPrincipal author) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

        if (!contest.getAuthor().getUserId().equals(author.getUserId())) {
            throw new BadRequestException("You are not the owner of this contest");
        }

        String bannerPath;
        try {
            if (payload.getBanner() == null || payload.getBanner().isEmpty()) {
                bannerPath = null;
            } else {
                bannerPath = storageService.store(payload.getBanner(), BANNER, author.getUserId());
            }
        } catch (IOException e) {
            log.error("I/O error - cannot store contest banner. Message - {}", e.getLocalizedMessage());
            throw new StorageException(e.getLocalizedMessage());
        }

        if (!contest.getTitle().equals(payload.getTitle())) {
            if (contestRepository.existsByTitle(payload.getTitle())) {
                throw new PersistenceException("Challenge has been already exist with tile" + payload.getTitle());
            } else {
                contest.setTitle(payload.getTitle());
            }
        }

        if (bannerPath != null) {
            contest.setBanner(bannerPath);
        }

        contest.setDescription(payload.getDescription());
        contest.setMaxRegister(payload.getMaxRegister());
        contest.setRegistrationDeadline(LocalDateTime.parse(payload.getRegistrationDeadline(), DatetimeUtils.JSON_DATETIME_FORMAT));
        contest.setTags(payload.getTags());
        contest.setContent(payload.getContent());

        contestRepository.save(contest);
    }

    /**
     * Softly delete
     *
     * @param contestId
     * @param me
     */
    @Override
    @Transactional
    public void removeContest(Long contestId, UserPrincipal me) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

        if (!contest.getAuthor().getUserId().equals(me.getUserId())) {
            throw new BadRequestException("You are not the owner of this contest");
        }

        contest.setDeleted(true);
    }

    /**
     * @param payload
     * @return paginated list of contests
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ContestSummary> getContestList(ContestPaginatedRequest payload, boolean isAdmin) {
        Page<ContestEntity> items = commonRepository.findAll(payload, isAdmin);

        List<ContestSummary> summaries = items.map(ContestBeanUtils::summarize).getContent();

        return PaginatedResponse.<ContestSummary>builder()
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
     * @param payload
     * @param myId
     * @return paginated list of my contests
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<ContestSummary> getMyContestList(ContestPaginatedRequest payload, Long myId) {
        Page<ContestEntity> items = commonRepository.findAllByAuthorId(myId, payload);

        List<ContestSummary> summaries = items.map(ContestBeanUtils::summarize).getContent();

        return PaginatedResponse.<ContestSummary>builder()
            .first(items.isFirst())
            .last(items.isLast())
            .page(items.getNumber())
            .size(items.getSize())
            .totalElements(items.getTotalElements())
            .totalPages(items.getTotalPages())
            .items(summaries)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ContestDetails getContestDetails(Long contestId) {
        ContestEntity contest = contestRepository.findByContestId(contestId)
            .orElseThrow(() -> new ResourceNotFoundException(ContestEntity.class, "id", contestId));

        ContestDetails contestDetails = ContestBeanUtils.details(contest);

        UserPrincipal me;

        try {
            me = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (ClassCastException e) {
            me = null;
        }

        if (me == null) {
            contestDetails.setEnrolled(false);
        } else {
            contestDetails.setEnrolled(contestRegistrationRepository.findByContestIdAndStudentId(contestId, me.getUserId()) != null);
        }

        return contestDetails;
    }
}
