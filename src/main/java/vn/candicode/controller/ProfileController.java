package vn.candicode.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.candicode.payload.ResponseFactory;
import vn.candicode.payload.response.UserProfile;
import vn.candicode.service.StudentService;

@RestController
public class ProfileController extends Controller {
    private final StudentService studentService;

    public ProfileController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    protected String getPath() {
        return "profiles";
    }

    @GetMapping(value = "/profiles/{id}", produces = {"application/json"})
    public ResponseEntity<?> getUserProfile(@PathVariable("id") Long userId) {
        UserProfile profile = studentService.getStudentProfile(userId);

        return ResponseEntity.ok(ResponseFactory.build(profile));
    }
}
