package jyang.deliverydotdot.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.UUID;
import jyang.deliverydotdot.exception.RestApiException;
import jyang.deliverydotdot.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class S3Service {

  private final AmazonS3Client s3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final String REVIEW_FOLDER = "review/";

  private final String STORE_FOLDER = "store/";

  private final String DELIVERY_FOLDER = "delivery/";

  @Transactional
  public String uploadReviewImage(MultipartFile file) {
    return upload(file, REVIEW_FOLDER);
  }

  @Transactional
  public String uploadStoreImage(MultipartFile file) {
    return upload(file, STORE_FOLDER);
  }

  @Transactional
  public String uploadDeliveryImage(MultipartFile file) {
    return upload(file, DELIVERY_FOLDER);
  }

  public String upload(MultipartFile file, String folder) {
    String fileName = folder + UUID.randomUUID() + file.getOriginalFilename();

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {

      s3Client.putObject(
          new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
              .withCannedAcl(CannedAccessControlList.PublicRead)
      );
      return s3Client.getUrl(bucket, fileName).toString();
    } catch (IOException e) {
      log.error("Failed to upload file to S3", e);
      throw new RestApiException(ErrorCode.FILE_UPLOAD_FAILED);
    }
  }

  @Transactional
  public void delete(String url) {
    try {
      s3Client.deleteObject(bucket, getFileNameFromURL(url));
    } catch (Exception e) {
      log.error("Failed to delete file from S3", e);
      throw new RestApiException(ErrorCode.FILE_DELETE_FAILED);
    }
  }

  public String getFileNameFromURL(String url) {
    return url.substring(url.indexOf("com/") + 4);
  }
}
