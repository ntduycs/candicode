package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class UserController extends GenericController {
    @Override
    protected String getResourceBasePath() {
        return "users";
    }
}
