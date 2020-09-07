import crawler.Crawler;
import crawler.SimpleDownloader;
import crawler.WebCrawler;
import model.Result;

public class Main {
    public static void main(String[] args) {
        try (Crawler crawler = new WebCrawler(
                new SimpleDownloader(),
                100,
                100,
                100,
                "test_dir")) {
            Result result = crawler.download("https://github.com/", 2);
            System.out.println(String.format("We downloaded %d pages, find %d errors occurred",
                    result.getDownloaded().size(), result.getErrors().size()));
        }
    }
}
