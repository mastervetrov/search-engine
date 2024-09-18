package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexEntity;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, Integer> {
    List<IndexEntity> findByPageIdInAndLemmaIdIn(List<Integer> pageEntitiesIds, List<Integer> lemmaEntitiesIds);

    List<IndexEntity> findByPageIdInAndLemmaId(List<Integer> pagesIds, Integer lemmasId);

    List<IndexEntity> findByLemmaId(Integer id);

    List<IndexEntity> findByIdIn(List<Integer> ids);

    List<IndexEntity> findByPageId(Integer id);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE `index` AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM `index`", nativeQuery = true)
    void deleteAllCustom();

    void deleteByIdIn(List<Integer> indexIds);
}