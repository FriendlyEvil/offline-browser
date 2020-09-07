## Thread-safe offline browser

Crawls html pages to the specified depth and saves all documents for offline viewing. This implementation supports:
+ multi-threaded processing of different pages
+ limit on the number of pages processed
+ limit on the number of pages loaded simultaneously from a single host

### Usage:
```
try (Crawler crawler = new WebCrawler(
                new SimpleDownloader(),
                downloaders,
                extractors,
                perHost,
                directory)) {
    Result result = crawler.download("https://github.com/", depth);
}
```
+ ``downloaders`` - maximum number of simultaneously loaded pages
+ ``extractors`` - maximum number of pages to extract links from
+ ``perHost`` - maximum number of pages loaded simultaneously from a single host
+ ``directory`` - directory for saving files
+ ``depth`` - crawl depth

### Comments:
+ My implementation use own html parser. To avoid errors, you can use existing libraries, such as jsoup.
+ Pages that use dynamic loading of scripts and css may not display correctly.
+ Uploaded files will be saved with random uuid with the old extension