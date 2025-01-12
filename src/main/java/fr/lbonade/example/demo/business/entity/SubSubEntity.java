package fr.lbonade.example.demo.business.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Data
@EqualsAndHashCode
@Entity(name = "sub-sub-entity" )
@Table(name = "sub-sub-entity")
@EntityListeners(AuditingEntityListener.class)
public class SubSubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @LastModifiedDate
    private Instant lastModified;

    @CreatedDate
    private Instant created;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub-entity-id", updatable = false, nullable = false)
    private transient SubEntity parent;

}
