package fr.lbonade.example.demo.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@Jacksonized
public class SubSub {

    private int id;
    private String name;
    private Instant lastModified;
    private Instant created;

}
