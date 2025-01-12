package fr.lbonade.example.demo.mappers;

import fr.lbonade.example.demo.business.entity.MainEntity;
import fr.lbonade.example.demo.business.entity.MainSummaryEntity;
import fr.lbonade.example.demo.business.entity.SubEntityEntity;
import fr.lbonade.example.demo.web.dto.MainDto;
import fr.lbonade.example.demo.web.dto.MainSummaryDto;
import fr.lbonade.example.demo.web.dto.SubDto;
import org.mapstruct.*;

import java.util.List;
import java.util.Optional;

@org.mapstruct.Mapper(collectionMappingStrategy = CollectionMappingStrategy.ACCESSOR_ONLY)
public interface Mapper {


    @Mapping(target = ".", source = ".")
    MainSummaryDto toDto(MainSummaryEntity entity);

    @Mapping(source = ".", target = ".")
    MainDto toDto(MainEntity dto);

    @Mapping(source = ".", target = ".")
    SubDto toDto(SubEntityEntity subEntity);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "children", source = "children", qualifiedByName = "subEntityToDtoList")
    void updateEntityEntityFromEntity(MainDto mainDto, @MappingTarget MainEntity mainEntity);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "children", source = "children", qualifiedByName = "subEntityToDtoList")
    MainEntity ToEntity(MainDto mainDto);

    @IterableMapping(qualifiedByName = "subEntityToSubEntityEntity")
    @Named("subEntityToDtoList")
    List<SubEntityEntity> toEntityList(List<SubDto> children);

    @Mapping(target = ".", source = ".")
    @Named("subEntityToSubEntityEntity")
    SubEntityEntity toEntity(SubDto entity);

    @AfterMapping
    default void setEntityEntity(@MappingTarget MainEntity mainEntity) {

        Optional.ofNullable(mainEntity.getChildren())
                .ifPresent(it -> it.forEach(item -> item.setParent(mainEntity)));
    }

}
