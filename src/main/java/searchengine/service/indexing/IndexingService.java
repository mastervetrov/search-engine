package searchengine.service.indexing;

public interface IndexingService {
    void startIndexing();

    boolean indexPage(String url);

    void stopIndexing() throws InterruptedException;

    boolean isRunning();
}
