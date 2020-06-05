package vn.candicode.services;

import org.springframework.web.multipart.MultipartFile;
import vn.candicode.common.structure.composite.Node;
import vn.candicode.models.ChallengeConfigEntity;
import vn.candicode.models.ChallengeEntity;

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

    String getNonImplementedPathByChallengeAndConfig(ChallengeEntity challenge, ChallengeConfigEntity config);
}
