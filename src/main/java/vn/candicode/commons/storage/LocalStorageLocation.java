package vn.candicode.commons.storage;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Profile({"dev"})
public class LocalStorageLocation implements StorageLocation {
    public static final String HOME_DIR = System.getProperty("user.home");

    @Override
    public Path getChallengeStorageLocation() {
        return Paths.get(HOME_DIR, "Desktop", "challenges");
    }

    @Override
    public Path getChallengeSubmissionStorageLocation() {
        return Paths.get(HOME_DIR, "Desktop", "submissions");
    }

    @Override
    public Path getChallengeStorageLocationByUser(Long userId) {
        return Paths.get(HOME_DIR, "Desktop", "challenges", String.valueOf(userId));
    }

    @Override
    public Path getSubmissionStorageLocationByUser(Long userId) {
        return Paths.get(HOME_DIR, "Desktop", "submissions", String.valueOf(userId));
    }
}
