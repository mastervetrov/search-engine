package searchengine.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServiceGroupImpl implements ServiceGroup {
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;

    @Autowired
    public ServiceGroupImpl(SiteService siteService, PageService pageService, LemmaService lemmaService, IndexService indexService) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
        this.indexService = indexService;
    }

    @Override
    public SiteService getSiteService() {
        return siteService;
    }

    @Override
    public PageService getPageService() {
        return pageService;
    }

    @Override
    public LemmaService getLemmaService() {
        return lemmaService;
    }

    @Override
    public IndexService getIndexService() {
        return indexService;
    }
}
