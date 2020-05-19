package vn.candicode.commons.storage;

import java.nio.file.Path;

public interface StorageLocation {
    Path getChallengeStorageLocation();

    Path getChallengeSubmissionStorageLocation();

    Path getChallengeStorageLocationByUser(Long userId);

    Path getSubmissionStorageLocationByUser(Long userId);
}
