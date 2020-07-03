package vn.candicode.service;

import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.Tags;

public interface TagService {
    Tags getPopularTags(PaginatedRequest payload);
}
