package searchengine.service.indexing;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import searchengine.model.*;
import searchengine.service.text.LemmaProcessor;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RecursiveAction extends java.util.concurrent.RecursiveAction {
    private static AtomicInteger counter = new AtomicInteger();
    private static final int OK = 200;

    private final IndexingProcessor indexingProcessor;
    private final LemmaProcessor lemmaProcessor;
    private final PageConnector pageConnector;
    private final IndexingInformer informer;

    private PageEntity pageEntity;
    private SiteEntity siteEntity;
    private HashSet<String> processedUrls;

    public RecursiveAction(IndexingProcessor indexingProcessor, LemmaProcessor lemmaProcessor, PageConnector pageConnector, IndexingInformer informer) {
        this.indexingProcessor = indexingProcessor;
        this.lemmaProcessor = lemmaProcessor;
        this.pageConnector = pageConnector;
        this.informer = informer;

        IndexingManagerImpl.indexingTaskList.add(this);

        siteEntity = informer.getSiteEntity();
        pageEntity = informer.getPageEntity();
        processedUrls = informer.getProcessedUrls();
    }

    @Override
    protected void compute() {
        if (!IndexingManagerImpl.indexingIsAllow) {
            indexingProcessor.stopAction(siteEntity);
            return;
        }
        log.warn("Обработано задач: " + counter.incrementAndGet() + " current page: " + pageEntity.getPath());
        if (!processedUrls.contains(pageEntity.getPath())) {
            processedUrls.add(pageEntity.getPath());
        }
        Document html = null;
        Connection.Response response = pageConnector.tryGetConnectionResponse(pageEntity.getPath(), siteEntity);

        if (response != null && response.statusCode() == OK) {
            html = pageConnector.getDocumentByResponse(response);
            updatePageEntityByHtml(html);
            addChildPages(html);
            HashMap<String, Integer> lemmasHashMap = lemmaProcessor.extractLemmasAndRank(html);
            indexingProcessor.processing(lemmasHashMap, siteEntity, pageEntity);
            runNewActions();
        }
    }

    public static void resetCounter() {
        counter.set(0);
    }

    private boolean urlIsValid(String url, String domain) {
        return urlIsValidCondition1(url) && urlIsValidCondition2(url, domain);
    }

    private boolean urlIsValidCondition1(String url) {
        String[] parts = url.split("/");
        String result = parts[parts.length - 1];
        return result.contains(".html") || !result.contains(".");
    }


    private boolean urlIsValidCondition2(String link, String domain) {
        link = link.replaceAll("https://", "");
        link = link.replaceAll("http://", "");
        domain = domain.replaceAll("https://", "");
        domain = domain.replaceAll("http://", "");
        boolean linkLengthAllowed = link.length() >= domain.length();
        if (linkLengthAllowed) {
            return link.substring(0, domain.length()).matches(domain) && !link.contains("#");
        }
        return false;
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

    private synchronized void addChildPages(Document doc) {
        Elements elements = doc.body().select("a");
        Set<String> newUrls = new HashSet<>();
        for (Element element : elements) {
            String newUrl = element.absUrl("href");
            newUrl = newUrl.replaceAll("https://www.", "https://");
            newUrl = newUrl.replaceAll("http://www.", "http://");
            if (urlIsValid(newUrl, informer.getDomain()) && !processedUrls.contains(newUrl)) {
                newUrls.add(newUrl);
            }
        }
        for (String newUrl : newUrls) {
            if (newUrl.charAt(newUrl.length() - 1) == '/') {
                newUrl = newUrl.substring(0, newUrl.length() - 1);
            }
            if (processedUrls.contains(newUrl)) {
                continue;
            }
            informer.addChildTaskInformer(newUrl);
            processedUrls.add(newUrl);
            processedUrls.add(newUrl + "/");
        }
    }

    private void runNewActions() {
        List<RecursiveAction> newActions = createNewActions();
        for (RecursiveAction action : newActions) {
            if (!IndexingManagerImpl.indexingIsAllow) {
                indexingProcessor.stopAction(siteEntity);
                return;
            }
            action.invoke();
        }
    }

    private List<RecursiveAction> createNewActions() {
        List<RecursiveAction> newActions = new ArrayList<>();
        for (IndexingInformer childTaskInformer : informer.getChildTaskInformers()) {
            if (childTaskInformer.getCurrentLevel() <= 10) {
                RecursiveAction newAction = new RecursiveAction(indexingProcessor, lemmaProcessor, pageConnector, childTaskInformer);
                newActions.add(newAction);
            }
        }
        return newActions;
    }

}
