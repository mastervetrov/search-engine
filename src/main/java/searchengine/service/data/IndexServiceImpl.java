package searchengine.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.data.IndexDto;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.IndexRepository;

import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {
    private final IndexRepository indexJpaRepository;
    private final PageService pageService;
    private final LemmaService lemmaService;

    @Autowired
    public IndexServiceImpl(IndexRepository indexJpaRepository, PageService pageService, LemmaService lemmaService) {
        this.indexJpaRepository = indexJpaRepository;
        this.pageService = pageService;
        this.lemmaService = lemmaService;
    }

    @Override
    public void save(IndexEntity indexEntity) {
        indexJpaRepository.save(indexEntity);
    }

    @Override
    public void saveAll(List<IndexEntity> indexEntityList) {
        indexJpaRepository.saveAll(indexEntityList);
    }

    @Override
    public void deleteById(Integer id) {
        indexJpaRepository.findById(id);
    }

    @Override
    public void deleteAll() {
        indexJpaRepository.deleteAll();
    }

    @Override
    public IndexEntity findById(Integer id) {
        return indexJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<IndexEntity> findByIdIn(List<Integer> ids) {
        return indexJpaRepository.findByIdIn(ids);
    }

    @Override
    public List<IndexEntity> findAll() {
        return indexJpaRepository.findAll();
    }

    @Override
    public IndexEntity mapDtoToEntity(IndexDto indexDto) {
        PageDto pageDto = indexDto.getPageDto();
        LemmaDto lemmaDto = indexDto.getLemmaDto();
        PageEntity pageEntity = pageService.mapDtoToEntity(pageDto);
        LemmaEntity lemmaEntity = lemmaService.mapDtoToEntity(lemmaDto);

        IndexEntity indexEntity = new IndexEntity();
        indexEntity.setPage(pageEntity);
        indexEntity.setLemma(lemmaEntity);
        indexEntity.setId(indexDto.getId());
        indexEntity.setRank(indexDto.getRank());
        return indexEntity;
    }

    @Override
    public IndexDto mapEntityToDto(IndexEntity indexEntity) {
        LemmaEntity lemmaEntity = indexEntity.getLemma();
        PageEntity pageEntity = indexEntity.getPage();
        PageDto pageDto = pageService.mapEntityToDto(pageEntity);
        LemmaDto lemmaDto = lemmaService.mapEntityToDto(lemmaEntity);

        IndexDto indexDto = new IndexDto();
        indexDto.setLemmaDto(lemmaDto);
        indexDto.setPageDto(pageDto);
        indexDto.setId(indexEntity.getId());
        indexDto.setRank(indexEntity.getRank());
        return indexDto;
    }
}
