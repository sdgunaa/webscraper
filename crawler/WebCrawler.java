package crawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import ai.AiContentAnalyzer;
import config.CrawlConfig;
import http.HttpRequester;
import http.RateLimiter;
import models.CrawlResult;
import models.WebPage;
import parser.ContentExtractor;
import parser.HtmlParser;
import util.Logger;
import util.UrlUtils;

/**
 * Main class responsible for orchestrating the web crawling process.
 */
public class WebCrawler {
    private final CrawlStrategy strategy;
    private final CrawlConfig config;
    private final HttpRequester httpRequester;
    private final HtmlParser htmlParser;
    private final ContentExtractor contentExtractor;
    private final RateLimiter rateLimiter;
    private final AiContentAnalyzer aiAnalyzer;
    private final Logger logger;
    
    /**
     * Constructor for WebCrawler
     * 
     * @param strategy The crawl strategy to use (BFS or DFS)
     * @param config The configuration for the crawl
     */
    public WebCrawler(CrawlStrategy strategy, CrawlConfig config) {
        this.strategy = strategy;
        this.config = config;
        this.httpRequester = new HttpRequester(config);
        this.htmlParser = new HtmlParser();
        this.contentExtractor = new ContentExtractor();
        this.rateLimiter = new RateLimiter(config.getRequestDelay());
        this.aiAnalyzer = new AiContentAnalyzer();
        this.logger = new Logger(WebCrawler.class.getSimpleName());
    }
    
    /**
     * Start the crawling process
     * 
     * @return CrawlResult containing the crawled data
     */
    public CrawlResult crawl() {
        Set<String> visitedUrls = new HashSet<>();
        Set<WebPage> relevantPages = new HashSet<>();
        Set<String> frontier = new HashSet<>();
        
        // Add seed URL to frontier
        frontier.add(config.getSeedUrl());
        
        logger.info("Starting crawl from seed URL: " + config.getSeedUrl());
        
        // Execute the crawl using the selected strategy
        strategy.initialize(frontier, visitedUrls, config.getMaxPages());
        
        while (strategy.hasNext() && visitedUrls.size() < config.getMaxPages()) {
            String currentUrl = strategy.next();
            
            if (visitedUrls.contains(currentUrl)) {
                continue;
            }
            
            // Rate limiting
            rateLimiter.limitRequest();
            
            logger.info("Crawling URL: " + currentUrl + " [" + visitedUrls.size() + "/" + config.getMaxPages() + "]");
            
            try {
                // Fetch page content
                String htmlContent = httpRequester.fetchUrl(currentUrl);
                
                if (htmlContent != null && !htmlContent.isEmpty()) {
                    // Parse the HTML content
                    Set<String> extractedLinks = htmlParser.extractLinks(htmlContent, currentUrl);
                    String title = htmlParser.extractTitle(htmlContent);
                    String text = contentExtractor.extractText(htmlContent);
                    
                    // Create a WebPage object
                    WebPage page = new WebPage(currentUrl, title, text);
                    
                    // Check if the page is relevant based on keywords
                    boolean isRelevant = isPageRelevant(page);
                    
                    // If relevant, perform AI analysis if enabled
                    if (isRelevant) {
                        if (config.isEnableAiAnalysis()) {
                            String aiAnalysis = aiAnalyzer.analyzeContent(text, config.getKeywords());
                            page.setAiAnalysis(aiAnalysis);
                        }
                        relevantPages.add(page);
                        logger.info("Found relevant page: " + title);
                    }
                    
                    // Add new URLs to the frontier
                    for (String link : extractedLinks) {
                        if (!visitedUrls.contains(link)) {
                            strategy.addUrl(link);
                        }
                    }
                }
                
                // Mark URL as visited
                visitedUrls.add(currentUrl);
                
            } catch (Exception e) {
                logger.error("Error crawling URL: " + currentUrl + " - " + e.getMessage());
            }
        }
        
        logger.info("Crawling complete. Visited " + visitedUrls.size() + " pages, found " + 
                    relevantPages.size() + " relevant pages.");
        
        return new CrawlResult(visitedUrls, relevantPages);
    }
    
    /**
     * Check if a page is relevant based on the configured keywords
     * 
     * @param page The WebPage to check
     * @return true if the page is relevant, false otherwise
     */
    private boolean isPageRelevant(WebPage page) {
        if (config.getKeywords() == null || config.getKeywords().length == 0) {
            return true;
        }
        
        String content = page.getTitle() + " " + page.getContent();
        content = content.toLowerCase();
        
        for (String keyword : config.getKeywords()) {
            if (content.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }
}
