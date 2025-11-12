package zw.co.digistock.mapper;

import org.mapstruct.*;
import zw.co.digistock.domain.Livestock;
import zw.co.digistock.dto.request.RegisterLivestockRequest;
import zw.co.digistock.dto.response.LivestockResponse;

import java.util.List;

/**
 * MapStruct mapper for Livestock entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface LivestockMapper {

    /**
     * Map entity to response DTO
     */
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "mother", source = "mother")
    @Mapping(target = "father", source = "father")
    @Mapping(target = "photos", source = "photos")
    LivestockResponse toResponse(Livestock livestock);

    /**
     * Map list of entities to list of response DTOs
     */
    List<LivestockResponse> toResponseList(List<Livestock> livestock);

    /**
     * Map request DTO to entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "mother", ignore = true)
    @Mapping(target = "father", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "offspringAsMother", ignore = true)
    @Mapping(target = "offspringAsFather", ignore = true)
    @Mapping(target = "clearances", ignore = true)
    @Mapping(target = "permits", ignore = true)
    @Mapping(target = "stolen", constant = "false")
    @Mapping(target = "stolenDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    Livestock toEntity(RegisterLivestockRequest request);

    /**
     * Update entity from request DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tagCode", ignore = true) // Tag code should not be updated
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "mother", ignore = true)
    @Mapping(target = "father", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "offspringAsMother", ignore = true)
    @Mapping(target = "offspringAsFather", ignore = true)
    @Mapping(target = "clearances", ignore = true)
    @Mapping(target = "permits", ignore = true)
    @Mapping(target = "stolen", ignore = true)
    @Mapping(target = "stolenDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(RegisterLivestockRequest request, @MappingTarget Livestock livestock);
}
