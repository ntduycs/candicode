package vn.candicode.services.v2;


import org.springframework.data.domain.Pageable;
import vn.candicode.payloads.requests.TutorialRequest;
import vn.candicode.payloads.responses.PaginatedResponse;
import vn.candicode.payloads.responses.TutorialDetails;
import vn.candicode.payloads.responses.TutorialSummary;
import vn.candicode.security.UserPrincipal;


public interface TutorialService {
    Long createTutorial(TutorialRequest payload, UserPrincipal user);

    TutorialDetails getTutorialDetails(Long tutorialId);

    PaginatedResponse<TutorialSummary> getTutorialList(Pageable pageable);

    PaginatedResponse<TutorialSummary> getMyTutorialList(Pageable pageable, UserPrincipal user);

    void deleteTutorial(Long tutorialId);
}
