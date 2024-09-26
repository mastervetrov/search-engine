package searchengine.service.data;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceGroupImpl implements ServiceGroup {
    private final SiteService siteService;
    private final PageService pageService;
    private final LemmaService lemmaService;
    private final IndexService indexService;

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
