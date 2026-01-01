package com.phiny.labs.contentmanagement.config;

import com.phiny.labs.contentmanagement.dto.MultimediaDto;
import com.phiny.labs.contentmanagement.entity.Multimedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
@Component
public interface EntityMapper {
    EntityMapper INSTANCE = Mappers.getMapper(EntityMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "url", source = "url")
    MultimediaDto convertToMultimediaDto(Multimedia multimedia);
}
