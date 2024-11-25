package searchengine.service.indexing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.exception.IndexingIsAlreadyRunningException;
import searchengine.exception.IndexingIsAlreadyStoppedException;
import searchengine.exception.PageForIndexationIsOutsideTheConfigurationFileException;
import searchengine.service.data.DataCleaner;
import searchengine.model.SiteEntity;
import searchengine.model.Status;
import searchengine.service.data.SiteService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    public static boolean isRunning = false;
    private final IndexingManager manager;
    private final DataCleaner dataCleaner;
    private final SiteService siteService;

    @Autowired
    private SitesList sitesList;

    /**
     * This method performs preparation and passes a list of entities from SitesList to the IndexingTaskManager
     */
    @Override
    public void startIndexing() {
        if (isRunning) {
            throw new IndexingIsAlreadyRunningException();
        }
        isRunning = true;
        List<SiteEntity> siteEntityList = preparation(sitesList);
        manager.start(siteEntityList);
    }

    @Override
    public void indexPage(String targetUrl) {
        if (isRunning) {
            throw new IndexingIsAlreadyRunningException();
        }
        isRunning = true;
        boolean linkAllow = false;
        String decodeRequest = URLDecoder.decode(targetUrl, StandardCharsets.UTF_8);
        String decodeTargetUrl = decodeRequest.replaceAll("url=", "");

        manager.cleanAll();
        String linkDomain = Utils.generateDomain(decodeTargetUrl);
        String targetSite = null;
        for (Site site : sitesList.getSites()) {
            String siteDomain = Utils.generateDomain(site.getUrl());
            if (linkDomain.equals(siteDomain)) {
                linkAllow = true;
                targetSite = site.getUrl();
                break;
            }
        }
        SiteEntity siteEntity = siteService.findByUrl(targetSite);
        if (linkAllow) {
            preparationByUrl(decodeTargetUrl);
            boolean result =  manager.indexPage(siteEntity, decodeTargetUrl);
            if (result) {
                return;
            }
        }
        throw new PageForIndexationIsOutsideTheConfigurationFileException();
    }

    /**
     * This method:
     * 1) Stops indexing
     */
    @Override
    public void stopIndexing() {
        if (!isRunning) {
            throw new IndexingIsAlreadyStoppedException();
        }
        isRunning = false;
        manager.stop();
    }

    /**
     * This method checks for indexation
     * 1) isRunning = true indexing is running
     * 2) isRunning = false indexing is not running
     *
     * @return boolean isRunning
     */
    @Override
    public boolean isRunning() {
        if (isRunning) {
            throw new IndexingIsAlreadyRunningException();
        }
        return isRunning;
    }

    private List<SiteEntity> preparation(SitesList sitesList) {
        dataCleaner.cleanAllRepository();
        manager.cleanAll();
        List<Site> siteListOfConfig = sitesList.getSites();
        List<SiteEntity> siteEntityList = getSiteEntityListAndSaveInDatabase(siteListOfConfig);
        return siteEntityList;
    }

    private void preparationByUrl(String url) {
        dataCleaner.cleanPageByUrl(url);
    }

    private List<SiteEntity> getSiteEntityListAndSaveInDatabase(List<Site> siteList) {
        List<SiteEntity> siteEntityList = new ArrayList<>();
        for (Site site : siteList) {
            SiteEntity siteEntity = new SiteEntity();
            siteEntity.setName(site.getName());
            siteEntity.setUrl(site.getUrl());
            siteEntity.setStatus(Status.INDEXING);
            siteEntity.setStatusTime(Instant.now());
            siteEntity.setLastError("");
            siteService.save(siteEntity);
            siteEntityList.add(siteEntity);
        }
        return siteEntityList;
    }
}
