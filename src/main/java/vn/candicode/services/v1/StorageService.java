package vn.candicode.services.v1;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.composite.Node;
import vn.candicode.models.ChallengeConfigEntity;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    enum Factor {
        CHALLENGE, SUBMISSION, AVATAR, BANNER,
        ;
    }

    void initStudentDirectories(Long studentId);

    String storeChallengeBanner(MultipartFile banner, Long id) throws IOException;

    String storeChallengeSourceCode(MultipartFile zipFile, Long id) throws IOException;

    List<Node> getDirectoryTree(String dirPath);

    String cleanPath(String path, Factor factor, Object... others);

    String getNonImplementedPathByAuthorAndConfig(Long authorId, ChallengeConfigEntity config);

    String getImplementedPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config);

    String getTestcaseInputPathByChallenge(Long challengeId);

    String getTestcaseOutputPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config);

    String getErrorPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config);

    String getChallengeDirPathByChallengeAuthorAndConfig(Long authorId, String challengeDir);

    String getSubmissionDirPathBySubmitterAndConfig(Long submitterId, ChallengeConfigEntity config);
}
