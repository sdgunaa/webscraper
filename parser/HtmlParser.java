package parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Logger;
import util.UrlUtils;

/**
 * Custom HTML parser implementation using regex patterns
 */
public class HtmlParser {
    private static final Pattern LINK_PATTERN = Pattern.compile("<a\\s+(?:[^>]*?\\s+)?href=[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE);
    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    private final Logger logger;
    
    public HtmlParser() {
        this.logger = new Logger(HtmlParser.class.getSimpleName());
    }
    
    /**
     * Extract all links from HTML content
     * 
     * @param html The HTML content to parse
     * @param baseUrl The base URL for resolving relative URLs
     * @return A set of absolute URLs found in the HTML
     */
    public Set<String> extractLinks(String html, String baseUrl) {
        Set<String> links = new HashSet<>();
        
        Matcher matcher = LINK_PATTERN.matcher(html);
        while (matcher.find()) {
            String link = matcher.group(1);
            
            // Convert relative URLs to absolute URLs
            try {
                String absoluteUrl = UrlUtils.resolveUrl(baseUrl, link);
                
                // Only add HTTP or HTTPS URLs
                if (absoluteUrl != null && (absoluteUrl.startsWith("http://") || absoluteUrl.startsWith("https://"))) {
                    links.add(absoluteUrl);
                }
            } catch (URISyntaxException e) {
                logger.debug("Invalid URL: " + link + " - " + e.getMessage());
            }
        }
        
        logger.debug("Extracted " + links.size() + " links from " + baseUrl);
        return links;
    }
    
    /**
     * Extract the title from HTML content
     * 
     * @param html The HTML content to parse
     * @return The title of the HTML page or an empty string if not found
     */
    public String extractTitle(String html) {
        Matcher matcher = TITLE_PATTERN.matcher(html);
        if (matcher.find()) {
            String title = matcher.group(1).trim();
            return title;
        }
        return "";
    }
}
