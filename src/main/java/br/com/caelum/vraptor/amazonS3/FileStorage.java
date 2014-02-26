package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public interface FileStorage {

    URL store(File file, String key);

    URL store(InputStream is, String key, String contentType);

    void newBucket(String name);

    URL urlFor(String bucket, String key);

}
