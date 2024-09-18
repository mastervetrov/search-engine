package searchengine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Entity
@Table(name = "site")
@Setter
@Getter
@Component
public class SiteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Integer id;
    @Column(name = "status", nullable = false)
    Enum<Status> status;
    @Column(name = "status_time", nullable = false)
    Instant statusTime;
    @Column(name = "last_error", columnDefinition = "TEXT")
    String lastError;
    @Column(name = "url", nullable = false, length = 255)
    String url;
    @Column(name = "name", nullable = false, length = 255)
    String name;
}
