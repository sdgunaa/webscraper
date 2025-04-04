package storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import models.CrawlResult;
import models.WebPage;
import util.Logger;

/**
 * Exports crawl results to CSV format
 */
public class CsvExporter implements DataExporter {
    private final Logger logger;
    
    public CsvExporter() {
        this.logger = new Logger(CsvExporter.class.getSimpleName());
    }
    
    @Override
    public boolean export(CrawlResult result, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("URL,Title,Content Length,Has AI Analysis,Metadata\n");
            
            // Write page data
            for (WebPage page : result.getRelevantPages()) {
                StringBuilder line = new StringBuilder();
                
                // URL (escape commas and quotes)
                line.append("\"").append(escapeCSV(page.getUrl())).append("\"").append(",");
                
                // Title (escape commas and quotes)
                line.append("\"").append(escapeCSV(page.getTitle())).append("\"").append(",");
                
                // Content length
                int contentLength = page.getContent() != null ? page.getContent().length() : 0;
                line.append(contentLength).append(",");
                
                // Whether AI analysis is available
                line.append(page.getAiAnalysis() != null ? "Yes" : "No").append(",");
                
                // Metadata as key-value pairs
                StringBuilder metadataStr = new StringBuilder();
                for (Map.Entry<String, String> entry : page.getMetadata().entrySet()) {
                    if (metadataStr.length() > 0) {
                        metadataStr.append("; ");
                    }
                    metadataStr.append(escapeCSV(entry.getKey()))
                               .append(": ")
                               .append(escapeCSV(entry.getValue()));
                }
                line.append("\"").append(metadataStr).append("\"");
                
                // Write the line
                writer.write(line.toString() + "\n");
            }
            
            // Write summary at the end
            writer.write("\n\nSummary\n");
            writer.write("Total Pages Visited," + result.getVisitedUrls().size() + "\n");
            writer.write("Total Relevant Pages," + result.getRelevantPages().size() + "\n");
            
            logger.info("Successfully exported results to CSV file: " + filePath);
            return true;
            
        } catch (IOException e) {
            logger.error("Error exporting to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Write a separate file with all visited URLs
     * 
     * @param result The crawl result
     * @param filePath The base file path (will append "_urls.csv")
     */
    public boolean exportVisitedUrls(CrawlResult result, String filePath) {
        String urlsFilePath = filePath.replace(".csv", "_urls.csv");
        
        try (FileWriter writer = new FileWriter(urlsFilePath)) {
            writer.write("Visited URLs\n");
            
            for (String url : result.getVisitedUrls()) {
                writer.write("\"" + escapeCSV(url) + "\"\n");
            }
            
            logger.info("Successfully exported visited URLs to CSV file: " + urlsFilePath);
            return true;
            
        } catch (IOException e) {
            logger.error("Error exporting URLs to CSV: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Escape special characters for CSV
     * 
     * @param input The string to escape
     * @return Escaped string
     */
    private String escapeCSV(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\"", "\"\"");
    }
}
