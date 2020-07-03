package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.candicode.payload.request.PaginatedRequest;

public class TagController extends Controller {
    @Override
    protected String getPath() {
        return "tags";
    }

    @GetMapping(path = "tags")
    public ResponseEntity<?> getPopularTags(@ModelAttribute PaginatedRequest payload) {
        return null;
    }
}
