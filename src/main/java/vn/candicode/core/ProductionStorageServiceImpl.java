package vn.candicode.core;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.aws.S3Service;
import vn.candicode.common.FileAuthor;
import vn.candicode.common.FileStorageType;
import vn.candicode.payload.response.CCDirectory;
import vn.candicode.payload.response.CCFile;
import vn.candicode.payload.response.CCRegularFile;
import vn.candicode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static vn.candicode.common.FileAuthor.STUDENT;

@Service
@Log4j2
@Profile("prod")
public class ProductionStorageServiceImpl implements StorageService {
    private static final Path EC2_BASE_ROOT = Paths.get("/home/ec2-user");
    private static final Path S3_BASE_ROOT = Paths.get("https://candicode.s3-ap-southeast-1.amazonaws.com");
    private static final String S3_BUCKET = "candicode";

    private final S3Service s3Service;

    public ProductionStorageServiceImpl(S3Service s3Service) throws IOException {
        this.s3Service = s3Service;

        createRequiredDirectories();
    }

    private void createRequiredDirectories() throws IOException {
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
        return EC2_BASE_ROOT.toString();
    }

    /**
     * @param userId
     * @param author
     */
    @Override
    public void initDirectoriesForUser(Long userId, FileAuthor author) throws IOException {
        init(challengeDirFor(userId));
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
     * @return path to resource
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
                String avatarFilename = FileUtils.genFilename(owner, type, file.getOriginalFilename());
                path = avatarDirFor(owner).resolve(avatarFilename);
                File avatar = new File(file.getOriginalFilename());
                avatar.createNewFile();
                FileOutputStream fos = new FileOutputStream(avatar);
                fos.write(file.getBytes());
                fos.close();
                s3Service.upload(S3_BUCKET, String.format("avatars/%d/%s", owner, avatarFilename), avatar);
                break;
            case BANNER:
                String bannerFilename = FileUtils.genFilename(owner, type, file.getOriginalFilename());
                path = bannerDirFor(owner).resolve(bannerFilename);
                File banner = new File(file.getOriginalFilename());
                banner.createNewFile();
                FileOutputStream bannerFos = new FileOutputStream(banner);
                bannerFos.write(file.getBytes());
                bannerFos.close();
                s3Service.upload(S3_BUCKET, String.format("banners/%d/%s", owner, bannerFilename), banner);
                break;
            default:
                path = Paths.get(rootDir());
        }

        return path == null ? null : path.toString();
    }

    @Override
    public void delete(String path, FileStorageType type, Long owner) {
        String fullQualifiedPath = resolvePath(path, type, owner);

        FileUtils.delete(new File(fullQualifiedPath));
    }

    /**
     * @param path              path to target directory
     * @param challengeDirname
     * @return lists all contained files and directories inside <code>dir</code> as tree
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
     * @param type
     * @param owner
     * @return the simplified path with excluding base root
     */
    @Override
    public String simplifyPath(String fullPath, FileStorageType type, Long owner) {
        if (fullPath == null) return null;
        switch (type) {
            case CHALLENGE:
                return challengeDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case STAGING:
                return stagingDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case SUBMISSION:
                return submissionDirFor(owner).relativize(Paths.get(fullPath).toAbsolutePath().normalize()).toString();
            case AVATAR:
                return fullPath.substring(avatarDirFor(owner).toString().length() + 1);
            case BANNER:
                return fullPath.substring(bannerDirFor(owner).toString().length() + 1);
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
                return normalizeUrl(bannerDirFor(owner).resolve(path));
            case AVATAR:
                return normalizeUrl(avatarDirFor(owner).resolve(path));
            case CHALLENGE:
                return challengeDirFor(owner).resolve(path).toString();
            case SUBMISSION:
                return submissionDirFor(owner).resolve(path).toString();
            case STAGING:
                return stagingDirFor(owner).resolve(path).toString();
            default:
                throw new UnsupportedOperationException("Cannot resolve path of a " + type.name());
        }
    }

    private String normalizeUrl(Path url) {
        String s = url.toString();

        return s.startsWith("https://") ? s : s.replace("https:/", "https://");
    }

    @Override
    public Long getDirOwner(String dirname) {
        return Long.parseLong(dirname.split("-", 3)[2]);
    }

    @Override
    public Path avatarDir() {
        return Paths.get(S3_BASE_ROOT.toString(), "avatars");
    }

    @Override
    public Path bannerDir() {
        return Paths.get(S3_BASE_ROOT.toString(), "banners");
    }
}
