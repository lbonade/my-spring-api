package fr.lbonade.example.demo.business.entity;

import java.time.Instant;

public interface MainSummaryEntity {

    String getId();
    String getName();

    Instant getLastModified();
    Instant getCreated();
}
