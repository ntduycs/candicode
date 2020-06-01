package vn.candicode.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.tika.Tika;
import vn.candicode.exceptions.FileNotFoundException;
import vn.candicode.exceptions.UnsupportedFileTypeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Log4j2
public class FileUtils {
    private FileUtils() {
    }

    private static final int END_OF_STREAM = -1;

    private static final List<String> ZIP_MIME_TYPES = List.of(
        "application/zip",
        "application/octet-stream",
        "application/x-zip-compressed",
        "multipart/x-zip"
    );

    private static final List<String> RAR_MIME_TYPES = List.of(
        "application/x-rar-compressed",
        "application/vnd.rar"
    );

    /**
     * @param file File to check MIME type
     * @return null if file is directory or IO error occurred
     */
    public static String getMimeType(File file) {
        if (file.isDirectory()) {
            return null;
        }

        try {
            return (new Tika()).detect(file);
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean isZipFile(File file) {
        return ZIP_MIME_TYPES.contains(getMimeType(file));
    }

    public static boolean isZipFile(String mimeType) {
        return ZIP_MIME_TYPES.contains(mimeType);
    }

    public static boolean isRarFile(File file) {
        return RAR_MIME_TYPES.contains(getMimeType(file));
    }

    public static boolean isRarFile(String mimeType) {
        return RAR_MIME_TYPES.contains(mimeType);
    }

    public static void unzip(File file, File destinationDirectory) throws IOException {
        String mimeType = getMimeType(file);
        if (mimeType == null || (!isZipFile(mimeType) && !isRarFile(mimeType))) {
            throw new UnsupportedFileTypeException(mimeType, ZIP_MIME_TYPES, RAR_MIME_TYPES);
        }

        if (destinationDirectory.isFile() || !destinationDirectory.exists()) {
            throw new FileNotFoundException("Cannot unzip file. Destination does not exist or is a regular file");
        }

        log.info("Unzipping file " + file.getName() + " ...");

        ZipInputStream in = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry = in.getNextEntry();

        while (entry != null) {
            // Ignore featured entry that might caused crash issue in MacOS
            if (entry.getName().startsWith("__MACOSX")) {
                in.closeEntry();
                entry = in.getNextEntry();
                continue;
            }

            String entryPath = destinationDirectory + File.separator + entry.getName();

            if (entry.isDirectory()) {
                Files.createDirectory(Paths.get(entryPath));
            } else {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(entryPath));
                byte[] readBuffer = new byte[4096];
                int readSizeInBytes = 0;
                while ((readSizeInBytes = in.read(readBuffer)) != END_OF_STREAM) {
                    out.write(readBuffer, 0, readSizeInBytes);
                }
                out.close();
            }

            in.closeEntry();
            entry = in.getNextEntry();
        }

        in.closeEntry();
        in.close();

        log.info("Unzipped file " + file.getName() + " successfully");
    }

    public static String readFileToString(File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file);
    }

    public static String encodeFileToString(File file) throws IOException {
        byte[] fileAsBytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
        return Base64.getEncoder().encodeToString(fileAsBytes);
    }

    /**
     * Copy all sub-files and sub-directories from src to dest directory
     *
     * @throws IOException
     */
    public static void copyDir2Dir(File copiedDir, File destDir) throws IOException {
        Path copiedDirPath = Paths.get(copiedDir.getAbsolutePath());
        Path destDirPath = Paths.get(destDir.getAbsolutePath());

        Files.walk(copiedDirPath)
            .forEach(copiedFilePath -> {
                try {
                    Path destFilePath = destDirPath.resolve(copiedDirPath.relativize(copiedFilePath));
                    Files.copy(copiedFilePath, destFilePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("Error when copying file {}: {}", copiedDirPath.toString(), e.getMessage());
                }
            });
    }
}
