package fr.lbonade.example.demo.business.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Data
@EqualsAndHashCode
@Entity(name = "main" )
@Table(name = "main")
@EntityListeners(AuditingEntityListener.class)
public class MainEntity {

    @Id
    private String id;

    @Version
    @Column(name = "version")
    private Long version;

    private String name;

    @LastModifiedDate
    private Instant lastModified;

    @CreatedDate
    private Instant created;

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "entity-id")
    private List<SubEntity> children;

}
