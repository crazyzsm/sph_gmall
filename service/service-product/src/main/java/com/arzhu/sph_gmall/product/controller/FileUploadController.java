package com.arzhu.sph_gmall.product.controller;

import com.arzhu.sph_gmall.common.result.Result;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.minio.MinioClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * ClassName: FileUploadController
 * Package: com.arzhu.sph_gmall.product.controller
 * Description:
 *
 * @Author arzhu
 * @Create 2023/11/15 12:46
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/product")
public class FileUploadController {
//    minIo 的 地址
    @Value("${minio.endpointUrl}")
    private String endpointUrl;
    //    minIo 的 账户
    @Value("${minio.accessKey}")
    private String accessKey;
    //    minIo 的 密码
    @Value("${minio.secreKey}")
    private String secreKey;
//    桶名
    @Value("${minio.bucketName}")
    private String bucketName;
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestBody MultipartFile file){
        String url = "";
        String originFileName = file.getOriginalFilename();
        try {
            //        //  准备获取到上传的文件路径！

            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            // MinioClient minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)  // minIO 的地址
                            .credentials(accessKey, secreKey)  // 账户 和 密码
                            .build();
            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if(isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
            //  定义一个文件的名称 : 文件上传的时候，名称不能重复！
            String fileName = originFileName+System.currentTimeMillis()+ UUID.randomUUID().toString();
            // 使用putObject上传一个文件到存储桶中。
            //  minioClient.putObject("asiatrip","asiaphotos.zip", "/home/user/Photos/asiaphotos.zip");
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            //  文件上传之后的路径： http://39.99.159.121:9000/gmall/xxxxxx
            url = endpointUrl+"/"+bucketName+"/"+fileName;
            System.out.println("url:\t"+url);
//        //  将文件上传之后的路径返回给页面！
        }catch (Exception e){
            e.printStackTrace();
            return Result.fail("上传失败");
        }

        return Result.ok(url);

    }
}
