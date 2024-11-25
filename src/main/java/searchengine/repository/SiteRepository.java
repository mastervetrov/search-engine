package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteEntity;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Integer> {
    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE site_id_seq RESTART WITH 1", nativeQuery = true)
    void resetSequence();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM site", nativeQuery = true)
    void deleteAllCustom();

    SiteEntity findByUrl(String url);

    List<SiteEntity> findByIdIn(List<Integer> ids);
}
