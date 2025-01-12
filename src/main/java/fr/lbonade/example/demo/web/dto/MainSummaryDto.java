package fr.lbonade.example.demo.web.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class MainSummaryDto {

    String id;
    String name;

    Instant lastModified;
    Instant created;
}
