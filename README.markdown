# vraptor-amazons3

A VRaptor plugin to ease integration with amazon [S3 service](http://aws.amazon.com/s3/).

## Installing

Add to your pom:
```xml
<dependency>
	<groupId>br.com.caelum.vraptor</groupId>
	<artifactId>vraptor-amazonS3</artifactId>
	<version>4.0.1</version> <!-- check latest version -->
</dependency>
```
		
Or simply copy all jars to your classpath

### Configuring 

To use vraptor-amazonS3, you must configure aws credentials in your
application. You need to create a file named `AwsCredentials.properties`, and
place it in a source folder of your project (`src/main/resources` or
`src/main/resources/production/`, for example). Check [aws
docs](http://docs.aws.amazon.com/AWSImportExport/latest/DG/SaveCredentials.html)
to undestand how to export your credentials.

### Saving files

To save files in your controller, you can inject a `FileStorage` implementation:

```java
@Controller
public class MyController {
    @Inject
    private FileStorage storage;

    public void save(UploadedFile file) {
        URL fileUrl = storage.store(file.getFile(), "fileName", file.getContentType());
        //save the url of the uploaded file...
    }
}

```

If you haven't configured AWS credentials, the plugin will inject and
implementation of FileStorage that saves the files locally, inside the web
content root (recommended for development/debugging purposes only).
