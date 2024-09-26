package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lemma")
@Getter
@Setter
public class LemmaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    SiteEntity site;
    @Column(name = "lemma", length = 255, nullable = false)
    String lemma;
    @Column(name = "frequency", nullable = false)
    Integer frequency;
}
