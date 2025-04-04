package util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class for URL operations
 */
public class UrlUtils {
    private static final Logger logger = new Logger(UrlUtils.class.getSimpleName());
    
    /**
     * Resolves a relative URL against a base URL
     * 
     * @param baseUrl The base URL
     * @param relativeUrl The relative URL to resolve
     * @return The resolved absolute URL, or null if invalid
     * @throws URISyntaxException If the URL is invalid
     */
    public static String resolveUrl(String baseUrl, String relativeUrl) throws URISyntaxException {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return null;
        }
        
        // Check if it's already an absolute URL
        if (relativeUrl.toLowerCase().startsWith("http://") || relativeUrl.toLowerCase().startsWith("https://")) {
            return normalizeUrl(relativeUrl);
        }
        
        // Handle fragment URLs (starting with #)
        if (relativeUrl.startsWith("#")) {
            return baseUrl;
        }
        
        // Handle javascript: and data: URLs
        if (relativeUrl.toLowerCase().startsWith("javascript:") || 
            relativeUrl.toLowerCase().startsWith("data:") ||
            relativeUrl.toLowerCase().startsWith("mailto:")) {
            return null;
        }
        
        try {
            URI baseUri = new URI(baseUrl);
            URI resolvedUri = baseUri.resolve(relativeUrl);
            
            // Convert to URL and back to normalize
            URL url = resolvedUri.toURL();
            return url.toString();
        } catch (MalformedURLException e) {
            logger.debug("Malformed URL: " + relativeUrl + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Normalizes a URL by removing fragments and normalizing the path
     * 
     * @param url The URL to normalize
     * @return Normalized URL
     */
    public static String normalizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            // Remove fragments
            int fragmentIndex = url.indexOf('#');
            if (fragmentIndex > 0) {
                url = url.substring(0, fragmentIndex);
            }
            
            // Convert to URI and back to normalize
            URI uri = new URI(url);
            URL normalizedUrl = uri.toURL();
            
            return normalizedUrl.toString();
        } catch (URISyntaxException | MalformedURLException e) {
            logger.debug("Error normalizing URL: " + url + " - " + e.getMessage());
            return url;  // Return original if normalization fails
        }
    }
    
    /**
     * Checks if a URL is within the same domain as a base URL
     * 
     * @param baseUrl The base URL
     * @param url The URL to check
     * @return true if the URL is within the same domain
     */
    public static boolean isSameDomain(String baseUrl, String url) {
        try {
            URI baseUri = new URI(baseUrl);
            URI uri = new URI(url);
            
            return baseUri.getHost().equalsIgnoreCase(uri.getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * Gets the domain from a URL
     * 
     * @param url The URL
     * @return The domain
     */
    public static String getDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }
    
    /**
     * Checks if a URL points to an image
     * 
     * @param url The URL to check
     * @return true if the URL points to an image
     */
    public static boolean isImageUrl(String url) {
        if (url == null) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") || lowerUrl.endsWith(".jpeg") || 
               lowerUrl.endsWith(".png") || lowerUrl.endsWith(".gif") || 
               lowerUrl.endsWith(".bmp") || lowerUrl.endsWith(".svg");
    }
    
    /**
     * Checks if a URL points to a document or media file
     * 
     * @param url The URL to check
     * @return true if the URL points to a document or media file
     */
    public static boolean isDocumentOrMediaUrl(String url) {
        if (url == null) {
            return false;
        }
        
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".pdf") || lowerUrl.endsWith(".doc") || 
               lowerUrl.endsWith(".docx") || lowerUrl.endsWith(".xls") || 
               lowerUrl.endsWith(".xlsx") || lowerUrl.endsWith(".ppt") || 
               lowerUrl.endsWith(".pptx") || lowerUrl.endsWith(".zip") || 
               lowerUrl.endsWith(".rar") || lowerUrl.endsWith(".mp3") || 
               lowerUrl.endsWith(".mp4") || lowerUrl.endsWith(".avi") || 
               lowerUrl.endsWith(".mov");
    }
}
