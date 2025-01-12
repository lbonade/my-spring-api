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
@Entity(name = "sub-entity" )
@Table(name = "sub-entity")
@EntityListeners(AuditingEntityListener.class)
public class SubEntityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @LastModifiedDate
    private Instant lastModified;

    @CreatedDate
    private Instant created;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity-id", updatable = false, nullable = false)
    private transient MainEntity parent;
}
