package fr.lbonade.example.demo.web.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
public class MainSummary extends RepresentationModel<MainSummary> {

    String id;
    String name;

    Instant lastModified;
    Instant created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}
