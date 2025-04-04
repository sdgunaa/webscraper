package storage;

import models.CrawlResult;

/**
 * Interface for different data export formats
 */
public interface DataExporter {
    
    /**
     * Export crawl results to a file
     * 
     * @param result The crawl result to export
     * @param filePath The path of the output file
     * @return true if export was successful, false otherwise
     */
    boolean export(CrawlResult result, String filePath);
}
