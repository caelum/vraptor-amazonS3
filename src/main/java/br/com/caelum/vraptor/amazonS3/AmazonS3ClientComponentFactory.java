package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.environment.Environment;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

@ApplicationScoped
public class AmazonS3ClientComponentFactory {
    
    private final Environment env;
    private AmazonS3Client amazonS3Client;
    private static final String CREDENTIALS_PROPERTY = "br.com.caelum.vraptor.amazonS3.credentials";

    public AmazonS3ClientComponentFactory(Environment env) {
        this.env = env;
    }

    @Produces
    public AmazonS3Client getInstance() {
        return amazonS3Client;
    }
    
    @PostConstruct
    public void create() {
        URL resource = getCredentialsResource();
        try {
            PropertiesCredentials credentials = new PropertiesCredentials(new File(resource.getFile()));
            amazonS3Client = new AmazonS3Client(credentials);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not instantiate amazon S3 client", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not instantiate amazon S3 client", e);
        }
    }

    private URL getCredentialsResource() {
        String propertiesFile = ""; 
        propertiesFile = getOrElse(CREDENTIALS_PROPERTY, "/AwsCredentials.properties");
        URL resource = env.getResource(propertiesFile);
        if (resource == null) {
            throw new IllegalStateException("Could not found your credentials resource, please "
                            + "place it at a source folder with name AwsCredentials.properties or set its path with "
                            + CREDENTIALS_PROPERTY + " in your environment file.");
        }
        return resource;
    }

    private String getOrElse(String key, String defaultValue) {
        try {
            return env.get(key);
        } catch (NoSuchElementException e) {
            return defaultValue;
        }
    }

}
