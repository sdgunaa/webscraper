package models;

import java.util.Set;

/**
 * Model class representing the results of a crawl
 */
public class CrawlResult {
    private final Set<String> visitedUrls;
    private final Set<WebPage> relevantPages;
    
    /**
     * Constructor for CrawlResult
     * 
     * @param visitedUrls Set of all URLs visited during the crawl
     * @param relevantPages Set of relevant web pages found during the crawl
     */
    public CrawlResult(Set<String> visitedUrls, Set<WebPage> relevantPages) {
        this.visitedUrls = visitedUrls;
        this.relevantPages = relevantPages;
    }
    
    /**
     * Get the set of all URLs visited during the crawl
     * 
     * @return Set of visited URLs
     */
    public Set<String> getVisitedUrls() {
        return visitedUrls;
    }
    
    /**
     * Get the set of relevant web pages found during the crawl
     * 
     * @return Set of relevant WebPage objects
     */
    public Set<WebPage> getRelevantPages() {
        return relevantPages;
    }
    
    /**
     * Get the total number of URLs visited
     * 
     * @return Count of visited URLs
     */
    public int getTotalVisited() {
        return visitedUrls.size();
    }
    
    /**
     * Get the total number of relevant pages found
     * 
     * @return Count of relevant pages
     */
    public int getTotalRelevant() {
        return relevantPages.size();
    }
}
