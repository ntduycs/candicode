package vn.candicode.payloads.requests;

import org.springframework.web.multipart.MultipartFile;

public abstract class ChallengeMultipartRequest extends MultipartRequest {
    public abstract String getTitle();

    public abstract String getLevel();

    public abstract String getDescription();

    public abstract MultipartFile getBanner();
}
