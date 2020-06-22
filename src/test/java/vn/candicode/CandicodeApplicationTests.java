package vn.candicode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.candicode.core.CodeRunnerService;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;
import vn.candicode.util.DatetimeUtils;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@SpringBootTest
class CandicodeApplicationTests {

    @Autowired
    private CodeRunnerService codeRunner;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetFileExtension() {
        Assertions.assertEquals(FileUtils.getFileExtension("candicode.doc"), "doc");
        Assertions.assertEquals(FileUtils.getFileExtension(".gitignore"), "gitignore");
        Assertions.assertEquals(FileUtils.getFileExtension(""), "");
        Assertions.assertEquals(FileUtils.getFileExtension("a"), "");
        Assertions.assertEquals(FileUtils.getFileExtension("vn/candicode/entity/UserEntity.java"), "java");
    }

    @Test
    void testPathApi() {
        String home = System.getProperty("user.home");
        Path parent = Paths.get(home);
        Path child = parent.resolve("Desktop");

        Assertions.assertEquals(child.toString(), home + File.separator + "Desktop");
        Assertions.assertTrue(child.toFile().exists());
    }

    @Test
    void testParseDatetime() {
        LocalDateTime datetime = LocalDateTime.parse("2020-06-21 14:03:00.000", DatetimeUtils.JSON_DATETIME_FORMAT);

        Assertions.assertEquals(datetime, LocalDateTime.of(2020, 6, 21, 14, 3));
    }

    @Test
    void testCodeRunnerService() {
        File root = new File("/Users/ntduycs/Desktop/Candicode_v3");

        Assertions.assertTrue(root.exists() && root.isDirectory());

        CompileResult compileResult = codeRunner.compile(root);

        System.out.println(compileResult);

        if (compileResult.isCompiled()) {
            ExecutionResult runtimeResult = codeRunner.run(root, 0);

            System.out.println(runtimeResult);
        }

        codeRunner.cleanGarbageFiles(root, "java");
    }
}
