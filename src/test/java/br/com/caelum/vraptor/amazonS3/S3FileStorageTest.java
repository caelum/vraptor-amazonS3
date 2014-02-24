package br.com.caelum.vraptor.amazonS3;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.environment.DefaultEnvironment;
import br.com.caelum.vraptor.environment.EnvironmentType;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileStorageTest {
    private DefaultEnvironment env;
    private AmazonS3Client client;
    private S3FileStorage s3FileProvider;
    private String bucketName = "bucket-name";
    
    @Before
    public void setUp() throws IOException {
        env = new DefaultEnvironment(EnvironmentType.TEST);
        client = mock(AmazonS3Client.class);
        s3FileProvider = new S3FileStorage(client);
    }
    
    @Test
    public void shoud_create_bucket_and_send_file_to_s3() throws Exception {
        s3FileProvider.newBucket(bucketName);
        
        URL resource = env.getResource("/sample.txt");
        File file = new File(resource.getFile());
        
        URL uri = s3FileProvider.store(file, bucketName, file.getName());
        
        verify(client).createBucket(bucketName);
        verify(client).putObject(any(PutObjectRequest.class));
        assertEquals(new URL("http://"+bucketName+".s3.amazonaws.com/sample.txt"), uri);
    }
    
    @Test
    public void shoud_send_input_stream() throws Exception {
        URL resource = env.getResource("/sample.txt");
        InputStream is = new FileInputStream(resource.getFile());
        
        URL uri = s3FileProvider.store(is, bucketName, "test.txt", "text/plain");
        
        verify(client).putObject(any(PutObjectRequest.class));
        assertEquals(new URL("http://" + bucketName + ".s3.amazonaws.com/test.txt"), uri);
    }

}
