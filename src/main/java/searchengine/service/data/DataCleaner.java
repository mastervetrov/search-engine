package searchengine.service.data;

import org.springframework.stereotype.Service;

@Service
public interface DataCleaner {
    void cleanAllRepository();

    void cleanPageByUrl(String url);
}
