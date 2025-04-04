package parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.Logger;

/**
 * Extracts and processes textual content from HTML
 */
public class ContentExtractor {
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern STYLE_PATTERN = Pattern.compile("<style[^>]*>.*?</style>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s+");
    
    private final Logger logger;
    
    public ContentExtractor() {
        this.logger = new Logger(ContentExtractor.class.getSimpleName());
    }
    
    /**
     * Extract clean text content from HTML
     * 
     * @param html The HTML content to process
     * @return Cleaned textual content
     */
    public String extractText(String html) {
        if (html == null || html.isEmpty()) {
            return "";
        }
        
        try {
            // Remove script tags and their content
            String result = SCRIPT_PATTERN.matcher(html).replaceAll("");
            
            // Remove style tags and their content
            result = STYLE_PATTERN.matcher(result).replaceAll("");
            
            // Extract paragraphs with better context (before removing all tags)
            StringBuilder paragraphs = new StringBuilder();
            Pattern paragraphPattern = Pattern.compile("<p[^>]*>(.*?)</p>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher paragraphMatcher = paragraphPattern.matcher(result);
            
            while (paragraphMatcher.find()) {
                String paragraph = paragraphMatcher.group(1).trim();
                // Remove inner tags
                paragraph = HTML_TAG_PATTERN.matcher(paragraph).replaceAll(" ");
                paragraphs.append(paragraph).append("\n\n");
            }
            
            // If no paragraphs found, use all text
            if (paragraphs.length() == 0) {
                // Remove all HTML tags
                result = HTML_TAG_PATTERN.matcher(result).replaceAll(" ");
                
                // Replace multiple spaces with a single space
                result = MULTIPLE_SPACES_PATTERN.matcher(result).replaceAll(" ");
                
                // Decode HTML entities
                result = decodeHtmlEntities(result);
                
                return result.trim();
            } else {
                // Decode HTML entities
                String text = decodeHtmlEntities(paragraphs.toString());
                
                // Replace multiple spaces with a single space
                text = MULTIPLE_SPACES_PATTERN.matcher(text).replaceAll(" ");
                
                return text.trim();
            }
        } catch (Exception e) {
            logger.error("Error extracting text: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Basic HTML entity decoder
     * 
     * @param html The HTML string with entities
     * @return Decoded string
     */
    private String decodeHtmlEntities(String html) {
        return html.replaceAll("&amp;", "&")
                   .replaceAll("&lt;", "<")
                   .replaceAll("&gt;", ">")
                   .replaceAll("&quot;", "\"")
                   .replaceAll("&apos;", "'")
                   .replaceAll("&#39;", "'")
                   .replaceAll("&nbsp;", " ");
    }
    
    /**
     * Extracts meta description from HTML
     * 
     * @param html The HTML content
     * @return The meta description or empty string if not found
     */
    public String extractMetaDescription(String html) {
        Pattern pattern = Pattern.compile("<meta\\s+name=[\"']description[\"']\\s+content=[\"'](.*?)[\"']", 
                                         Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try alternate meta description format
        pattern = Pattern.compile("<meta\\s+content=[\"'](.*?)[\"']\\s+name=[\"']description[\"']", 
                                 Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
}
