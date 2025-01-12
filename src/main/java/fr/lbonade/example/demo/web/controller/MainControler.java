package fr.lbonade.example.demo.web.controller;

import fr.lbonade.example.demo.business.entity.MainEntity;
import fr.lbonade.example.demo.business.entity.VersionOnly;
import fr.lbonade.example.demo.infra.repository.JpaMainEntityRepository;
import fr.lbonade.example.demo.mappers.Mapper;
import fr.lbonade.example.demo.mappers.MapperImpl;
import fr.lbonade.example.demo.web.dto.Main;
import fr.lbonade.example.demo.web.dto.MainSummary;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ExposesResourceFor(Main.class)
@RestController
@RequestMapping( path = "main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainControler {


    private final JpaMainEntityRepository mainRepository;

    private final Mapper mapper;

    private final TypedEntityLinks<MainSummary> links;


    public MainControler(JpaMainEntityRepository mainRepository, EntityLinks entityLinks) {
        this.mainRepository = mainRepository;
        links = entityLinks.forType(MainSummary::getId);
        mapper = new MapperImpl();
    }

    @GetMapping( path = "/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Main> get(WebRequest request, @PathVariable(name = "id", required = true) String id) {

        Optional<String> maybeVersion = mainRepository.findVersionById(id)
                .map(VersionOnly::version)
                .map(v -> Long.toString(v));

        boolean notModified = maybeVersion.map(version-> request.checkNotModified(version)).orElse(false);
        if (notModified) {
            String version = maybeVersion.orElseThrow();
            return ResponseEntity
                    .status(HttpStatus.NOT_MODIFIED.value())
                    .eTag(version).build();
        }


        Optional<MainEntity> maybeEntity = mainRepository.findById(id);
        return maybeEntity
                .map(ent ->
                        {
                            Main dto = mapper.mainToDto(ent);
                            dto.add(links.linkToItemResource(dto));
                            return ResponseEntity.ok()
                                    .eTag(Long.toString(ent.getVersion()))
                                    .body(dto);
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping( path = "/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable(name = "id", required = true) String id, @RequestHeader(HttpHeaders.ETAG) String eTag) {

        Optional<String> maybeVersion = mainRepository.findVersionById(id)
                .map(VersionOnly::version)
                .map(v -> String.format("\"%d\"",v));

        boolean isSameETag = maybeVersion.map(version-> Objects.equals(version, eTag)).orElse(false);
        if (!isSameETag) {
            // TODO Throw à ConcurentModificationException
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }
        String version = maybeVersion.orElseThrow();
        mainRepository.deleteById(id);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Main> post(@RequestBody Main mainDto) {
        mainDto.setId(UUID.randomUUID().toString());
        MainEntity mainEntity = mapper.mainToEntity(mainDto);
        MainEntity ent = mainRepository.save(mainEntity);

        Main dto = mapper.mainToDto(ent);
        Link link = links.linkToItemResource(dto);
        dto.add(link);
        return ResponseEntity.created(link.toUri())
                .eTag(Long.toString(ent.getVersion()))
                .body(dto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Main> put(
            @PathVariable("id") String id,
            @RequestBody Main mainDto,
            @RequestHeader(HttpHeaders.ETAG) String eTag) {
        // Check Id tare identical //FIXEME
        if (mainDto.getId() == null || !Objects.equals(mainDto.getId(),id)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<String> maybeVersion = mainRepository.findVersionById(id)
                .map(VersionOnly::version)
                .map(v -> String.format("\"%d\"", v));

        if (maybeVersion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        boolean isSameETag = maybeVersion.map(version-> Objects.equals(version, eTag)).orElse(false);
        if (!isSameETag) {
            // TODO Throw à ConcurentModificationException
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).build();
        }


        MainEntity previous = mainRepository.findById(id).orElseThrow();

        mapper.updateMainEntityFromDto(mainDto, previous);
        MainEntity updated = mainRepository.saveAndFlush(previous);
        if (Objects.equals(updated.getVersion(), maybeVersion.orElse(""))) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(Long.toString(updated.getVersion())).build();
        }

        Main dto = mapper.mainToDto(updated);
        dto.add(links.linkToItemResource(dto));
        return ResponseEntity.status(HttpStatus.OK)
                        .eTag(Long.toString(updated.getVersion()))
                        .body(dto);
    }
}
