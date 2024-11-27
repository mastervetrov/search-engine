package searchengine.service.indexing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupProperties;
import searchengine.model.SiteEntity;
import searchengine.model.StatusString;

import java.io.IOException;
import java.time.Instant;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class PageConnector {
    private final JsoupProperties jsoupProperties;

    public Connection.Response tryGetConnectionResponse(String url, SiteEntity siteEntity) {
        Connection.Response response = null;
        try {
            randomSleepThread();
            response = Jsoup.connect(url)
                    .userAgent(jsoupProperties.getUserAgent())
                    .referrer(jsoupProperties.getReferrer())
                    .timeout(5000)
                    .execute();

        } catch (IOException e) {
            log.debug("Connection error for site:  " + "\"" + siteEntity.getName() + "\"" + " by url: " + url);
            siteEntity.setStatusTime(Instant.now());
            siteEntity.setStatus(StatusString.FAILED);
            siteEntity.setLastError("Превышено ожидание от сервера. Индексацию выполнить не удалось");
        }
        return response;
    }

    private void randomSleepThread() {
        if (IndexingServiceImpl.isRunning) {
            Random random = new Random();
            int sleepTime = jsoupProperties.getSleepPageConnectorBasicVolume() + random.nextInt(jsoupProperties.getSleepPageConnectorAdditionalVolumeRandomTo());

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Document getDocumentByResponse(Connection.Response response) {
        try {
            return response.parse();
        } catch (IOException e) {
            return null;
        }
    }
}
