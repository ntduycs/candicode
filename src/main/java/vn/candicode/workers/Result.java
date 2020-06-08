package vn.candicode.workers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    private boolean compiled;
    private String compileMessage;
}
