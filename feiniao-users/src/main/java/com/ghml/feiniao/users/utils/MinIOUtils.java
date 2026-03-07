package com.ghml.feiniao.users.utils;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-31 15:24
 * @description
 */
@Slf4j
public class MinIOUtils {

    // 上传文件到MinIO
    public static String uploadFile(MinioClient minioClient,
                                    MultipartFile file,
                                    String bucket,
                                    String filename) throws Exception {
        // 检查存储桶是否存在，不存在则创建
        if (!minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucket)
                .build())) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucket)
                    .build());
        }

        // 上传文件
        ObjectWriteResponse resp = minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)          // 存储桶名称
                        .object(filename)          // 对象名称（文件名,可以包含目录, test/随机字符串.txt）
                        .stream(file.getInputStream(), file.getSize(), -1)  // 文件流, 文件大小, 每次读取上传大小，-1表示自动
                        .contentType(file.getContentType())  // 文件类型，比如 image/jpeg
                        .build());

        return resp.object();
    }

    // 获取文件url
    public static String getObjectUrl(MinioClient minioClient,
                                      String bucket,
                                      String filename,
                                      Integer expiry) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(filename)
                        .expiry(expiry, TimeUnit.HOURS) // 临时访问有效期
                        .build()
        );
    }

    // 判断对象是否存在
    public static boolean objectExists(MinioClient minioClient,
                                       String bucket,
                                       String filename) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );
            return false;
        } catch (Exception e) {
            log.warn("对象不存在或不可访问: bucket={}, object={}, reason={}", bucket, filename, e.getMessage());
            return true;
        }
    }

}
