package fr.lbonade.example.demo.web.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@Jacksonized
public class SubDto {

    private int id;
    private String name;
    private Instant lastModified;
    private Instant created;

}
