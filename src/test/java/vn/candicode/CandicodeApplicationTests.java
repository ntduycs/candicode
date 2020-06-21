package vn.candicode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootTest
class CandicodeApplicationTests {

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
}
