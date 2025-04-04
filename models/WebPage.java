package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class representing a web page
 */
public class WebPage {
    private final String url;
    private final String title;
    private final String content;
    private String aiAnalysis;
    private Map<String, String> metadata;
    
    /**
     * Constructor for WebPage
     * 
     * @param url The URL of the web page
     * @param title The title of the web page
     * @param content The textual content of the web page
     */
    public WebPage(String url, String title, String content) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.aiAnalysis = null;
        this.metadata = new HashMap<>();
    }
    
    /**
     * Get the URL of the web page
     * 
     * @return The URL
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Get the title of the web page
     * 
     * @return The title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Get the textual content of the web page
     * 
     * @return The content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Get the AI analysis of the web page content
     * 
     * @return The AI analysis
     */
    public String getAiAnalysis() {
        return aiAnalysis;
    }
    
    /**
     * Set the AI analysis of the web page content
     * 
     * @param aiAnalysis The AI analysis
     */
    public void setAiAnalysis(String aiAnalysis) {
        this.aiAnalysis = aiAnalysis;
    }
    
    /**
     * Get metadata for the web page
     * 
     * @return Map of metadata key-value pairs
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    /**
     * Add a metadata key-value pair
     * 
     * @param key The metadata key
     * @param value The metadata value
     */
    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }
    
    /**
     * Get a summary of the web page (for debugging and logging)
     * 
     * @return A summary string
     */
    public String getSummary() {
        int contentLength = content != null ? content.length() : 0;
        return String.format("WebPage[url=%s, title=%s, contentLength=%d]", 
                            url, title, contentLength);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        WebPage webPage = (WebPage) obj;
        return url.equals(webPage.url);
    }
    
    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
