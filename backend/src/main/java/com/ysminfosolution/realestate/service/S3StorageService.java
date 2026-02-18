package com.ysminfosolution.realestate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    public record S3ObjectData(byte[] bytes, String contentType) {
    }

    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    public String uploadFile(String key, MultipartFile file) {
        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading file to S3", e);
        }
    }

    public S3ObjectData downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(request);
            return new S3ObjectData(response.asByteArray(), response.response().contentType());
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file from S3: " + key, e);
        }
    }

    public void deleteProjectDirectoryFromS3(Project project, AppUserDetails user) {

        log.info("\n");
        log.info("Method: deleteProjectDirectoryFromS3");

        String prefix = user.getOrgId() + "/" + project.getProjectName() + "/";

        try {
            // 1. List all files under the prefix
            ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

            if (listRes.contents().isEmpty()) {
                log.info("No objects found under prefix {}", prefix);
                return;
            }

            // 2. Prepare batch delete request
            List<ObjectIdentifier> objectsToDelete = listRes.contents().stream()
                    .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                    .toList();

            DeleteObjectsRequest deleteReq = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder().objects(objectsToDelete).build())
                    .build();

            // 3. Delete all objects
            s3Client.deleteObjects(deleteReq);

            log.info("ROLLBACK STATUS : S3 Directory deleted successfully for projectId : {}",
                    project.getProjectId());

        } catch (Exception e) {
            throw new RuntimeException("Failed to delete S3 directory with prefix: " + prefix, e);
        }
    }

}
