package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import config.CrawlConfig;
import util.Logger;

/**
 * Handles HTTP requests with retry logic and response handling
 */
public class HttpRequester {
    private static final String USER_AGENT = "Mozilla/5.0 (compatible; JavaWebCrawlerBot/1.0)";
    private static final int CONNECTION_TIMEOUT = 10000; // 10 seconds
    private static final int READ_TIMEOUT = 30000; // 30 seconds
    
    private final CrawlConfig config;
    private final Logger logger;
    
    public HttpRequester(CrawlConfig config) {
        this.config = config;
        this.logger = new Logger(HttpRequester.class.getSimpleName());
    }
    
    /**
     * Fetch the HTML content from the given URL with retry logic
     * 
     * @param urlString The URL to fetch
     * @return The HTML content as a String, or null if failed
     */
    public String fetchUrl(String urlString) {
        int retries = 0;
        
        while (retries < config.getMaxRetries()) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", USER_AGENT);
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml");
                connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setInstanceFollowRedirects(true);
                
                int responseCode = connection.getResponseCode();
                
                // Handle redirects manually if needed
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || 
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP || 
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
                    String newUrl = connection.getHeaderField("Location");
                    logger.debug("Redirecting to: " + newUrl);
                    return fetchUrl(newUrl);
                }
                
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Check content type
                    String contentType = connection.getContentType();
                    if (contentType == null || !contentType.contains("text/html")) {
                        logger.debug("Skipping non-HTML content: " + contentType);
                        return null;
                    }
                    
                    // Read the response
                    BufferedReader reader;
                    String encoding = connection.getContentEncoding();
                    if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                        reader = new BufferedReader(new InputStreamReader(
                                new GZIPInputStream(connection.getInputStream()), "UTF-8"));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(
                                connection.getInputStream(), "UTF-8"));
                    }
                    
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();
                    
                    logger.debug("Successfully fetched: " + urlString);
                    return content.toString();
                } else {
                    logger.error("HTTP Error: " + responseCode + " for URL: " + urlString);
                }
            } catch (IOException e) {
                logger.error("Error fetching URL: " + urlString + " - " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            
            // Retry logic
            retries++;
            if (retries < config.getMaxRetries()) {
                int waitTime = retries * 1000; // Exponential backoff
                logger.debug("Retrying (" + retries + "/" + config.getMaxRetries() + ") in " + waitTime + "ms: " + urlString);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        logger.error("Failed to fetch URL after " + config.getMaxRetries() + " retries: " + urlString);
        return null;
    }
}
