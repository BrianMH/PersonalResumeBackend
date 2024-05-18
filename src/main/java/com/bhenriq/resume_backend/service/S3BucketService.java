package com.bhenriq.resume_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import org.apache.commons.io.FilenameUtils;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Unlike the typical repositories present, this service is inherently associated with a pre-determined S3 bucket and
 * uses the AWS API along with the relevant access keys to be able to modify the bucket.
 */
@Service
@Slf4j
public class S3BucketService {
    @Autowired
    private S3Client s3Client;

    @Value("${S3_BUCKET}")
    private String BUCKET_NAME;

    // Our allowed image types. It could be extended, but these seemed like the most pertinent to keep around.
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg",
                                                                 "image/gif",
                                                                 "image/png",
                                                                 "image/webp");

    /**
     * In this case, we need to make sure the input is a valid URL string from a certain set of mime types
     */
    public boolean checkValidImageUrl(String imageUrl) {
        try {
            URL propUrl = new URI(imageUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) propUrl.openConnection();
            conn.setRequestMethod("HEAD");
            conn.connect();
            boolean isValid = ALLOWED_MIME_TYPES.contains(conn.getContentType());
            conn.disconnect();

            return isValid;
        } catch (Exception e) {
            log.error("Failed to assess filename. Is the URL malformed?");
            return false;
        }
    }

    /**
     * Allows deletion from a bucket asynchronously.
     * @param bucketPath the file to delete
     */
    @Async
    public Future<DeleteObjectResponse> removeObjectFromBucketAsync(String bucketPath) {
        try {
            DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(bucketPath).build();
            return CompletableFuture.completedFuture(s3Client.deleteObject(req));
        } catch (S3Exception e) {
            log.error("Encountered S3 exception on deletion: " + e);
            return CompletableFuture.failedFuture(e);   // rethrow on main thread
        }
    }

    /**
     * Attempts to retrieve an object from the given bucket path. This is simply a read operation that is the exact same as
     * the example provided by the AWS page.
     * @param bucketPath the path of the file to retrieve
     * @return the stream representing the received object (the underlying byte[] stream should be converted to a proper format)
     */
    public InputStream getObjectFromBucket(String bucketPath) {
        GetObjectRequest req = GetObjectRequest.builder().bucket(BUCKET_NAME).key(bucketPath).build();
        ResponseBytes<GetObjectResponse> res = s3Client.getObject(req, ResponseTransformer.toBytes());
        byte[] data = res.asByteArray();
        InputStream inStream = new ByteArrayInputStream(data);

        return inStream;
    }

    /**
     * Adds an image to the S3 bucket asynchronously. Note that the URL should be checked prior to this function as
     * there isn't a nice and graceful way to catch exceptions inside of this async function.
     * @param imageUrl the URL containing the image to add to the S3 bucket
     */
    @Async
    public Future<PutObjectResponse> addImageToBucketAsync(String targetFilename, String imageUrl, String bucketPath) {
        try {
            // convert our string url into a proper URL
            URL propUrl = new URI(imageUrl).toURL();

            // before we start, we need to infer the content type via the url
            HttpURLConnection conn = (HttpURLConnection) propUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // acquire metadata along with image stream
            String mimeType = conn.getContentType();
            long contentLength = conn.getContentLengthLong();
            log.debug(String.format("Acquired Image with Content Type: %s", mimeType));
            InputStream imageStream = conn.getInputStream();
            log.debug("Acquired Image with Content Length:" + contentLength);

            // and then package into the RequestBody and submit it to the client
            RequestBody req = RequestBody.fromContentProvider(RequestBody.fromInputStream(imageStream, contentLength).contentStreamProvider(), mimeType);
            PutObjectResponse res = s3Client.putObject(PutObjectRequest.builder().bucket(BUCKET_NAME).key(bucketPath + "/" + targetFilename).build(), req);
            log.debug("Transfer completed for given object: " + bucketPath + "/" + targetFilename);
            return CompletableFuture.completedFuture(res);
        } catch(Exception e) {
            log.error("Encountered exception on s3 client PUT: " + e);
            return CompletableFuture.failedFuture(e);   // rethrow on main thread
        }
    }
}
