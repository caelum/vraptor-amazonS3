package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import br.com.caelum.vraptor.ioc.Component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Component
public class S3FileStorage implements FileStorage {

    private final AmazonS3Client amazonS3Client;

    public S3FileStorage(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public URL store(File file, String bucket, String key) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file)
            .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);
        return urlFor(bucket, key);
    }
    
    public URL store(InputStream is, String bucket, String key, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, is, metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);
        return urlFor(bucket, key);
    }
    
    public void newBucket(String bucket) {
        amazonS3Client.createBucket(bucket);
    }

    private URL urlFor(String bucket, String key) {
        try {
            return new URL("http://" + bucket + ".s3.amazonaws.com/" + key);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
