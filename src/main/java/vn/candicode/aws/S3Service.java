package vn.candicode.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.Upload;
import vn.candicode.exception.S3Exception;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface S3Service {
    boolean existsBucket(String bucketName);

    /**
     * @param name of new bucket
     * @return created bucked or empty if any error happened or bucket exists with given name
     */
    Optional<Bucket> createBucket(String name);

    /**
     * @return list of buckets
     * @throws S3Exception if any error happened
     */
    List<Bucket> listBuckets();

    /**
     * This is a non-blocking method that returns immediately. Using the returned object to check the uploading process
     *
     * @see #uploadSafely(String, String, File)
     *
     * @param bucketName bucket's name
     * @param key stored key
     * @param file uploaded file
     */
    Upload upload(String bucketName, String key, File file);

    /**
     * This method guarantees and waits for the uploading completed entirely
     *
     * @see #upload(String, String, File)
     *
     * @param bucketName bucket's name
     * @param key stored keu
     * @param file uploaded file
     */
    void uploadSafely(String bucketName, String key, File file);

    AmazonS3 getS3Client();

    /**
     * @param bucketName bucket's name
     * @param keys deleted keys
     */
    void delete(String bucketName, String[] keys);

    void deleteBucket(String bucketName);

    /**
     * It serves as rename() if <code>srcBucket = destBucket</code>
     * It can also served as move if being used conjunction with delete operation
     *
     * @param srcBucket source bucket name
     * @param srcKey source key
     * @param destBucket destination bucket name
     * @param destKey destination key
     */
    void copy(String srcBucket, String srcKey, String destBucket, String destKey);

    /**
     * @param bucketName bucket's name
     * @param key key
     * @return empty if any error happened
     */
    Optional<S3Object> download(String bucketName, String key);
}
