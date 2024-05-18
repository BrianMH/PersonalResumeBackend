package com.bhenriq.resume_backend.service;

import com.bhenriq.resume_backend.util.SkipRemainingOnFailure;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Testing services normally requires a proper repository backing, but this can't do the same due to a bucket being
 * intrinsically tied to something offered through an online service.
 */
@SpringBootTest
@SkipRemainingOnFailure
public class S3BucketServiceTest {
    @TestConfiguration
    static class S3BucketServiceTestConfiguration {
        @Bean
        public S3BucketService s3BucketService() {
            return new S3BucketService();
        }
    }

    @Autowired
    private S3BucketService s3BucketService;

    private static final String TEST_IMAGE_PNG = "https://www.gstatic.com/webp/gallery3/1.png";
    private static final String TEST_IMAGE_WEBP = "https://www.gstatic.com/webp/gallery/2.webp";
    private static final String TEST_IMAGE_JPG = "https://www.gstatic.com/webp/gallery/3.jpg";
    private static final String TEST_PATH = "test";

    /**
     * Method: checkValidImageUrl
     *
     * Input Space:
     *      UrL: Valid / Invalid
     */
    @Test
    @Order(1)
    public void ValidImageUrlShouldBeValid() {
        List.of(TEST_IMAGE_JPG, TEST_IMAGE_WEBP, TEST_IMAGE_PNG).forEach(link -> {
            Assertions.assertTrue(s3BucketService.checkValidImageUrl(link), "Valid image link must be marked as valid: " + link);
        });
    }

    @Test
    @Order(1)
    public void InvalidImageUrlShouldBeInvalid() {
        List.of("https://www.google.com/", "", "example_string.png").forEach(link -> {
            Assertions.assertFalse(s3BucketService.checkValidImageUrl(link), "Invalid image link must be marked invalid: " + link);
        });
    }

    /**
     * Method: addImageToBucketAsync
     *
     * Because this affects something external, the best we can do is ensure the transfer is properly executed and no
     * strange exceptions are thrown given a known valid URL.
     */
    @Test
    @Order(2)
    public void AddedImageShouldBeInBucket() {
        // first add our object and wait for the return
        String[] toks = TEST_IMAGE_PNG.split("/");
        String filename = toks[toks.length-1];
        Future<PutObjectResponse> res = s3BucketService.addImageToBucketAsync(filename, TEST_IMAGE_PNG, TEST_PATH);

        try {
            // first wait for our object to be placed
            PutObjectResponse trueRes = res.get();

            // and then try and receive the object and compare with the underlying file that is present
            InputStream inObj = s3BucketService.getObjectFromBucket(TEST_PATH + "/" + filename);

            // and in order to ensure the file is correct, we need to make sure that the streams match...
            URL originalImgUrl = new URI(TEST_IMAGE_PNG).toURL();
            HttpURLConnection originalImgConn = (HttpURLConnection) originalImgUrl.openConnection();
            originalImgConn.setRequestMethod("GET");
            InputStream origObj = originalImgConn.getInputStream();

            // and then enforce that our two input streams are equivalent
            Assertions.assertTrue(IOUtils.contentEquals(origObj, inObj), "Saved file must match the original file.");
        } catch (Exception e) {
            // test failure
            Assertions.fail("Add object failed during the test. Make sure to check the bucket for any remaining objects.", e);
        }
    }

    /**
     * Method: removeObjectFromBucketAsync
     *
     * One assumption is that this method ALWAYS runs after a successful add method. If the add method is not successful,
     * then this method cannot properly be evaluated.
     *
     * The following call then ensures that the method responds properly when called with a file known to not exist.
     */
    @Test
    @Order(3)
    public void DeletedValidImageShouldNotBeInBucket() {
        // we can delete our object first
        String[] toks = TEST_IMAGE_PNG.split("/");
        String filename = toks[toks.length-1];
        Future<DeleteObjectResponse> futureRes = s3BucketService.removeObjectFromBucketAsync(TEST_PATH + "/" + filename);

        try {
            DeleteObjectResponse res = futureRes.get();
        } catch (Exception e) {
            Assertions.fail("Found an exception during deletion.", e);
        }

        // and of course we can try and receive the file again and make sure that an exception is thrown on read
        Assertions.assertThrows(Exception.class, () -> s3BucketService.getObjectFromBucket(TEST_PATH + "/" + filename), "Object should not exist post deletion.");
    }

    @Test
    @Order(4)
    public void DeletedInvalidImageShouldThrow() {
        // and of course we can try and receive the file again and make sure that an exception is thrown on read
        Assertions.assertThrows(Exception.class, () -> s3BucketService.getObjectFromBucket(""), "Invalid path should throw an error.");
    }
}
