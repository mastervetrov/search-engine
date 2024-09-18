package searchengine.service.data;

import org.springframework.stereotype.Component;

@Component
public interface ServiceGroup {
    SiteService getSiteService();

    PageService getPageService();

    LemmaService getLemmaService();

    IndexService getIndexService();
}
