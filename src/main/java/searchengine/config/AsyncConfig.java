package searchengine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Async Settings
 */
@Configuration
@EnableAsync
@EnableTransactionManagement
public class AsyncConfig {
}
