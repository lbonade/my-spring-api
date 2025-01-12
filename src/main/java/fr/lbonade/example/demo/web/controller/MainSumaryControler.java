package fr.lbonade.example.demo.web.controller;

import fr.lbonade.example.demo.business.entity.MainSummaryEntity;
import fr.lbonade.example.demo.infra.repository.JpaMainEntityRepository;
import fr.lbonade.example.demo.mappers.Mapper;
import fr.lbonade.example.demo.mappers.MapperImpl;
import fr.lbonade.example.demo.web.dto.MainSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.stream.StreamSupport;

@ExposesResourceFor(MainSummary.class)
@RestController
@RequestMapping( path = "main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainSumaryControler {


    private final JpaMainEntityRepository mainRepository;

    private final Mapper mapper;

    private final TypedEntityLinks<MainSummary> links;


    public MainSumaryControler(JpaMainEntityRepository mainRepository, EntityLinks entityLinks) {
        this.mainRepository = mainRepository;
        links = entityLinks.forType(MainSummary::getId);
        mapper = new MapperImpl();
    }

    @GetMapping()
    @Transactional(readOnly = true)
    public ResponseEntity<CollectionModel<MainSummary>> all(
            @RequestParam(name = "pageSize", defaultValue = "2") int pageSize,
            @RequestParam(name = "pageIndex", defaultValue = "0") int pageIndex
    ) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Order.asc( "name")));
        Page<MainSummaryEntity> readedPage = mainRepository.findAllProjectedOnEntityLightBy(pageable);
        Page<MainSummary> readedPageDto = readedPage.map(mapper::mainSummaryToDto);
        CollectionModel<MainSummary> res = CollectionModel.of(
                StreamSupport.stream(readedPageDto.spliterator(), false)
                        .map(m -> m.add(links.linkToItemResource(m)))
                        .toList());

        res.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MainSumaryControler.class).all(readedPageDto.getSize(), readedPage.getNumber())).withRel("self"));
        res.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MainSumaryControler.class).all(readedPageDto.getSize(), 0)).withRel("first"));
        res.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(MainSumaryControler.class).all(readedPageDto.getSize(), readedPage.getTotalPages()-1)).withRel("last"));
        if (readedPage.getNumber() - 1 >=0) {
            res.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(MainSumaryControler.class).all(readedPageDto.getSize(), readedPage.getNumber())).withRel("previous"));
        }
        if (readedPage.getNumber() +1 < readedPageDto.getTotalPages()) {
            res.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(MainSumaryControler.class).all(readedPageDto.getSize(), readedPage.getNumber() + 1)).withRel("next"));
        }

        return ResponseEntity.ok(res);
    }

}
