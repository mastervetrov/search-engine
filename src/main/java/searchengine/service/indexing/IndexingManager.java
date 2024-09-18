package searchengine.service.indexing;

import org.springframework.stereotype.Component;
import searchengine.model.SiteEntity;

import java.util.List;

@Component
public interface IndexingManager {
    void start(List<SiteEntity> siteEntityList);

    void stop();

    void cleanAll();

    boolean indexPage(SiteEntity siteEntity, String url);
}
