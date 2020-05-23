package vn.candicode.utils;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.commons.dsa.Component;
import vn.candicode.commons.dsa.Composite;
import vn.candicode.commons.dsa.Leaf;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.exceptions.StorageException;
import vn.candicode.models.User;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@org.springframework.stereotype.Component
@Log4j2
public class FileUtils {
    public enum Type {
        FILE("file"),
        DIRECTORY("directory"),

        ;
        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final StorageLocation storageLocation;

    public FileUtils(StorageLocation storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Component parseDirTree(MultipartFile file, User user) {
        try {
            String destDir = unzip(file, user);
            return parseDirTree(destDir);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new StorageException("Cannot unzip file");
        }
    }

    public String unzip(MultipartFile file, User user) throws IOException {
        // TODO: Replace this by other storage provider
        File destinationDir = storageLocation.getChallengeStorageLocationByUser(user.getId()).toFile();

        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        File challengeDir = new File(destinationDir.getAbsolutePath() + File.separator + Files.getNameWithoutExtension(file.getOriginalFilename()));

        if (!challengeDir.exists()) {
            challengeDir.mkdirs();
        }

        ZipInputStream inputStream = new ZipInputStream(file.getInputStream());
        ZipEntry entry = inputStream.getNextEntry();

        while (entry != null) {
            String filePath = challengeDir + File.separator + entry.getName();

            if (entry.getName().startsWith("__MACOSX")) {
                inputStream.closeEntry();
                entry = inputStream.getNextEntry();
                continue;
            }

            if (!entry.isDirectory()) {
                unzip(inputStream, filePath);
            } else {
                new File(filePath).mkdirs();
            }

            inputStream.closeEntry();
            entry = inputStream.getNextEntry();
        }

        inputStream.closeEntry();
        inputStream.close();

        return challengeDir.getAbsolutePath();
    }

    private void unzip(ZipInputStream inputStream, String filePath) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] buffer = new byte[4096];
        int chunkSize;
        while ((chunkSize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, chunkSize);
        }
        outputStream.close();
    }

    private static Component parseDirTree(String pathToDir) {
        File destinationDir = new File(pathToDir);

        if (destinationDir.exists() && destinationDir.isDirectory()) {
            File[] subFiles = destinationDir.listFiles();

            Composite root = new Composite(destinationDir.getName(), destinationDir.getPath(), Type.DIRECTORY.getName());

            if (subFiles != null) {
                parseSubDirsTree(subFiles, 0, root);
            }

            return root;
        }

        return null;
    }

    private static void parseSubDirsTree(File[] subFiles, int index, Composite root) {
        if (index == subFiles.length) {
            return;
        }

        String fileName = subFiles[index].getName();

        if (subFiles[index].isFile()) {
            root.addChild(new Leaf(fileName.substring(0, fileName.lastIndexOf(".")), subFiles[index].getPath(), Type.FILE.getName()));
        } else {
            Composite subTree = new Composite(fileName, subFiles[index].getPath(), Type.DIRECTORY.getName());
            File[] sFiles = subFiles[index].listFiles();
            if (sFiles != null) {
                parseSubDirsTree(sFiles, 0, subTree);
            }
            root.addChild(subTree);
        }

        parseSubDirsTree(subFiles, ++index, root);
    }

    public String readFile(String path) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(new File(path));
    }
}
