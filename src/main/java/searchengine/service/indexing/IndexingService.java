package searchengine.service.indexing;

public interface IndexingService {
    void startIndexing();

    void indexPage(String url);

    void stopIndexing();
}
