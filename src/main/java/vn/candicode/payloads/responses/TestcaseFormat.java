package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class TestcaseFormat implements Serializable {
    private int numArgs;
    private List<String> format;

    public TestcaseFormat(List<String> format) {
        this.numArgs = format.size();
        this.format = format;
    }
}
