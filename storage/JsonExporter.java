package storage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import models.CrawlResult;
import models.WebPage;
import util.Logger;

/**
 * Exports crawl results to JSON format
 */
public class JsonExporter implements DataExporter {
    private final Logger logger;
    
    public JsonExporter() {
        this.logger = new Logger(JsonExporter.class.getSimpleName());
    }
    
    @Override
    public boolean export(CrawlResult result, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            StringBuilder json = new StringBuilder();
            
            // Start the JSON object
            json.append("{\n");
            
            // Add summary data
            json.append("  \"total_pages_visited\": ").append(result.getVisitedUrls().size()).append(",\n");
            json.append("  \"total_relevant_pages\": ").append(result.getRelevantPages().size()).append(",\n");
            
            // Add visited URLs
            json.append("  \"visited_urls\": [\n");
            int urlCount = 0;
            for (String url : result.getVisitedUrls()) {
                json.append("    \"").append(escapeJsonString(url)).append("\"");
                if (urlCount < result.getVisitedUrls().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
                urlCount++;
            }
            json.append("  ],\n");
            
            // Add relevant pages with details
            json.append("  \"relevant_pages\": [\n");
            int pageCount = 0;
            for (WebPage page : result.getRelevantPages()) {
                json.append("    {\n");
                json.append("      \"url\": \"").append(escapeJsonString(page.getUrl())).append("\",\n");
                json.append("      \"title\": \"").append(escapeJsonString(page.getTitle() != null ? page.getTitle() : "")).append("\",\n");
                json.append("      \"content_length\": ").append(page.getContent() != null ? page.getContent().length() : 0).append(",\n");
                
                // Add AI analysis if available
                if (page.getAiAnalysis() != null) {
                    json.append("      \"ai_analysis\": \"").append(escapeJsonString(page.getAiAnalysis())).append("\",\n");
                }
                
                // Add metadata if available
                if (!page.getMetadata().isEmpty()) {
                    json.append("      \"metadata\": {\n");
                    int metaCount = 0;
                    for (Map.Entry<String, String> entry : page.getMetadata().entrySet()) {
                        json.append("        \"").append(escapeJsonString(entry.getKey())).append("\": \"")
                            .append(escapeJsonString(entry.getValue())).append("\"");
                        if (metaCount < page.getMetadata().size() - 1) {
                            json.append(",");
                        }
                        json.append("\n");
                        metaCount++;
                    }
                    json.append("      },\n");
                }
                
                // Add content snippet (truncated)
                if (page.getContent() != null) {
                    String content = page.getContent();
                    String snippet = content.length() > 500 ? content.substring(0, 500) + "..." : content;
                    json.append("      \"content_snippet\": \"").append(escapeJsonString(snippet)).append("\"\n");
                } else {
                    json.append("      \"content_snippet\": \"\"\n");
                }
                
                json.append("    }");
                if (pageCount < result.getRelevantPages().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
                pageCount++;
            }
            json.append("  ]\n");
            
            // Close the JSON object
            json.append("}");
            
            // Write to file
            fileWriter.write(json.toString());
            
            logger.info("Successfully exported results to JSON file: " + filePath);
            return true;
            
        } catch (IOException e) {
            logger.error("Error exporting to JSON: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Escape JSON string value
     * 
     * @param input String to escape
     * @return Escaped string
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            switch (ch) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '\"':
                    result.append("\\\"");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                default:
                    if (ch < ' ') {
                        String hex = Integer.toHexString(ch);
                        result.append("\\u");
                        for (int j = 0; j < 4 - hex.length(); j++) {
                            result.append('0');
                        }
                        result.append(hex);
                    } else {
                        result.append(ch);
                    }
            }
        }
        return result.toString();
    }
}
