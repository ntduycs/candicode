package vn.candicode.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@Log4j2
public class TestController extends GenericController {
    @PostMapping("/test/unzip")
    ResponseEntity<?> testUnzip(@RequestParam("file") MultipartFile file) throws IOException {
        file.transferTo(Paths.get("/Users/ntduycs/Desktop/test/" + file.getOriginalFilename()));

        File zipFile = new File("/Users/ntduycs/Desktop/test/" + file.getOriginalFilename());

        if (!zipFile.exists()) {
            return ResponseEntity.badRequest().body(Map.of("message", "File not found"));
        }

        FileUtils.unzip(zipFile, new File("/Users/ntduycs/Desktop/test"));

        return ResponseEntity.ok().body(Map.of("message", "Unzipped successfully"));
    }

    @GetMapping("/test/copyDir2Dir")
    ResponseEntity<?> copyDir2Dir() throws IOException {
        FileUtils.copyDir2Dir(
            new File("/Users/ntduycs/Desktop/test/add"),
            new File("/Users/ntduycs/Desktop/test/a"));

        return ResponseEntity.ok().body(Map.of("message", "Copied successfully"));
    }
}
