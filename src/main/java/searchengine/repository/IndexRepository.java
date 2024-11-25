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
    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE idx_id_seq RESTART WITH 1", nativeQuery = true)
    void resetSequence();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM idx", nativeQuery = true)
    void deleteAllCustom();

    List<IndexEntity> findByPageIdInAndLemmaIdIn(List<Integer> pageEntitiesIds, List<Integer> lemmaEntitiesIds);

    List<IndexEntity> findByPageIdInAndLemmaId(List<Integer> pagesIds, Integer lemmasId);

    List<IndexEntity> findByLemmaId(Integer id);

    List<IndexEntity> findByIdIn(List<Integer> ids);

    List<IndexEntity> findByPageId(Integer id);

    void deleteByIdIn(List<Integer> indexIds);
}