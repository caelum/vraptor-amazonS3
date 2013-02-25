package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

@Component
@ApplicationScoped
public class AmazonS3ClientComponentFactory implements ComponentFactory<AmazonS3Client> {
    
    private final Environment env;
    private AmazonS3Client amazonS3Client;

    public AmazonS3ClientComponentFactory(Environment env) {
        this.env = env;
    }

    @Override
    public AmazonS3Client getInstance() {
        return amazonS3Client;
    }
    
    @PostConstruct
    public void create() {
        URL resource = env.getResource("/AwsCredentials.properties");
        try {
            PropertiesCredentials credentials = new PropertiesCredentials(new File(resource.getFile()));
            amazonS3Client = new AmazonS3Client(credentials);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
