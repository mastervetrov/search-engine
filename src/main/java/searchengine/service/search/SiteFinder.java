package searchengine.service.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.model.SiteEntity;
import searchengine.repository.SiteRepository;

import java.util.List;

@Component
public class SiteFinder {
    private final SiteRepository siteJpaRepository;

    @Autowired
    public SiteFinder(SiteRepository siteJpaRepository) {
        this.siteJpaRepository = siteJpaRepository;
    }

    public SiteEntity findByUrl(String url) {
        return siteJpaRepository.findByUrl(url);
    }

    public List<SiteEntity> findAll() {
        return siteJpaRepository.findAll();
    }
}
