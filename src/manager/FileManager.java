package manager;

public interface FileManager {
    String addFile(String url);

    void saveFile(String url);

    String addHTML(String url);

    void addStartPage(String url);

    String getFile(String url);
}
