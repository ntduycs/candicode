package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import vn.candicode.services.StorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@Log4j2
public class StorageServiceImpl implements StorageService {
    private static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "Desktop";

    public StorageServiceImpl() throws IOException, SecurityException {
        boolean initSuccess = createRequiredDirectories();

        if (!initSuccess) {
            throw new RuntimeException("One of the required directories was not existing and failed to create");
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void initStudentDirectories(Long studentId) {
        new File(getChallengeDirByUserId(studentId)).mkdirs();
        new File(getSubmissionDirByUserId(studentId)).mkdirs();
        new File(getAvatarDirByUserId(studentId)).mkdirs();
    }

    private String getChallengeRootDir() {
        return ROOT_DIR + File.separator + "challenges";
    }

    private String getSubmissionRootDir() {
        return ROOT_DIR + File.separator + "submissions";
    }

    private String getAvatarRootDir() {
        return ROOT_DIR + File.separator + "avatars";
    }

    private String getBannerRootDir() {
        return ROOT_DIR + File.separator + "banners";
    }

    private String getChallengeDirByUserId(Long userId) {
        return getChallengeRootDir() + File.separator + userId;
    }

    private String getSubmissionDirByUserId(Long userId) {
        return getSubmissionRootDir() + File.separator + userId;
    }

    private String getAvatarDirByUserId(Long userId) {
        return getAvatarRootDir() + File.separator + userId;
    }

    private String getBannerDirByChallengeId(Long challengeId) {
        return getBannerRootDir() + File.separator + challengeId;
    }

    private boolean createRequiredDirectories() {
        boolean initSuccess = true;

        if (!Files.exists(Paths.get(getChallengeRootDir()))) {
            initSuccess = (new File(getChallengeRootDir()).mkdirs());
        }

        if (!Files.exists(Paths.get(getSubmissionRootDir()))) {
            initSuccess = (new File(getSubmissionRootDir()).mkdirs());
        }

        if (!Files.exists(Paths.get(getAvatarRootDir()))) {
            initSuccess = (new File(getAvatarRootDir()).mkdirs());
        }

        if (!Files.exists(Paths.get(getBannerRootDir()))) {
            initSuccess = (new File(getBannerRootDir()).mkdirs());
        }

        return initSuccess;
    }
}
