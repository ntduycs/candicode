package vn.candicode.services;

import java.io.File;

public interface StorageService {
    /**
     * Create challenge creation and submission storage location for user based on his id
     *
     * @param userId
     */
    void createUserStorageLocation(Long userId);

    /**
     * Delete challenge creation and submission storage location of user based on his id
     *
     * @param userId
     */
    void deleteUserStorageLocation(Long userId);

    /**
     * @param challengeOwnerId
     * @param challengeTitle
     * @return an File object representing for challenge source directory
     */
    File getChallengeSourceDirectory(Long challengeOwnerId, String challengeTitle);
}
