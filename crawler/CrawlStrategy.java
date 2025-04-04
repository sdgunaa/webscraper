package crawler;

import java.util.Set;

/**
 * Interface for different crawling strategies (BFS, DFS)
 */
public interface CrawlStrategy {
    
    /**
     * Initialize the crawl strategy with initial data
     * 
     * @param frontier Set of URLs to crawl
     * @param visitedUrls Set of already visited URLs
     * @param maxPages Maximum number of pages to visit
     */
    void initialize(Set<String> frontier, Set<String> visitedUrls, int maxPages);
    
    /**
     * Get the next URL to crawl
     * 
     * @return The next URL to crawl
     */
    String next();
    
    /**
     * Check if there are more URLs to crawl
     * 
     * @return true if there are more URLs, false otherwise
     */
    boolean hasNext();
    
    /**
     * Add a URL to the frontier
     * 
     * @param url The URL to add
     */
    void addUrl(String url);
}
