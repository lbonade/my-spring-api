package fr.lbonade.example.demo.web.controller;

import fr.lbonade.example.demo.business.entity.MainEntity;
import fr.lbonade.example.demo.business.entity.MainSummaryEntity;
import fr.lbonade.example.demo.business.entity.VersionOnly;
import fr.lbonade.example.demo.infra.repository.JpaMainEntityRepository;
import fr.lbonade.example.demo.mappers.Mapper;
import fr.lbonade.example.demo.mappers.MapperImpl;
import fr.lbonade.example.demo.web.dto.MainDto;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping( path = "main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainControler {


    private final JpaMainEntityRepository mainRepository;

    private final Mapper mapper;


    public MainControler(JpaMainEntityRepository mainRepository) {
        this.mainRepository = mainRepository;
        mapper = new MapperImpl();
    }

    @GetMapping()
    @Transactional(readOnly = true)
    public ResponseEntity<List<MainSummaryEntity>> all() {
        return ResponseEntity.ok(
                StreamSupport.stream(mainRepository.findAllProjectedOnEntityLightBy(Pageable.unpaged()).spliterator(), false)
                        .toList());
    }

    @GetMapping( path = "/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<MainDto> get(WebRequest request, @PathVariable(name = "id", required = true) String id) {

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
                        ResponseEntity.ok()
                                .eTag(Long.toString(ent.getVersion()))
                                .body(mapper.toDto(ent)))
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
    public ResponseEntity<MainDto> post(@RequestBody MainDto mainDto) {
        mainDto.setId(UUID.randomUUID().toString());
        MainEntity mainEntity = mapper.ToEntity(mainDto);
        MainEntity ent = mainRepository.save(mainEntity);

        return ResponseEntity.created(
                        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                                .buildAndExpand(ent.getId()).toUri())
                .eTag(Long.toString(ent.getVersion()))
                .body(mapper.toDto(ent));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<MainDto> put(
            @PathVariable("id") String id,
            @RequestBody MainDto mainDto,
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

        mapper.updateEntityEntityFromEntity(mainDto, previous);
        MainEntity updated = mainRepository.saveAndFlush(previous);
        if (Objects.equals(updated.getVersion(), maybeVersion.orElse(""))) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).eTag(Long.toString(updated.getVersion())).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                        .eTag(Long.toString(updated.getVersion()))
                        .body(mapper.toDto(updated));
    }
}
