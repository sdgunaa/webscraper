package crawler;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import util.Logger;

/**
 * Implements breadth-first search (BFS) crawl strategy
 */
public class BreadthFirstCrawler implements CrawlStrategy {
    private Queue<String> urlQueue;
    private Set<String> visitedUrls;
    private int maxPages;
    private final Logger logger;
    
    public BreadthFirstCrawler() {
        this.logger = new Logger(BreadthFirstCrawler.class.getSimpleName());
    }
    
    @Override
    public void initialize(Set<String> frontier, Set<String> visitedUrls, int maxPages) {
        this.urlQueue = new LinkedList<>(frontier);
        this.visitedUrls = visitedUrls;
        this.maxPages = maxPages;
        logger.info("Initialized BFS crawler with " + frontier.size() + " seed URLs");
    }
    
    @Override
    public String next() {
        if (urlQueue.isEmpty()) {
            return null;
        }
        return urlQueue.poll();
    }
    
    @Override
    public boolean hasNext() {
        return !urlQueue.isEmpty() && visitedUrls.size() < maxPages;
    }
    
    @Override
    public void addUrl(String url) {
        if (!visitedUrls.contains(url) && !urlQueue.contains(url)) {
            urlQueue.add(url);
        }
    }
}
