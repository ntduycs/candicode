package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.candicode.payload.request.NewTutorialRequest;
import vn.candicode.payload.request.UpdateTutorialRequest;
import vn.candicode.payload.response.PaginatedResponse;
import vn.candicode.payload.response.TutorialDetails;
import vn.candicode.payload.response.TutorialSummary;
import vn.candicode.security.UserPrincipal;

@Service
@Log4j2
public class TutorialServiceImpl implements TutorialService {
    /**
     * @param payload
     * @param me
     * @return id of new tutorial
     */
    @Override
    public Long createTutorial(NewTutorialRequest payload, UserPrincipal me) {
        return null;
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

    }

    /**
     * @param tutorialId
     * @param me
     */
    @Override
    public void removeTutorial(Long tutorialId, UserPrincipal me) {

    }
}
