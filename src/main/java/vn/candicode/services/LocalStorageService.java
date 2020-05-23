package vn.candicode.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vn.candicode.commons.storage.StorageLocation;
import vn.candicode.configs.AppProperties;
import vn.candicode.exceptions.StorageException;

import java.io.File;

@Service
@Profile({"dev"})
@Log4j2
public class LocalStorageService implements StorageService {
    private final StorageLocation storageLocation;

    private final AppProperties appProperties;

    public LocalStorageService(StorageLocation storageLocation, AppProperties appProperties) {
        this.storageLocation = storageLocation;
        this.appProperties = appProperties;

        try {
            if (!storageLocation.getChallengeStorageLocation().toFile().exists()) {
                storageLocation.getChallengeStorageLocation().toFile().mkdirs();
            }

            if (!storageLocation.getChallengeSubmissionStorageLocation().toFile().exists()) {
                storageLocation.getChallengeSubmissionStorageLocation().toFile().mkdirs();
            }
        } catch (SecurityException e) {
            log.error("\n\nError when creating storage location.\n");
            throw new RuntimeException(e);
        }
    }

    /**
     * Create challenge creation and submission storage location for user based on his id
     *
     * @param userId
     */
    @Override
    public void createUserStorageLocation(Long userId) {
        File challengeStorageLocation = storageLocation.getChallengeStorageLocationByUser(userId).toFile();
        File submissionStorageLocation = storageLocation.getSubmissionStorageLocationByUser(userId).toFile();

        boolean created = true;

        if (!challengeStorageLocation.exists() && !submissionStorageLocation.exists()) {
            created = challengeStorageLocation.mkdirs() && submissionStorageLocation.mkdirs();
        }

        if (!created) {
            throw new StorageException("Error when creating storage location for user with id " + userId);
        }
    }

    /**
     * Delete challenge creation and submission storage location of user based on his id
     *
     * @param userId
     */
    @Override
    public void deleteUserStorageLocation(Long userId) {

    }

    /**
     * @param challengeOwnerId
     * @param challengeTitle
     * @return an File object representing for challenge source directory
     */
    @Override
    public File getChallengeSourceDirectory(Long challengeOwnerId, String challengeTitle) {
        File root = storageLocation.getChallengeStorageLocationByUser(challengeOwnerId).toFile();

        return new File(root, challengeTitle);
    }
}
