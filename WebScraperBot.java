import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import crawler.BreadthFirstCrawler;
import crawler.CrawlStrategy;
import crawler.DepthFirstCrawler;
import crawler.WebCrawler;
import models.CrawlResult;
import models.WebPage;
import storage.CsvExporter;
import storage.DataExporter;
import storage.JsonExporter;
import util.Logger;
import config.CrawlConfig;

/**
 * Main class for the Web Scraper Bot application.
 * Provides a command-line interface to interact with the web crawler.
 */
public class WebScraperBot {
    private static final Logger logger = new Logger(WebScraperBot.class.getSimpleName());
    
    public static void main(String[] args) {
        logger.info("Starting Web Scraper Bot...");
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("=== Web Scraper Bot ===");
            System.out.println("Enter a starting URL:");
            String seedUrl = reader.readLine().trim();
            
            System.out.println("Enter search keywords (comma-separated):");
            String[] keywords = reader.readLine().split(",");
            for (int i = 0; i < keywords.length; i++) {
                keywords[i] = keywords[i].trim();
            }
            
            System.out.println("Enter maximum number of pages to crawl:");
            int maxPages = Integer.parseInt(reader.readLine().trim());
            
            System.out.println("Select crawl strategy (1 for BFS, 2 for DFS):");
            int strategyChoice = Integer.parseInt(reader.readLine().trim());
            
            System.out.println("Enter output format (json or csv):");
            String outputFormat = reader.readLine().trim().toLowerCase();
            
            System.out.println("Enable AI content analysis? (yes/no):");
            boolean enableAi = reader.readLine().trim().toLowerCase().equals("yes");
            
            System.out.println("Enter output file path:");
            String outputFilePath = reader.readLine().trim();
            
            // Create crawl configuration
            CrawlConfig config = new CrawlConfig.Builder()
                    .setSeedUrl(seedUrl)
                    .setKeywords(keywords)
                    .setMaxPages(maxPages)
                    .setEnableAiAnalysis(enableAi)
                    .setRequestDelay(1000) // 1 second delay between requests
                    .setMaxRetries(3)
                    .build();
            
            // Select crawl strategy
            CrawlStrategy strategy;
            if (strategyChoice == 1) {
                strategy = new BreadthFirstCrawler();
                logger.info("Using Breadth-First Search strategy");
            } else {
                strategy = new DepthFirstCrawler();
                logger.info("Using Depth-First Search strategy");
            }
            
            // Initialize the web crawler
            WebCrawler crawler = new WebCrawler(strategy, config);
            
            // Start crawling
            System.out.println("Starting crawl process...");
            CrawlResult result = crawler.crawl();
            
            // Export results
            DataExporter exporter;
            if (outputFormat.equals("json")) {
                exporter = new JsonExporter();
            } else {
                exporter = new CsvExporter();
            }
            
            exporter.export(result, outputFilePath);
            
            // Print summary
            System.out.println("\n=== Crawl Summary ===");
            System.out.println("Total pages visited: " + result.getVisitedUrls().size());
            System.out.println("Total relevant pages found: " + result.getRelevantPages().size());
            System.out.println("Results saved to: " + outputFilePath);
            
        } catch (IOException e) {
            logger.error("Error reading input: " + e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
