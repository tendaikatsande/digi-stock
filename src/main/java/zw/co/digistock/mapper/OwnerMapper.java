package zw.co.digistock.mapper;

import org.mapstruct.*;
import zw.co.digistock.domain.Owner;
import zw.co.digistock.dto.request.RegisterOwnerRequest;
import zw.co.digistock.dto.response.OwnerResponse;

import java.util.List;

/**
 * MapStruct mapper for Owner entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OwnerMapper {

    /**
     * Map entity to response DTO
     */
    @Mapping(target = "fullName", expression = "java(owner.getFullName())")
    @Mapping(target = "livestockCount", ignore = true) // Set manually in service
    OwnerResponse toResponse(Owner owner);

    /**
     * Map list of entities to list of response DTOs
     */
    List<OwnerResponse> toResponseList(List<Owner> owners);

    /**
     * Map request DTO to entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "biometricEnrolled", constant = "false")
    @Mapping(target = "fingerprintRefs", ignore = true)
    @Mapping(target = "photoRef", ignore = true)
    @Mapping(target = "livestock", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    Owner toEntity(RegisterOwnerRequest request);

    /**
     * Update entity from request DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "biometricEnrolled", ignore = true)
    @Mapping(target = "fingerprintRefs", ignore = true)
    @Mapping(target = "photoRef", ignore = true)
    @Mapping(target = "livestock", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(RegisterOwnerRequest request, @MappingTarget Owner owner);
}
