package zw.co.digistock.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import zw.co.digistock.domain.Vaccination;
import zw.co.digistock.dto.request.CreateVaccinationRequest;
import zw.co.digistock.dto.request.UpdateVaccinationRequest;
import zw.co.digistock.dto.response.VaccinationResponse;

/**
 * MapStruct mapper for converting between Vaccination entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VaccinationMapper {

    /**
     * Convert CreateVaccinationRequest to Vaccination entity.
     *
     * @param request The request DTO
     * @return The Vaccination entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinaryOfficer", ignore = true)
    @Mapping(target = "livestock", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Vaccination toEntity(CreateVaccinationRequest request);

    /**
     * Convert UpdateVaccinationRequest to Vaccination entity.
     *
     * @param request The request DTO
     * @param entity The existing entity to update
     * @return The updated Vaccination entity
     */
    @Mapping(target = "veterinaryOfficer", ignore = true)
    @Mapping(target = "livestock", ignore = true)
    @Mapping(target = "verifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateEntity(UpdateVaccinationRequest request, @MappingTarget Vaccination entity);

    /**
     * Convert Vaccination entity to VaccinationResponse DTO.
     *
     * @param entity The Vaccination entity
     * @return The response DTO
     */
    VaccinationResponse toResponse(Vaccination entity);
}
