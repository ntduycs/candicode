package vn.candicode.payload.response.sub;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TestcaseFormat implements Serializable {
    private int numArgs;
    private List<String> format;

    public TestcaseFormat(List<String> format) {
        if (format == null) {
            this.numArgs = 0;
            this.format = new ArrayList<>();
        } else {
            this.numArgs = format.size();
            this.format = format;
        }
    }
}
