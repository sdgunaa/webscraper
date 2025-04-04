package config;

/**
 * Configuration class for the web crawler
 */
public class CrawlConfig {
    private final String seedUrl;
    private final String[] keywords;
    private final int maxPages;
    private final boolean enableAiAnalysis;
    private final long requestDelay;
    private final int maxRetries;
    
    /**
     * Private constructor used by the Builder
     */
    private CrawlConfig(Builder builder) {
        this.seedUrl = builder.seedUrl;
        this.keywords = builder.keywords;
        this.maxPages = builder.maxPages;
        this.enableAiAnalysis = builder.enableAiAnalysis;
        this.requestDelay = builder.requestDelay;
        this.maxRetries = builder.maxRetries;
    }
    
    /**
     * Get the seed URL
     * 
     * @return The seed URL
     */
    public String getSeedUrl() {
        return seedUrl;
    }
    
    /**
     * Get the search keywords
     * 
     * @return Array of keywords
     */
    public String[] getKeywords() {
        return keywords;
    }
    
    /**
     * Get the maximum number of pages to crawl
     * 
     * @return The maximum number of pages
     */
    public int getMaxPages() {
        return maxPages;
    }
    
    /**
     * Check if AI analysis is enabled
     * 
     * @return true if AI analysis is enabled
     */
    public boolean isEnableAiAnalysis() {
        return enableAiAnalysis;
    }
    
    /**
     * Get the delay between requests in milliseconds
     * 
     * @return The request delay
     */
    public long getRequestDelay() {
        return requestDelay;
    }
    
    /**
     * Get the maximum number of retries for failed requests
     * 
     * @return The maximum number of retries
     */
    public int getMaxRetries() {
        return maxRetries;
    }
    
    /**
     * Builder class for CrawlConfig
     */
    public static class Builder {
        private String seedUrl;
        private String[] keywords = new String[0];
        private int maxPages = 100;
        private boolean enableAiAnalysis = false;
        private long requestDelay = 1000; // Default 1 second
        private int maxRetries = 3;
        
        /**
         * Set the seed URL
         * 
         * @param seedUrl The seed URL
         * @return The Builder instance
         */
        public Builder setSeedUrl(String seedUrl) {
            this.seedUrl = seedUrl;
            return this;
        }
        
        /**
         * Set the search keywords
         * 
         * @param keywords Array of keywords
         * @return The Builder instance
         */
        public Builder setKeywords(String[] keywords) {
            this.keywords = keywords;
            return this;
        }
        
        /**
         * Set the maximum number of pages to crawl
         * 
         * @param maxPages The maximum number of pages
         * @return The Builder instance
         */
        public Builder setMaxPages(int maxPages) {
            this.maxPages = maxPages;
            return this;
        }
        
        /**
         * Set whether AI analysis is enabled
         * 
         * @param enableAiAnalysis true to enable AI analysis
         * @return The Builder instance
         */
        public Builder setEnableAiAnalysis(boolean enableAiAnalysis) {
            this.enableAiAnalysis = enableAiAnalysis;
            return this;
        }
        
        /**
         * Set the delay between requests in milliseconds
         * 
         * @param requestDelay The request delay
         * @return The Builder instance
         */
        public Builder setRequestDelay(long requestDelay) {
            this.requestDelay = requestDelay;
            return this;
        }
        
        /**
         * Set the maximum number of retries for failed requests
         * 
         * @param maxRetries The maximum number of retries
         * @return The Builder instance
         */
        public Builder setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }
        
        /**
         * Build the CrawlConfig
         * 
         * @return The CrawlConfig instance
         */
        public CrawlConfig build() {
            return new CrawlConfig(this);
        }
    }
}
