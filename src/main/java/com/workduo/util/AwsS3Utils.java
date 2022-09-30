package com.workduo.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.workduo.error.global.exception.CustomS3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.workduo.error.global.type.GlobalExceptionType.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Utils {

    private final AmazonS3Client amazonS3Client;

    @Value("${property.s3-base-url}")
    private String BASE_URL;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * member/1/
     * member/1/content/1/
     * group/1
     * gorup/1/content/1/
     * @param multipartFiles
     * @param path
     * @return
     */
    public List<String> uploadFile(List<MultipartFile> multipartFiles, String path) {
        List<String> files = new ArrayList<>();

        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(
                        new PutObjectRequest(
                                bucket,
                                path + fileName,
                                inputStream,
                                objectMetadata
                                )
                                .withCannedAcl(CannedAccessControlList.PublicRead)
                );
            } catch (Exception e) {
                throw new CustomS3Exception(S3_FAIL_UPLOAD);
            }

            files.add(BASE_URL + path + fileName);
        });

        return files;
    }

    public boolean deleteFile(List<String> paths) {

        List<KeyVersion> keyVersions = new ArrayList<>();

        for (String path : paths) {
            keyVersions.add(new KeyVersion(path));
        }

        DeleteObjectsRequest deleteObjectsRequest =
                new DeleteObjectsRequest(bucket).withKeys(keyVersions).withQuiet(false);
        int successfulDeletes = 0;

        try {
            DeleteObjectsResult response = amazonS3Client.deleteObjects(deleteObjectsRequest);

            successfulDeletes = response.getDeletedObjects().size();
            System.out.println(successfulDeletes + " objects successfully deleted.");
        } catch (CustomS3Exception e) {
            throw new CustomS3Exception(FILE_DELETE_FAIL);
        }

        return successfulDeletes == paths.size();
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (Exception e) {
            throw new CustomS3Exception(FILE_EXTENSION_MALFORMED);
        }
    }
    public static String parseAwsUrl(String url){
        if(StringUtils.hasText(url)){
            String[] profileImg = url.split(".com/");
            return profileImg[1];
        }
        return null;
    }
}
