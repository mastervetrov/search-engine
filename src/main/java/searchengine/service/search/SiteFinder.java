package searchengine.service.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import searchengine.model.SiteEntity;
import searchengine.repository.SiteRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SiteFinder {
    private final SiteRepository siteJpaRepository;

    public SiteEntity findByUrl(String url) {
        return siteJpaRepository.findByUrl(url);
    }

    public List<SiteEntity> findAll() {
        return siteJpaRepository.findAll();
    }
}
