package vn.candicode.services;

import vn.candicode.models.User;
import vn.candicode.payloads.requests.SubmissionRequest;
import vn.candicode.payloads.responses.SubmissionResult;

public interface SubmissionService {

    SubmissionResult check(SubmissionRequest request, User user);
}
