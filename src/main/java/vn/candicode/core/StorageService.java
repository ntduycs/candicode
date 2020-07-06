package vn.candicode.core;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.FileAuthor;
import vn.candicode.common.FileStorageType;
import vn.candicode.payload.response.CCFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface StorageService {
    String rootDir();

    /**
     * @param author
     */
    void initDirectoriesForUser(Long userId, FileAuthor author) throws IOException;

    /**
     * @param userId
     * @return path to challenge dir of user
     */
    Path challengeDirFor(Long userId);

    Path stagingDirFor(Long userId);

    /**
     * @param userId
     * @return path to submission dir of student
     */
    Path submissionDirFor(Long userId);

    /**
     * @param userId
     * @return path to avatar dir of user
     */
    Path avatarDirFor(Long userId);

    /**
     * @param ownerId challengeId or tutorialId or contestId
     * @return path to banner dir of owner
     */
    Path bannerDirFor(Long ownerId);

    /**
     * @param file
     * @param type
     * @param owner id of resource's owner
     * @return path to resource
     */
    String store(MultipartFile file, FileStorageType type, Long owner) throws IOException;

    void delete(String path, FileStorageType type, Long owner);

    /**
     * @param dir path to target directory
     * @return lists all contained files and directories inside <code>dir</code> as tree
     */
    List<CCFile> parse(String dir, String challengeDirname);

    /**
     * @param fullPath the fully path
     * @return the simplified path with excluding base root
     */
    String simplifyPath(String fullPath, FileStorageType type, Long owner);

    /**
     * @param path
     * @param type
     * @param owner
     * @return
     */
    String resolvePath(String path, FileStorageType type, Long owner);

    Long getDirOwner(String dirname);

    // ==========================================================
    // = DEFAULT METHODS =
    // ==========================================================

    default Path challengeDir() {
        return Paths.get(rootDir(), "challenges");
    }

    default Path submissionDir() {
        return Paths.get(rootDir(), "submissions");
    }

    default Path avatarDir() {
        return Paths.get(rootDir(), "avatars");
    }

    default Path bannerDir() {
        return Paths.get(rootDir(), "banners");
    }

    default Path stagingDir() {
        return Paths.get(rootDir(), "staging");
    }
}
