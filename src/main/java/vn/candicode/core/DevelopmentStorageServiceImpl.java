package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.FileAuthor;
import vn.candicode.common.FileStorageType;
import vn.candicode.payload.response.CCDirectory;
import vn.candicode.payload.response.CCFile;
import vn.candicode.payload.response.CCRegularFile;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileAuthor.STUDENT;

@Service
@Log4j2
@Profile("dev")
public class DevelopmentStorageServiceImpl implements StorageService {
    private static final Path BASE_ROOT = Paths.get(System.getProperty("user.home"), "Desktop", "Candicode");

    public DevelopmentStorageServiceImpl() throws IOException {
        createRequiredDirectories();
    }

    private void createRequiredDirectories() throws IOException, SecurityException {
        try {
            if (!Files.exists(challengeDir())) {
                Files.createDirectories(challengeDir());
            }

            if (!Files.exists(submissionDir())) {
                Files.createDirectories(submissionDir());
            }

            if (!Files.exists(avatarDir())) {
                Files.createDirectories(avatarDir());
            }

            if (!Files.exists(bannerDir())) {
                Files.createDirectories(bannerDir());
            }

            if (!Files.exists(stagingDir())) {
                Files.createDirectories(stagingDir());
            }

        } catch (IOException e) {
            log.error("Cannot init the required directories. Message - {}", e.getLocalizedMessage());
            throw e;
        } catch (SecurityException e) {
            log.error("Not have access to init the required directories. Message - {}", e.getLocalizedMessage());
            throw e;
        }
    }


    @Override
    public String rootDir() {
        return BASE_ROOT.toString();
    }

    /**
     * @param userId
     * @param author
     */
    @Override
    public void initDirectoriesForUser(Long userId, FileAuthor author) throws IOException {
        init(avatarDirFor(userId));
        init(challengeDirFor(userId));
        init(bannerDirFor(userId));
        init(stagingDirFor(userId));

        if (author.equals(STUDENT)) {
            init(submissionDirFor(userId));
        }
    }

    private void init(Path dirPath) throws IOException {
        if (Files.exists(dirPath)) {
            FileUtils.cleanDirectory(dirPath.toFile());
        } else {
            Files.createDirectory(dirPath);
        }
    }

    /**
     * @param userId
     * @return path to challenge dir of user
     */
    @Override
    public Path challengeDirFor(Long userId) {
        return challengeDir().resolve(String.valueOf(userId));
    }

    @Override
    public Path stagingDirFor(Long userId) {
        return stagingDir().resolve(String.valueOf(userId));
    }

    /**
     * @param userId
     * @return path to submission dir of student
     */
    @Override
    public Path submissionDirFor(Long userId) {
        return submissionDir().resolve(String.valueOf(userId));
    }

    /**
     * @param userId
     * @return path to avatar dir of user
     */
    @Override
    public Path avatarDirFor(Long userId) {
        return avatarDir().resolve(String.valueOf(userId));
    }

    /**
     * @param ownerId challengeId or tutorialId or contestId
     * @return path to banner dir of owner
     */
    @Override
    public Path bannerDirFor(Long ownerId) {
        return bannerDir().resolve(String.valueOf(ownerId));
    }

    /**
     * @param file
     * @param type
     * @param owner id of resource's owner
     * @return path to resource or null if <code>type</code> was not recognised
     */
    @Override
    public String store(MultipartFile file, FileStorageType type, Long owner) throws IOException {
        Path path = null;
        switch (type) {
            case SUBMISSION:
            case CHALLENGE:
                break;
            case STAGING:
                path = stagingDirFor(owner).resolve(FileUtils.genDirname(owner, type));
                Path challengeZipFile = path.resolve(file.getOriginalFilename());
                Files.createDirectory(path);
                file.transferTo(challengeZipFile);
                FileUtils.unzip(challengeZipFile.toFile(), path.toFile());
                FileUtils.delete(challengeZipFile.toFile());
                break;
            case AVATAR:
                path = avatarDirFor(owner).resolve(FileUtils.genFilename(owner, type, file.getOriginalFilename()));
                file.transferTo(path);
                break;
            case BANNER:
                path = bannerDirFor(owner).resolve(FileUtils.genFilename(owner, type, file.getOriginalFilename()));
                file.transferTo(path);
                break;
            default:
                path = Paths.get(rootDir());
        }

        return path == null ? null : path.toString();
    }

    @Override
    @Async
    public void delete(String path, FileStorageType type, Long owner) {
        String fullQualifiedPath = resolvePath(path, type, owner);

        FileUtils.delete(new File(fullQualifiedPath));
    }

    /**
     * @param path path to target directory
     * @return lists all contained files and directories inside <code>dir</code> as tree and
     * null if <code>path</code> is not a directory or non-existing
     */
    @Override
    public List<CCFile> parse(String path, String challengeDirname) {
        File dir = new File(path);

        if (dir.exists() && dir.isDirectory()) {

            CCDirectory rootDir = new CCDirectory(dir.getName(), simplifyPath(dir.getPath(), challengeDirname), new ArrayList<>());

            File[] files = dir.listFiles();

            if (files != null) {
                parse(files, 0, rootDir, challengeDirname);
            }

            return rootDir.getChildren();
        }

        return null;
    }

    private String simplifyPath(String path, String root) {
        return path.substring(path.indexOf(root) + root.length());
    }

    private void parse(File[] files, int index, CCDirectory parentDir, String challengeDirname) {
        if (index == files.length) { // Already traversed all files in parentDir entirely
            return;
        }

        File file = files[index];

        String filename = file.getName();
        String filepath = simplifyPath(file.getPath(), challengeDirname);

        if (file.isDirectory()) {
            CCDirectory dir = new CCDirectory(filename, filepath, new ArrayList<>());
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                parse(subFiles, 0, dir, challengeDirname);
            }
            parentDir.getChildren().add(dir);
        } else {
            parentDir.getChildren().add(new CCRegularFile(filename, filepath));
        }

        // Continue to parse next sub-file of parentDir
        parse(files, ++index, parentDir, challengeDirname);
    }

    /**
     * @param fullPath the fully path
     * @return the simplified path with excluding base root
     */
    @Override
    public String simplifyPath(String fullPath, FileStorageType type, Long owner) {
        if (fullPath == null) return null;
        switch (type) {
            case BANNER:
                return bannerDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case CHALLENGE:
                return challengeDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case STAGING:
                return stagingDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case SUBMISSION:
                return submissionDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case AVATAR:
                return avatarDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            default:
                throw new UnsupportedOperationException("Cannot simplify path of a " + type.name());
        }
    }

    /**
     * @param path
     * @param type
     * @param owner
     * @return
     */
    @Override
    public String resolvePath(String path, FileStorageType type, Long owner) {
        if (path == null) return null;
        switch (type) {
            case BANNER:
                return bannerDirFor(owner).resolve(path).toString();
            case CHALLENGE:
                return challengeDirFor(owner).resolve(path).toString();
            case SUBMISSION:
                return submissionDirFor(owner).resolve(path).toString();
            case AVATAR:
                return avatarDirFor(owner).resolve(path).toString();
            case STAGING:
                return stagingDirFor(owner).resolve(path).toString();
            default:
                throw new UnsupportedOperationException("Cannot resolve path of a " + type.name());
        }
    }

    /**
     * @param dirname without path
     * @return owner path of dir
     * @see FileUtils#genDirname(Long, FileStorageType)
     */
    @Override
    public Long getDirOwner(String dirname) {
        return Long.parseLong(dirname.split("-", 3)[2]);
    }
}
