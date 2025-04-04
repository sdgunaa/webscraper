package ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import util.Logger;

/**
 * Integrates with OpenAI API to analyze content relevance and context
 */
public class AiContentAnalyzer {
    private static final String OPENAI_API_ENDPOINT = "https://api.openai.com/v1/chat/completions";
    private final Logger logger;
    
    public AiContentAnalyzer() {
        this.logger = new Logger(AiContentAnalyzer.class.getSimpleName());
    }
    
    /**
     * Analyze content using OpenAI API
     * 
     * @param content The text content to analyze
     * @param keywords Keywords to focus on during analysis
     * @return Analysis results or error message
     */
    public String analyzeContent(String content, String[] keywords) {
        if (content == null || content.isEmpty()) {
            return "No content to analyze";
        }
        
        // Get API key from environment variable
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("OpenAI API key not found in environment variables");
            return "Error: OpenAI API key not configured";
        }
        
        // Truncate content if it's too long
        String truncatedContent = truncateContent(content, 2000);
        
        try {
            URL url = new URL(OPENAI_API_ENDPOINT);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);
            
            // Create the request body
            String prompt = createPrompt(truncatedContent, keywords);
            String requestBody = createRequestBody(prompt);
            
            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            
            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader br = new BufferedReader(
                     new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    
                    // Parse JSON response manually (without javax.json)
                    StringBuilder responseStr = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseStr.append(line);
                    }
                    
                    // Manual JSON parsing (simple implementation)
                    String jsonResponse = responseStr.toString();
                    String analysisResult = extractContentFromJson(jsonResponse);
                    
                    if (analysisResult != null) {
                        return analysisResult;
                    }
                    
                    return "No analysis results returned";
                }
            } else {
                logger.error("OpenAI API request failed with status code: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                     new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    logger.error("API Error: " + response.toString());
                    return "Error: API request failed - " + responseCode;
                }
            }
        } catch (IOException e) {
            logger.error("Error connecting to OpenAI API: " + e.getMessage());
            return "Error: Unable to connect to AI service";
        }
    }
    
    /**
     * Extracts content from JSON response (simple implementation)
     * 
     * @param jsonResponse The JSON response as a string
     * @return The extracted content or null if not found
     */
    private String extractContentFromJson(String jsonResponse) {
        try {
            // Look for "content" field in the response
            // Note: This is a very simplified JSON parser and may not work for all JSON structures
            int choicesIndex = jsonResponse.indexOf("\"choices\"");
            if (choicesIndex != -1) {
                int contentIndex = jsonResponse.indexOf("\"content\"", choicesIndex);
                if (contentIndex != -1) {
                    int valueStart = jsonResponse.indexOf(":", contentIndex) + 1;
                    int valueEnd = -1;
                    
                    // Find the closing quote of the content value
                    boolean inQuote = false;
                    boolean escaped = false;
                    int i = valueStart;
                    while (i < jsonResponse.length()) {
                        char c = jsonResponse.charAt(i);
                        if (c == '\\') {
                            escaped = !escaped;
                        } else if (c == '"' && !escaped) {
                            if (!inQuote) {
                                inQuote = true;
                                valueStart = i + 1;
                            } else {
                                valueEnd = i;
                                break;
                            }
                        } else {
                            escaped = false;
                        }
                        i++;
                    }
                    
                    if (valueEnd != -1) {
                        return jsonResponse.substring(valueStart, valueEnd);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            logger.error("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Creates the prompt for the OpenAI API
     * 
     * @param content The text content to analyze
     * @param keywords Keywords to focus on
     * @return The formatted prompt
     */
    private String createPrompt(String content, String[] keywords) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following content and provide insights regarding its relevance to these keywords: ");
        prompt.append(Arrays.toString(keywords));
        prompt.append(".\n\nContent to analyze:\n");
        prompt.append(content);
        prompt.append("\n\nPlease provide a brief summary of the content, assess its relevance to the keywords, ");
        prompt.append("and extract the 3-5 most important facts or points made in relation to those keywords.");
        
        return prompt.toString();
    }
    
    /**
     * Creates the JSON request body for the OpenAI API
     * 
     * @param prompt The prompt to send
     * @return The formatted JSON request body
     */
    private String createRequestBody(String prompt) {
        // Manual JSON construction (without javax.json)
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"model\":\"gpt-3.5-turbo\",");
        json.append("\"messages\":[");
        json.append("{\"role\":\"system\",\"content\":\"You are a web content analyzer that evaluates relevance and extracts key information.\"},");
        json.append("{\"role\":\"user\",\"content\":\"").append(escapeJsonString(prompt)).append("\"}");
        json.append("],");
        json.append("\"temperature\":0.3,");
        json.append("\"max_tokens\":500");
        json.append("}");
        
        return json.toString();
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
    
    /**
     * Truncates content to a maximum length to avoid exceeding API limits
     * 
     * @param content The content to truncate
     * @param maxChars Maximum number of characters
     * @return Truncated content
     */
    private String truncateContent(String content, int maxChars) {
        if (content.length() <= maxChars) {
            return content;
        }
        
        // Try to truncate at sentence or paragraph boundary
        int lastPeriod = content.lastIndexOf(".", maxChars);
        int lastNewline = content.lastIndexOf("\n", maxChars);
        
        int truncatePoint = Math.max(lastPeriod, lastNewline);
        if (truncatePoint < 0) {
            truncatePoint = maxChars;
        } else {
            truncatePoint++; // Include the period or newline
        }
        
        return content.substring(0, truncatePoint) + "... [content truncated]";
    }
}
