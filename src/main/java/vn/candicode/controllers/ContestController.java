package vn.candicode.controllers;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContestController extends GenericController {
    @Override
    protected String getResourceBasePath() {
        return "contests";
    }


}
