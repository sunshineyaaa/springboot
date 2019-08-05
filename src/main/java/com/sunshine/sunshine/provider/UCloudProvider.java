package com.sunshine.sunshine.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import com.sunshine.sunshine.exception.CustomizeErrorCode;
import com.sunshine.sunshine.exception.CustomizeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UCloudProvider {
@Value("${ucloud.ufile.public-key}")
    private String publicKey;
@Value("${ucloud.ufile.private-key}")
    private String privateKey;
@Value("${ucloud.ufile.bucket-name}")
    private String bucketName;;
    @Value("${ucloud.ufile.region}")
    private String region;
    @Value("${ucloud.ufile.proxy}")
    private String proxy;
    //授权器
    public String upload(InputStream fileStream,String mimeType,String fileName){
        File file = new File("your file path");
        String generateFileName;
        String [] fileSpliter=fileName.split("\\.");
        if(fileSpliter.length>1){
            generateFileName= UUID.randomUUID().toString()+"."+fileSpliter[fileSpliter.length-1];
        }else {
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
        try {
            ObjectAuthorization objectLocalAuthorization = new UfileObjectLocalAuthorization(publicKey, privateKey);
            ObjectConfig config = new ObjectConfig(region, proxy);
            PutObjectResultBean response = UfileClient.object(objectLocalAuthorization, config)
                    .putObject(fileStream, mimeType)
                    .nameAs(generateFileName)
                    .toBucket(bucketName)
                    .setOnProgressListener((bytesWritten,contentLength)-> {

                    })
                    .execute();
                    if (response!=null && response.getRetCode()==0){
                        String Url=UfileClient.object(objectLocalAuthorization,config)
                                .getDownloadUrlFromPrivateBucket(generateFileName,bucketName,24*60*60*365*10)
                                .createUrl();
                        //这里表示若是我们的
                        return  Url;
                    }
                    else{
                        throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
                        //表示我们问题不存在
                    }
        } catch (UfileClientException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        } catch (UfileServerException e) {
            e.printStackTrace();
            throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
        }
    }
}
