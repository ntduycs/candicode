package vn.candicode.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.Tags;

@Service
@Log4j2
public class TagServiceImpl implements TagService {
    @Override
    public Tags getPopularTags(PaginatedRequest payload) {
        return null;
    }
}
