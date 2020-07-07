package vn.candicode.util;

import com.google.common.io.Files;
import lombok.extern.log4j.Log4j2;
import vn.candicode.common.FileOperationResult;
import vn.candicode.common.FileStorageType;

import javax.validation.constraints.NotBlank;
import java.io.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

@Log4j2
public class FileUtils {
    private static final int READ_BUFFER_SIZE = 4096;

    private FileUtils() {
    }

    /**
     * @param filename accepts both full-qualified file name and simple file name
     * @return <ul>
     * <li>No extension - return empty String</li>
     * <li>Only extension - return the String after the dot <i>(e.g. gitignore)</i></li>
     * <li>Null was given - return empty String</li>
     * </ul>
     */
    public static String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }

        return Files.getFileExtension(filename);
    }

    /**
     * This method return the file extension that no aware of that the given file is actually exist or not
     *
     * @param file
     * @return <ul>
     * <li>No extension - return empty String</li>
     * <li>Only extension - return the String after the dot <i>(e.g. gitignore)</i></li>
     * <li>Null was given - return empty String</li>
     * </ul>
     */
    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    /**
     * @param file zip file
     * @param destDir the destination the zip file would be extracted to
     * @return <ul>
     *     <li>SUCCESS - No error happens and file was unzipped successfully</li>
     *     <li>ACCESS_DENIED - If the given file cannot read or some entry cannot be write to destDir</li>
     *     <li>INVALID_FILE_TYPE - If given file is not a compressed zip file</li>
     *     <li>FILE_NOT_EXISTS - If given file is not exist</li>
     *     <li>DIR_NOT_EXISTS - If given destDir is not exist</li>
     *     <li>IO_ERROR - If any other IO error happened</li>
     * </ul>
     */
    public static FileOperationResult unzip(File file, File destDir) {
        if (!isCompressedFile(file)) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        if (!file.exists()) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        if (!destDir.exists()) {
            return FileOperationResult.DIR_NOT_EXIST;
        }

        ZipEntry entry = null;
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(file))) {
            while ((entry = in.getNextEntry()) != null) {
                // Ignore OS-dependent entry that might cause crash in MacOS
                if (entry.getName().startsWith("__MACOSX")) {
                    in.closeEntry();
                    continue;
                }

                final String path = destDir + File.separator + entry.getName();

                if (entry.isDirectory()) {
                    new File(path).mkdirs();
                } else {
                    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(path))) {
                        byte[] buffer = new byte[READ_BUFFER_SIZE];
                        int bytes;
                        while ((bytes = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytes);
                        }
                    } catch (SecurityException e) {
                        log.error("Access denied - Cannot access to write file. Message - {}", e.getLocalizedMessage());
                        return FileOperationResult.ACCESS_DENIED;
                    } catch (IOException e) {
                        log.error("I/O error at entry - {}. Message - {}", entry.getName(), e.getLocalizedMessage());
                        return FileOperationResult.IO_ERROR;
                    }
                }

                in.closeEntry();
            }

            in.closeEntry();
        } catch (ZipException e) {
            log.error("Zip entry error at entry - {}. Message - {}", entry.getName(), e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        } catch (SecurityException e) {
            log.error("Access denied - Cannot access to read file {}. Message - {}", file.getName(), e.getLocalizedMessage());
            return FileOperationResult.ACCESS_DENIED;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }

        log.info("Unzipped file " + file.getName() + " successfully");

        return FileOperationResult.SUCCESS;
    }

    private static boolean isCompressedFile(File file) {
        return "zip".equals(getFileExtension(file));
    }

    /**
     * @param file
     * @return <ul>
     *     <li>Content of file if no error happened</li>
     *     <li><code>null</code> if file is null, not exist or failed to read</li>
     * </ul>
     */
    public static String readFileToString(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }

        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * @param file
     * @return <ul>
     *     <li>Encoded string if no error happened</li>
     *     <li><code>null</code> if file is null, not exist or failed to read/encode</li>
     * </ul>
     */
    public static String encodeFileToString(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }

        try {
            byte[] bytes = org.apache.commons.io.FileUtils.readFileToByteArray(file);
            return Base64.getMimeEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * The destination directory is created if it does not exist. If the destination directory did exist,
     * then this method merges the source with the destination, with the source taking precedence.
     *
     * @param srcDir
     * @param destDir will be created if not currently exist
     * @return <ul>
     *     <li>SUCCESS - if no error happened and copied successfully</li>
     *     <li>DIR_NOT_EXIST - if one of two parameters is null or <code>srcDir</code> is not exist</li>
     *     <li>INVALID_FILE_TYPE - if <code>srcDir</code> is not a directory</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult copyDirectory(File srcDir, File destDir) {
        if (srcDir == null || !srcDir.exists() || destDir == null) {
            return FileOperationResult.DIR_NOT_EXIST;
        }

        if (!srcDir.isDirectory()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.copyDirectory(srcDir, destDir, false);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * The destination directory is created if it does not exist. If the destination directory did exist,
     * then this method merges the source with the destination, with the source taking precedence.
     *
     * @param srcDir
     * @param destDir will be created if not currently exist
     * @return <ul>
     * <li>SUCCESS - if no error happened and copied successfully</li>
     * <li>DIR_NOT_EXIST - if one of two parameters is null or <code>srcDir</code> is not exist</li>
     * <li>INVALID_FILE_TYPE - if <code>srcDir</code> is not a directory</li>
     * <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult copyDirectoryToDirectory(File srcDir, File destDir) {
        if (srcDir == null || !srcDir.exists() || destDir == null) {
            return FileOperationResult.DIR_NOT_EXIST;
        }

        if (!srcDir.isDirectory()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.copyDirectoryToDirectory(srcDir, destDir);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * This method copies the contents of the specified source file to a file of the same name in the specified destination directory.
     * The destination directory is created if it does not exist.
     * If the destination file exists, then this method will overwrite it.
     *
     * @param file
     * @param destDir
     * @return <ul>
     *     <li>SUCCESS - if no error happened and copied successfully</li>
     *     <li>FILE_NOT_EXIST - if <code>file</code> is not exist</li>
     *     <li>INVALID_FILE_TYPE - if <code>file</code> or <code>destDir</code> is null or <code>file</code> is not a regular file</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult copyFileToDirectory(File file, File destDir) {
        if (file == null || !file.isFile() || destDir == null) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        if (!file.exists()) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        try {
            org.apache.commons.io.FileUtils.copyFileToDirectory(file, destDir);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * This method copies the contents of the specified source file to the specified destination file.
     * The directory holding the destination file is created if it does not exist.
     * If the destination file exists, then this method will <b>overwrite</b> it.
     *
     * @param srcFile
     * @param destFile
     * @return <ul>
     *     <li>SUCCESS - if no error happened and copied successfully</li>
     *     <li>FILE_NOT_EXIST - if one of two parameters is null or <code>srcFile</code> is not exist</li>
     *     <li>INVALID_FILE_TYPE - if <code>srcFile</code> is not a regular file</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult copyFileToFile(File srcFile, File destFile) {
        if (srcFile == null || !srcFile.exists() || destFile == null) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        if (!srcFile.isFile()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.copyFile(srcFile, destFile, false);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * @param destFile will be created if not exist
     * @param data
     * @return <ul>
     *     <li>SUCCESS - if no error happened and copied successfully</li>
     *     <li>FILE_NOT_EXIST - if <code>destFile</code> is null</li>
     *     <li>INVALID_FILE_TYPE - if <code>srcFile</code> is not a regular file</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult writeStringToFile(File destFile, String data) {
        if (destFile == null) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        if (destFile.exists() && !destFile.isFile()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.writeStringToFile(destFile, data, "UTF-8", false);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * @param destFile will be created if not exist
     * @param data
     * @return <ul>
     *     <li>SUCCESS - if no error happened and copied successfully</li>
     *     <li>FILE_NOT_EXIST - if <code>destFile</code> is null</li>
     *     <li>INVALID_FILE_TYPE - if <code>srcFile</code> is not a regular file</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult appendStringToFile(File destFile, String data) {
        if (destFile == null) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        if (destFile.exists() && !destFile.isFile()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.writeStringToFile(destFile, data, "UTF-8", true);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }

    /**
     * Generate a new filename in form of <code>timestamp-code-author</code>
     *
     * @param authorId
     * @param type
     * @return generate filename
     */
    public static String genFilename(@NotBlank Long authorId, @NotBlank FileStorageType type, @NotBlank String originalFilename) {
        final String timestamp = LocalDateTime.now().format(DatetimeUtils.FILESYSTEM_DATETIME_FORMAT);

        return timestamp + "-" + type.code + "-" + authorId + "." + getFileExtension(originalFilename);
    }

    /**
     * Generate a new filename in form of <code>timestamp-code-author</code>
     *
     * @param authorId
     * @param type
     * @return generate filename
     */
    public static String genDirname(Long authorId, FileStorageType type) {
        final String timestamp = LocalDateTime.now().format(DatetimeUtils.FILESYSTEM_DATETIME_FORMAT);

        return timestamp + "-" + type.code + "-" + authorId;
    }

    /**
     * This method accepts both regular file and directory. In case of directory, it deletes recursively
     *
     * @param file
     * @return <ul>
     *     <li>SUCCESS - if no error happened and deleted successfully</li>
     *     <li>FILE_NOT_EXIST - if <code>file</code> is not exist</li>
     *     <li>INVALID_FILE_TYPE - if <code>file</code> is null</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult delete(File file) {
        if (file == null) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        if (!file.exists()) {
            return FileOperationResult.FILE_NOT_EXIST;
        }

        boolean success = org.apache.commons.io.FileUtils.deleteQuietly(file);

        if (!success) {
            log.error("Some error happened when delete file - {}", file.getPath());
            return FileOperationResult.IO_ERROR;
        } else {
            return FileOperationResult.SUCCESS;
        }
    }

    /**
     * Returns the size of the specified file or directory in bytes
     *
     * @param file
     * @return the relative file size or -1 if <code>file</code> is null or not exist
     */
    public static long sizeOf(File file) {
        if (file == null || !file.exists()) {
            return -1;
        }

        return org.apache.commons.io.FileUtils.sizeOf(file);
    }

    /**
     * @param dir
     * @param file accepts both regular file and directory
     * @return <code>false</code> if: <ul>
     *     <li><code>dir</code> or <code>file</code> is null</li>
     *     <li><code>dir</code> is not exist or not a directory</li>
     *     <li>I/O error happened while checking files of <code>dir</code></li>
     * </ul>
     */
    public static boolean directoryContains(File dir, File file) {
        if (dir == null || file == null) {
            log.warn("Invalid parameter - " + (dir == null ? "dir" : "file") + " is null");
            return false;
        }

        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Directory is not exist or not a directory");
            return false;
        }

        try {
            return org.apache.commons.io.FileUtils.directoryContains(dir, file);
        } catch (IOException e) {
            log.warn("I/O error. Message - {}", e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * @param dir
     * @return <ul>
     *     <li>SUCCESS - if no error happened and cleaned successfully</li>
     *     <li>FILE_NOT_EXIST - if <code>dir</code> is not exist</li>
     *     <li>INVALID_FILE_TYPE - if <code>dir</code> is null or not a directory</li>
     *     <li>IO_ERROR - if any other errors happened</li>
     * </ul>
     */
    public static FileOperationResult cleanDirectory(File dir) {
        if (dir == null) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        if (!dir.exists()) {
            return FileOperationResult.DIR_NOT_EXIST;
        }

        if (!dir.isDirectory()) {
            return FileOperationResult.INVALID_FILE_TYPE;
        }

        try {
            org.apache.commons.io.FileUtils.cleanDirectory(dir);
            return FileOperationResult.SUCCESS;
        } catch (IOException e) {
            log.error("I/O error. Message - {}", e.getLocalizedMessage());
            return FileOperationResult.IO_ERROR;
        }
    }
}
