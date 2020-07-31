package vn.candicode.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.google.common.base.Joiner;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vn.candicode.exception.S3Exception;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Log4j2
@Profile("prod")
public class S3ServiceImpl implements S3Service {
    private static final long MULTIPART_UPLOAD_THRESHOLD = 5 * 1024 * 1025;
    private static final int MAX_UPLOAD_THREADS = 10;

    private final AmazonS3 s3Client;
    private final TransferManager transferManager;

    public S3ServiceImpl(@Value("${s3.secret-key}") String secretKey, @Value("${s3.access-key}") String accessKey) {
        this.s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
            .withRegion(Regions.AP_SOUTHEAST_1)
            .build();

        this.transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3Client)
            .withMultipartUploadThreshold(MULTIPART_UPLOAD_THRESHOLD) // 5MB
            .withExecutorFactory(() -> Executors.newFixedThreadPool(MAX_UPLOAD_THREADS))
            .build();
    }

    @Override
    public boolean existsBucket(String bucketName) {
        return s3Client.doesBucketExistV2(bucketName);
    }

    @Override
    public Optional<Bucket> createBucket(String name) {
        try {
            if (s3Client.doesBucketExistV2(name)) {
                log.error("Bucket name has been already in use. Try again with a different Bucket name");
                return Optional.empty();
            }

            return Optional.of(s3Client.createBucket(name));
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Messsage - {} {}", e.getErrorCode(), e.getErrorMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Bucket> listBuckets() {
        try {
            return s3Client.listBuckets();
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
            throw new S3Exception(e.getErrorMessage());
        }
    }

    @Override
    public Upload upload(String bucketName, String destPathOnS3, File storedFile) {
        return transferManager.upload(bucketName, destPathOnS3, storedFile);
    }

    /**
     * This method guarantees and waits for the uploading completed entirely
     *
     * @param bucketName bucket's name
     * @param key        stored keu
     * @param file       uploaded file
     */
    @Override
    public void uploadSafely(String bucketName, String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file);

        Upload uploadProcess = transferManager.upload(putObjectRequest);

        try {
            uploadProcess.waitForCompletion();
            log.info("Upload completed successfully");
        } catch (InterruptedException e) {
            log.error("Upload process has been interrupted accidentally");
            e.printStackTrace();
        } catch (AmazonServiceException e) {
            log.error("Error when processing upload request. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
        } catch (AmazonClientException e) {
            log.error("Error when making request or handling response. Message - {}", e.getMessage());
        }
    }

    @Override
    public AmazonS3 getS3Client() {
        return s3Client;
    }

    @Override
    public void delete(String bucketName, String[] keys) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(keys);

        try {
            s3Client.deleteObjects(deleteObjectsRequest);
        } catch (MultiObjectDeleteException e) {
            log.error("Some object cannot be deleted. Message - {}", Joiner.on("\n").join(
                e.getErrors().stream().map(MultiObjectDeleteException.DeleteError::getMessage).collect(Collectors.toList())));
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        try {
            s3Client.deleteBucket(bucketName);
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
        }
    }

    /**
     * It serves as rename() if <code>srcBucket = destBucket</code>
     * It can also served as move if being used conjunction with delete operation
     *
     * @param srcBucket  source bucket name
     * @param srcKey     source key
     * @param destBucket destination bucket name
     * @param destKey    destination key
     */
    @Override
    public void copy(String srcBucket, String srcKey, String destBucket, String destKey) {
        try {
            s3Client.copyObject(srcBucket, srcKey, destBucket, destKey);
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
        }
    }

    @Override
    public Optional<S3Object> download(String bucketName, String key) {
        try {
            return Optional.of(s3Client.getObject(bucketName, key));
        } catch (AmazonServiceException e) {
            log.error("Unexpected error happened. Message - {} {}", e.getErrorCode(), e.getErrorMessage());
            return Optional.empty();
        }
    }
}
