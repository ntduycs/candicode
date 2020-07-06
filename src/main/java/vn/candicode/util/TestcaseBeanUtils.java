package vn.candicode.util;

import vn.candicode.entity.TestcaseEntity;
import vn.candicode.payload.response.sub.Testcase;

public class TestcaseBeanUtils {
    public static Testcase details(TestcaseEntity entity, boolean displayable) {
        return new Testcase(entity.getTestcaseId(), entity.getInput(), entity.getExpectedOutput(), entity.getHidden(), displayable);
    }
}
