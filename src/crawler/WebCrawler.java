package crawler;

import manager.FileManager;
import manager.FileManagerImpl;
import model.Document;
import model.Result;
import parser.HTMLParser;
import model.Tag;
import parser.TagProcessor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Runnable;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentHashMap;

import static utils.HTMLUtils.getHost;


public class WebCrawler implements Crawler {
    private final Downloader downloader;
    private final ExecutorService downloadService;
    private final ExecutorService extractService;
    private final ConcurrentHashMap<String, HostLimiter> hostLimitation;
    private final int perHost;
    private final FileManager fileManager;

    private class HostLimiter {
        private int amount;
        private final Queue<Runnable> scheduled;

        HostLimiter() {
            amount = 0;
            scheduled = new ArrayDeque<>();
        }

        private synchronized void add(final Runnable runnable) {
            if (amount < perHost) {
                ++amount;
                downloadService.submit(runnable);
            } else {
                scheduled.add(runnable);
            }
        }

        private synchronized void next() {
            final Runnable runnable = scheduled.poll();
            if (runnable == null) {
                --amount;
            } else {
                downloadService.submit(runnable);
            }
        }
    }

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost, String dir) {
        downloadService = Executors.newFixedThreadPool(downloaders);
        extractService = Executors.newFixedThreadPool(extractors);
        this.downloader = downloader;
        this.perHost = perHost;
        this.fileManager = new FileManagerImpl(downloader, dir);
        hostLimitation = new ConcurrentHashMap<>();
    }

    private void recursionStep(final String url, final int depth, final Set<String> processed, final Map<String, IOException> failed, final Phaser phaser) {
        String host;
        try {
            host = getHost(url);
        } catch (MalformedURLException e) {
            failed.put(url, e);
            return;
        }

        HostLimiter hostLimiter = hostLimitation.computeIfAbsent(host, x -> new HostLimiter());
        phaser.register();
        hostLimiter.add(() -> {
            try (PrintWriter writer = new PrintWriter(fileManager.getFile(url), StandardCharsets.UTF_8)) {
                System.out.println("Start parse: " + url);

                Tag page = new HTMLParser(downloader.downloadUTF8(url)).parse();
                Document document = new TagProcessor(url, page, fileManager).processAndSave(writer);

                document.getFiles().forEach(fileManager::saveFile);

                if (depth > 1) {
                    phaser.register();
                    extractService.submit(() -> {
                        try {
                            document.getPages().stream().filter(str -> {
                                try {
                                    return getHost(url).equals(host);
                                } catch (MalformedURLException e) {
                                    return false;
                                }
                            }).filter(processed::add)
                                    .forEach(link -> recursionStep(link, depth - 1, processed, failed, phaser));
                        } finally {
                            phaser.arrive();
                        }
                    });
                }
            } catch (IOException e) {
                failed.put(url, e);
            } finally {
                phaser.arrive();
                hostLimiter.next();
            }
        });
    }

    @Override
    public Result download(String url, int depth) {
        final Set<String> res = new HashSet<>();
        final Map<String, IOException> failed = new HashMap<>();

        final Phaser phaser = new Phaser(1);
        res.add(url);
        fileManager.addStartPage(url);
        recursionStep(url, depth, res, failed, phaser);
        phaser.arriveAndAwaitAdvance();

        res.removeAll(failed.keySet());
        return new Result(new ArrayList<>(res), failed);
    }

    @Override
    public void close() {
        downloadService.shutdown();
        extractService.shutdown();
    }
}