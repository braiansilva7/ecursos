package com.ecursos.myapp.service;

import com.ecursos.myapp.config.MinIOProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import jakarta.annotation.PostConstruct;
import io.minio.StatObjectArgs;

@Service
public class MinIOService {

    private final Logger log = LoggerFactory.getLogger(MinIOService.class);

    private final MinioClient minioClient;
    private final MinIOProperties minIOProperties;

    public MinIOService(MinioClient minioClient, MinIOProperties minIOProperties) {
        this.minioClient = minioClient;
        this.minIOProperties = minIOProperties;
    }

    @PostConstruct
    public void init() {
        try {
            String bucketName = minIOProperties.getBucketName();
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket {} criado com sucesso", bucketName);
            }
        } catch (Exception e) {
            log.error("Erro ao inicializar MinIO: {}", e.getMessage());
            throw new RuntimeException("Falha ao configurar MinIO", e);
        }
    }

    public boolean fileExists(String bucketName, String objectPath) {
        String targetBucket = (bucketName != null && !bucketName.isEmpty()) ? bucketName : minIOProperties.getBucketName();
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(targetBucket)
                    .object(objectPath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String uploadFile(String bucketName, String objectPath, InputStream inputStream, String contentType, long size)
        throws Exception {
        String targetBucket = bucketName != null && !bucketName.isEmpty() ? bucketName : minIOProperties.getBucketName();
        minioClient.putObject(
            PutObjectArgs.builder().bucket(targetBucket).object(objectPath).stream(inputStream, size, -1).contentType(contentType).build()
        );
        log.info("Arquivo {} enviado para o bucket {}", objectPath, targetBucket);
        return objectPath;
    }

    public String downloadImage(String bucketName, String objectPath) throws Exception {
        String targetBucket = (bucketName != null && !bucketName.isEmpty()) ? bucketName : minIOProperties.getBucketName();

        return minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder().bucket(targetBucket).object(objectPath).method(Method.GET).build()
        );
    }


    public void deleteFile(String bucketName, String objectPath) throws Exception {
        String targetBucket = bucketName != null && !bucketName.isEmpty() ? bucketName : minIOProperties.getBucketName();
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(targetBucket).object(objectPath).build());
        log.info("Arquivo {} removido do bucket {}", objectPath, targetBucket);
    }
}
