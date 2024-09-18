package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "`index`")
@Getter
@Setter
public class IndexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;
    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    PageEntity page;
    @ManyToOne
    @JoinColumn(name = "lemma_id", nullable = false)
    LemmaEntity lemma;
    @Column(name = "`rank`", nullable = false)
    Float rank;
}
