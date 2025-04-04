package crawler;

import java.util.Set;
import java.util.Stack;

import util.Logger;

/**
 * Implements depth-first search (DFS) crawl strategy
 */
public class DepthFirstCrawler implements CrawlStrategy {
    private Stack<String> urlStack;
    private Set<String> visitedUrls;
    private int maxPages;
    private final Logger logger;
    
    public DepthFirstCrawler() {
        this.logger = new Logger(DepthFirstCrawler.class.getSimpleName());
    }
    
    @Override
    public void initialize(Set<String> frontier, Set<String> visitedUrls, int maxPages) {
        this.urlStack = new Stack<>();
        for (String url : frontier) {
            this.urlStack.push(url);
        }
        this.visitedUrls = visitedUrls;
        this.maxPages = maxPages;
        logger.info("Initialized DFS crawler with " + frontier.size() + " seed URLs");
    }
    
    @Override
    public String next() {
        if (urlStack.isEmpty()) {
            return null;
        }
        return urlStack.pop();
    }
    
    @Override
    public boolean hasNext() {
        return !urlStack.isEmpty() && visitedUrls.size() < maxPages;
    }
    
    @Override
    public void addUrl(String url) {
        if (!visitedUrls.contains(url) && !urlStack.contains(url)) {
            urlStack.push(url);
        }
    }
}
