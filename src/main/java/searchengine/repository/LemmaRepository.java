package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaEntity;

import java.util.HashSet;
import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {
    List<LemmaEntity> findBySiteIdInAndLemmaInAndFrequencyLessThanEqual(List<Integer> siteIds, HashSet<String> lemmas, double frequency);

    LemmaEntity findByLemmaAndSiteId(String lemma, Integer siteId);

    List<LemmaEntity> findByIdIn(List<Integer> ids);

    Long countBySiteId(Integer id);

    void deleteByIdIn(List<Integer> lemmasIds);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE lemma AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM lemma", nativeQuery = true)
    void deleteAllCustom();


}
