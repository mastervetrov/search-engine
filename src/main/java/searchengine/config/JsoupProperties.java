package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jsoup")
@Getter
@Setter
public class JsoupProperties {
    private String userAgent;
    private String referrer;
    private Integer sleepPageConnectorBasicVolume;
    private Integer sleepPageConnectorAdditionalVolumeRandomTo;
}
