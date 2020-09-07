package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface Downloader {
    InputStream download(String url) throws IOException;

    Reader downloadUTF8(String url) throws IOException;
}
