package searchengine.service.indexing;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.service.text.LemmaProcessor;

import java.util.HashMap;

@Slf4j
public class SimpleAction {
    private static final Integer OK = 200;
    private final IndexingProcessor indexingProcessor;
    private final PageConnector connector;
    private final LemmaProcessor lemmaProcessor;
    private final PageEntity pageEntity;
    private final SiteEntity siteEntity;

    @Autowired
    public SimpleAction(IndexingProcessor indexingProcessor, PageConnector connector, LemmaProcessor lemmaProcessor, PageEntity pageEntity, SiteEntity siteEntity) {
        this.indexingProcessor = indexingProcessor;
        this.connector = connector;
        this.lemmaProcessor = lemmaProcessor;
        this.pageEntity = pageEntity;
        this.siteEntity = siteEntity;
    }

    protected boolean compute() {
        String urlLeftPart = siteEntity.getUrl();
        String urlRightPart = pageEntity.getPath();
        if (urlLeftPart.charAt(urlLeftPart.length() - 1) == '/') {
            urlLeftPart = urlLeftPart.substring(0, urlLeftPart.length() - 1);
        }
        String urlForConnect = urlLeftPart + urlRightPart;
        Connection.Response response = connector.tryGetConnectionResponse(urlForConnect, siteEntity);
        if (response == null) {
            System.out.println("false");
            return false;
        }
        Document html = connector.getDocumentByResponse(response);
        if (response != null && response.statusCode() == OK) {
            updatePageEntityByHtml(html);
            HashMap<String, Integer> lemmasAndRankHashMap = lemmaProcessor.extractLemmasAndRank(html);
            indexingProcessor.processing(lemmasAndRankHashMap, siteEntity, pageEntity);
        }
        return true;
    }

    private void updatePageEntityByHtml(Document html) {
        pageEntity.setCode(OK);
        pageEntity.setContent(html.toString());

        String url = pageEntity.getPath();
        String regexUrl = siteEntity.getUrl();

        regexUrl = regexUrl.replaceAll("http://www\\.", "http://");
        regexUrl = regexUrl.replaceAll("https://www\\.", "https://");
        url = url.replaceAll("http://www\\.", "http://");
        url = url.replaceAll("https://www\\.", "https://");

        regexUrl = Utils.makeShieldForSpecialCharacters(regexUrl);
        url = url.replaceAll(regexUrl, "////");
        url = url.replaceAll("/////", "/");
        url = url.replaceAll("////", "/");

        pageEntity.setPath(url);
        pageEntity.setSite(siteEntity);
    }
}
