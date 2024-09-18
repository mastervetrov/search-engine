package searchengine.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.LemmaRepository;

import java.util.List;

@Service
public class LemmaServiceImpl implements LemmaService {
    private final LemmaRepository lemmaJpaRepository;
    private final SiteService siteService;

    @Autowired
    public LemmaServiceImpl(LemmaRepository lemmaJpaRepository, SiteService siteService) {
        this.lemmaJpaRepository = lemmaJpaRepository;
        this.siteService = siteService;
    }

    @Override
    public void save(LemmaEntity lemmaEntity) {
        lemmaJpaRepository.save(lemmaEntity);
    }

    @Override
    public void saveAll(List<LemmaEntity> lemmaEntities) {
        lemmaJpaRepository.saveAll(lemmaEntities);
    }

    @Override
    public void deleteById(Integer id) {
        lemmaJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        lemmaJpaRepository.deleteAll();
    }

    @Override
    public LemmaEntity findById(Integer id) {
        return lemmaJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<LemmaEntity> findByIdIn(List<Integer> ids) {
        return lemmaJpaRepository.findByIdIn(ids);
    }

    @Override
    public List<LemmaEntity> findAll() {
        return lemmaJpaRepository.findAll();
    }

    @Override
    public LemmaEntity mapDtoToEntity(LemmaDto lemmaDto) {
        SiteDto siteDto = lemmaDto.getSiteDto();
        SiteEntity siteEntity = siteService.mapDtoToEntity(siteDto);

        LemmaEntity lemmaEntity = new LemmaEntity();
        lemmaEntity.setSite(siteEntity);
        lemmaEntity.setLemma(lemmaDto.getLemma());
        ;
        lemmaEntity.setId(lemmaDto.getId());
        lemmaEntity.setFrequency(lemmaDto.getFrequency());
        return lemmaEntity;
    }

    @Override
    public LemmaDto mapEntityToDto(LemmaEntity lemmaEntity) {
        SiteEntity siteEntity = lemmaEntity.getSite();
        SiteDto siteDto = siteService.mapEntityToDto(siteEntity);

        LemmaDto lemmaDto = new LemmaDto();
        lemmaDto.setSiteDto(siteDto);
        lemmaDto.setId(lemmaEntity.getId());
        lemmaDto.setLemma(lemmaEntity.getLemma());
        lemmaDto.setFrequency(lemmaEntity.getFrequency());
        return lemmaDto;
    }

    @Override
    public LemmaEntity findByLemmaAndSiteId(String lemma, Integer siteId) {
        return lemmaJpaRepository.findByLemmaAndSiteId(lemma, siteId);
    }

    @Override
    public void decreaseFrequencyOrDeleteLemma(Integer id) {
        LemmaEntity lemmaEntity = lemmaJpaRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Lemma not found with id: " + id));
        lemmaEntity.setFrequency(lemmaEntity.getFrequency() - 1);
        if (lemmaEntity.getFrequency() == 0) {
            lemmaJpaRepository.deleteById(id);
        } else lemmaJpaRepository.save(lemmaEntity);
    }
}
