package fr.lbonade.example.demo.mappers;

import fr.lbonade.example.demo.business.entity.MainEntity;
import fr.lbonade.example.demo.business.entity.MainSummaryEntity;
import fr.lbonade.example.demo.business.entity.SubEntity;
import fr.lbonade.example.demo.business.entity.SubSubEntity;
import fr.lbonade.example.demo.web.dto.*;
import fr.lbonade.example.demo.web.dto.Main;
import fr.lbonade.example.demo.web.dto.MainSummary;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

@org.mapstruct.Mapper(collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY)
public interface Mapper {


    @Mapping(target = ".", source = ".")
    MainSummary mainSummaryToDto(MainSummaryEntity entity);

    @Mapping(source = ".", target = ".")
    Main mainToDto(MainEntity dto);

    @Mapping(source = ".", target = ".")
    Sub subToDto(SubEntity subEntity);

    @Mapping(source = ".", target = ".")
    SubSub subSubToDto(SubSubEntity subEntity);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "children", source = "children", qualifiedByName = "subToEntityList")
    void updateMainEntityFromDto(Main mainDto, @MappingTarget MainEntity mainEntity);


    @Mapping(target = ".", source = ".")
    @Mapping(target = "children", source = "children", qualifiedByName = "subToEntityList")
    MainEntity mainToEntity(Main mainDto);

    @Named("subToEntityList")
    @IterableMapping(qualifiedByName = "subToEntity")
    List<SubEntity> subToEntityList(List<Sub> children);

    @Named("subToEntity")
    @Mapping(target = ".", source = ".")
    @Mapping(target = "children", source = "children", qualifiedByName = "subSubToEntityList")
    SubEntity subToEntity(Sub entity);

    @Named("subSubToEntityList")
    @IterableMapping(qualifiedByName = "subSubToEntity")
    List<SubSubEntity> subSubToEntityList(List<SubSub> children);

    @Named("subSubToEntity")
    @Mapping(target = ".", source = ".")
    SubSubEntity subSubToEntity(SubSub entity);

    @AfterMapping
    default void setEntityEntity(@MappingTarget MainEntity mainEntity) {

        Optional.ofNullable(mainEntity.getChildren())
                .ifPresent(it -> it.forEach(item -> item.setParent(mainEntity)));
    }

}
