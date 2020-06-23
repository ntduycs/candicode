package vn.candicode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.candicode.common.FileStorageType;
import vn.candicode.core.CodeRunnerService;
import vn.candicode.core.CompileResult;
import vn.candicode.core.ExecutionResult;
import vn.candicode.core.StorageService;
import vn.candicode.util.DatetimeUtils;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
class CandicodeApplicationTests {

    @Autowired
    private CodeRunnerService codeRunner;

    @Autowired
    private StorageService storageService;

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

        CompileResult compileResult = codeRunner.compile(root, "java");

        System.out.println(compileResult);

        if (compileResult.isCompiled()) {
            ExecutionResult runtimeResult = codeRunner.run(root, 0, "java");

            System.out.println(runtimeResult);
        }

        codeRunner.cleanGarbageFiles(root, "java");
    }

    @Test
    void testSimplifyPath() {
        // Outside base dir
        String outside = storageService.simplifyPath("/Users/ntduycs/Desktop/Candicode_v3", FileStorageType.CHALLENGE, 1L);

        Assertions.assertEquals("../../../Candicode_v3", outside);

        // Inside base dir
        String inside = storageService.simplifyPath("/Users/ntduycs/Desktop/Candicode/challenges/1/out.txt", FileStorageType.CHALLENGE, 1L);

        Assertions.assertEquals("out.txt", inside);
    }

    @Test
    void testResolvePath() {
        Assertions.assertEquals("/Users/ntduycs/Desktop/Candicode/challenges/1/out.txt", storageService.resolvePath("out.txt", FileStorageType.CHALLENGE, 1L));
    }

    @Test
    void testCompletableFuture() {
        List<CompletableFuture<Void>> tasks = new ArrayList<>();

        List<String> strings = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            CompletableFuture<Void> task = CompletableFuture.supplyAsync(() -> "Hello " + finalI)
                .thenAccept(strings::add);

            tasks.add(task);
        }

        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).thenRun(() -> System.out.println(strings));
    }
}
