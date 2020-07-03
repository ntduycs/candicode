package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.request.PaginatedRequest;
import vn.candicode.payload.response.Tags;
import vn.candicode.service.TagService;

@RestController
public class TagController extends Controller {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    protected String getPath() {
        return "tags";
    }

    @GetMapping(path = "tags")
    public ResponseEntity<?> getPopularTags(@ModelAttribute PaginatedRequest payload) {
        Tags popularTags = tagService.getPopularTags(payload);

        return ResponseEntity.ok(ResponseFactory.build(popularTags));
    }
}
