package http;

import util.Logger;

/**
 * Implements rate limiting to avoid overloading websites
 */
public class RateLimiter {
    private long lastRequestTime;
    private final long delayMs;
    private final Logger logger;
    
    /**
     * Constructor for RateLimiter
     * 
     * @param delayMs Minimum delay between requests in milliseconds
     */
    public RateLimiter(long delayMs) {
        this.delayMs = delayMs;
        this.lastRequestTime = 0;
        this.logger = new Logger(RateLimiter.class.getSimpleName());
    }
    
    /**
     * Limits request rate by ensuring minimum delay between requests
     */
    public synchronized void limitRequest() {
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastRequestTime;
        
        if (timeSinceLastRequest < delayMs) {
            long sleepTime = delayMs - timeSinceLastRequest;
            try {
                logger.debug("Rate limiting: Sleeping for " + sleepTime + "ms");
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        lastRequestTime = System.currentTimeMillis();
    }
    
    /**
     * Set a different delay for rate limiting
     * 
     * @param newDelayMs New delay in milliseconds
     */
    public void setDelay(long newDelayMs) {
        if (newDelayMs > 0) {
            logger.info("Changing rate limit delay from " + delayMs + "ms to " + newDelayMs + "ms");
        }
    }
}
