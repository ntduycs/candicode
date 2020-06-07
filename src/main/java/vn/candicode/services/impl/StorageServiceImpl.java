package vn.candicode.services.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.composite.Node;
import vn.candicode.common.structure.composite.impl.CCDirectory;
import vn.candicode.common.structure.composite.impl.CCFile;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.services.StorageService;
import vn.candicode.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class StorageServiceImpl implements StorageService {
    private static final String ROOT_DIR = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "Candicode";

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

    @Override
    public String storeChallengeBanner(MultipartFile banner, Long id) throws IOException {
        String bannerPath = getBannerRootDir() + File.separator + FileUtils.generateFileName(banner.getOriginalFilename(), id);
        banner.transferTo(new File(bannerPath));

        return bannerPath;
    }

    @Override
    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    public String storeChallengeSourceCode(MultipartFile zipFile, Long id) throws IOException {
        String filenameWoExtension = FileUtils.getFilenameWoExtension(zipFile.getOriginalFilename());
        String challengeDir = getChallengeDirByUserId(id) + File.separator + FileUtils.generateDirectoryName(filenameWoExtension, id);

        File tempFile = new File(getTempDir() + File.separator + zipFile.getOriginalFilename());

        zipFile.transferTo(tempFile);

        FileUtils.unzip(tempFile, new File(challengeDir));

        tempFile.delete();

        return challengeDir;
    }

    @Override
    public List<Node> getDirectoryTree(String dirPath) {
        File dir = new File(dirPath);

        if (dir.exists() && dir.isDirectory()) {
            CCDirectory root = new CCDirectory(dir.getName(), dir.getPath());

            File[] subFiles = dir.listFiles();

            if (subFiles != null) {
                getSubDirectoryTree(subFiles, 0, root);
            }

            return root.getChildren();
        }

        return null;
    }

    private void getSubDirectoryTree(File[] files, int numTraversedFiles, CCDirectory root) {
        if (numTraversedFiles == files.length) {
            return;
        }

        File currentFile = files[numTraversedFiles];

        String filename = currentFile.getName();
        String filepath = currentFile.getPath();

        if (files[numTraversedFiles].isDirectory()) {
            CCDirectory node = new CCDirectory(filename, filepath);
            File[] subFiles = currentFile.listFiles();
            if (subFiles != null) {
                getSubDirectoryTree(subFiles, 0, node);
            }
            root.add(node);
        } else {
            root.add(new CCFile(filename, filepath));
        }

        // Continue to parse next file
        getSubDirectoryTree(files, ++numTraversedFiles, root);
    }

    @Override
    public String cleanPath(String path, Factor factor, Object... others) {
        Map<String, Object> parameters = new HashMap<>();

        if (others.length == 1) {
            parameters.put("userId", others[0]);
        } else if (others.length == 2) {
            parameters.put("userId", others[0]);
            parameters.put("dir", others[1]);
        }

        String eliminatedPath = getEliminatedPath(factor, parameters);

        return path.substring(eliminatedPath.length() - 1);
    }

    @Override
    public String getNonImplementedPathByAuthorAndConfig(Long authorId, ChallengeConfigEntity config) {
        return getChallengeDirByUserId(authorId) + config.getChallengeDir() + config.getNonImplementedPath();
    }

    @Override
    public String getImplementedPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config) {
        return getSubmissionDirByUserId(submitterId) + config.getChallengeDir() + config.getImplementedPath();
    }

    @Override
    public String getTestcaseInputPathByChallenge(Long challengeId) {
        return getTestcaseRootDir() + File.separator + FileUtils.getInputTestcaseFileName(challengeId);
    }

    @Override
    public String getTestcaseOutputPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config) {
        return getSubmissionDirPathBySubmitterAndConfig(submitterId, config) + File.separator + FileUtils.OUTPUT_TESTCASE_FILE;
    }

    @Override
    public String getErrorPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config) {
        return getSubmissionDirPathBySubmitterAndConfig(submitterId, config) + File.separator + FileUtils.ERROR_FILE;
    }

    @Override
    public String getChallengeDirPathByChallengeAuthorAndConfig(Long authorId, ChallengeConfigEntity config) {
        return getChallengeDirByUserId(authorId) + config.getChallengeDir();
    }

    @Override
    public String getSubmissionDirPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config) {
        return getSubmissionDirByUserId(submitterId) + config.getChallengeDir();
    }

    private String getTempDir() {
        return ROOT_DIR + File.separator + "temp";
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

    private String getTestcaseRootDir() {
        return ROOT_DIR + File.separator + "testcases";
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

    private String getEliminatedPath(Factor factor, Map<String, Object> parameters) {
        switch (factor) {
            case AVATAR:
                assert parameters.size() == 1;
                return getAvatarDirByUserId((Long) parameters.get("userId"));
            case BANNER:
                return getBannerRootDir();
            case CHALLENGE:
                assert parameters.size() == 2;
                return getChallengeDirByUserId((Long) parameters.get("userId")) + File.separator + parameters.get("dir");
            case SUBMISSION:
                assert parameters.size() == 2;
                return getSubmissionDirByUserId((Long) parameters.get("userId")) + File.separator + parameters.get("dir");
            default:
                return "";
        }
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

        if (!Files.exists(Paths.get(getTempDir()))) {
            initSuccess = (new File(getTempDir()).mkdirs());
        }

        if (!Files.exists(Paths.get(getTestcaseRootDir()))) {
            initSuccess = new File(getTestcaseRootDir()).mkdirs();
        }

        return initSuccess;
    }
}
