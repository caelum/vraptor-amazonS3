package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileStorage implements FileStorage {

    private final AmazonS3Client amazonS3Client;
	private String bucket;

    public S3FileStorage(AmazonS3Client amazonS3Client, String bucket) {
        this.amazonS3Client = amazonS3Client;
		this.bucket = bucket;
    }

    public URL store(File file, String key) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, file)
            .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);
        return urlFor(bucket, key);
    }
    
    public URL store(InputStream is, String path, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, path, is, metadata)
            .withCannedAcl(CannedAccessControlList.PublicRead);
        amazonS3Client.putObject(putObjectRequest);
        return urlFor(bucket, path);
    }
    
    public void newBucket(String bucket) {
        amazonS3Client.createBucket(bucket);
    }

    public URL urlFor(String bucket, String key) {
        try {
            return new URL("http://" + bucket + ".s3.amazonaws.com/" + key);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
