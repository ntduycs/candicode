package vn.candicode.payloads.responses;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RemoveTestcasesResult implements Serializable {
    private Integer removedTestcase;
    private Integer remainingTestcases;
}
