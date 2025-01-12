package fr.lbonade.example.demo.web.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;


@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Jacksonized
public class MainDto {

    private String id;
    private String name;
    private Instant lastModified;
    private Instant created;

    private List<SubDto> children;


}
