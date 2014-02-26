package br.com.caelum.vraptor.amazonS3;

import static br.com.caelum.vraptor.amazonS3.AmazonS3ClientFactory.CREDENTIALS_PROPERTY;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.environment.Environment;

import com.amazonaws.services.s3.AmazonS3Client;

@ApplicationScoped
public class FileStorageFactory {
	
	private static final String BUCKET_KEY = "br.com.caelum.vraptor.amazons3.bucket";
	private final Environment env;
	private FileStorage fileStorage;
	private ServletContext context;

	@Deprecated
	FileStorageFactory() {
		this(null, null);
	}

	@Inject
	public FileStorageFactory(Environment env, ServletContext context) {
		this.env = env;
		this.context = context;
	}
	
	@PostConstruct
	public void init() {
		String credentialsFile = env.get(CREDENTIALS_PROPERTY, "/AwsCredentials.properties");
		URL credentials = env.getResource(credentialsFile);
		if (credentials == null) {
			this.fileStorage = new LocalFileStorage(env, context);
		} else {
			AmazonS3ClientFactory factory = new AmazonS3ClientFactory(env);
			factory.create();
			AmazonS3Client client = factory.getInstance();
			String bucket = env.get(BUCKET_KEY);
			this.fileStorage = new S3FileStorage(client, bucket);
		}
	}
	
	@Produces
	public FileStorage getFileStorage() {
		return this.fileStorage;
	}

}
