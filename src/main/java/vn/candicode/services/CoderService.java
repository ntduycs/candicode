package vn.candicode.services;

import vn.candicode.payloads.requests.RegisterRequest;

public interface CoderService {
    /**
     * @param registerRequest
     * @return ID of new coder account
     */
    Long createNewCoderAccount(RegisterRequest registerRequest);
}
