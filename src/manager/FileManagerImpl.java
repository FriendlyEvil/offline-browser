package manager;

import crawler.Downloader;

import java.io.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static utils.HTMLUtils.getExpansion;

public class FileManagerImpl implements FileManager {
    private static final String INDEX_FILE_NAME = "index.html";
    private static final String FILES_DIRECTORY = "files";
    private static final String HTML_DIRECTORY = "html";
    private static final String FILE_PATH_TEMPLATE = "%s/%s%s";
    private static final String PATH_TEMPLATE = "%s/%s";

    private final Downloader input;
    private final String rootDir;
    private final Map<String, String> urlToPath = new ConcurrentHashMap<>();
    private final Map<String, String> wait = new ConcurrentHashMap<>();

    public FileManagerImpl(Downloader input, String rootDir) {
        this.input = input;
        this.rootDir = rootDir;
        try {
            new File(String.format(PATH_TEMPLATE, rootDir, FILES_DIRECTORY)).mkdirs();
            new File(String.format(PATH_TEMPLATE, rootDir, HTML_DIRECTORY)).mkdirs();
        } catch (SecurityException e) {
            System.err.println("Can't create directory: " + e.getMessage());
        }
    }

    @Override
    public String addFile(String url) {
        String filename = String.format(FILE_PATH_TEMPLATE, FILES_DIRECTORY, getUniqueName(), getExpansion(url));
        String value = urlToPath.putIfAbsent(url, filename);
        if (value == null) {
            wait.putIfAbsent(url, filename);
            return filename;
        }
        return value;
    }

    @Override
    public void saveFile(String fileUrl) {
        String value = wait.remove(fileUrl);
        if (value != null) {
            save(fileUrl, value);
        }
    }

    @Override
    public String addHTML(String fileUrl) {
        String filepath = String.format(FILE_PATH_TEMPLATE, HTML_DIRECTORY, getUniqueName(), ".html");
        String insertResult = urlToPath.putIfAbsent(fileUrl, filepath);
        return insertResult == null ? filepath : insertResult;
    }

    @Override
    public void addStartPage(String url) {
        urlToPath.putIfAbsent(url, INDEX_FILE_NAME);
    }

    @Override
    public String getFile(String url) {
        return String.format(PATH_TEMPLATE, rootDir, urlToPath.get(url));
    }

    private String getUniqueName() {
        return UUID.randomUUID().toString();
    }

    private void save(String urlFile, String filename) {
        try (InputStream buf = input.download(urlFile)) {
            try (OutputStream pw = new FileOutputStream(String.format(PATH_TEMPLATE, rootDir, filename))) {
                byte[] buffer = new byte[1 << 16];
                int r;
                while ((r = buf.read(buffer)) != -1) {
                    for (int i = 0; i < r; i++) {
                        pw.write(buffer[i]);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: File '" + filename + "' can not be created or changed");
            } catch (SecurityException e) {
                System.out.println(
                        "Error: File '" + filename + "' is forbidden to be created or changed by security manager");
            } catch (IOException e) {
                System.out.println("Error while working with file " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error while working with site " + e.getMessage());
        }
    }
}
