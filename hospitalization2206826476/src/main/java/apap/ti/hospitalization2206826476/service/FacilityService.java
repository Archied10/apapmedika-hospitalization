package apap.ti.hospitalization2206826476.service;

import java.util.List;
import java.util.UUID;

import apap.ti.hospitalization2206826476.model.Facility;

public interface FacilityService {
    Facility addFacility(Facility facility);
    Facility getFacilityById(UUID facilityId);
    List<Facility> getAllFacilities(boolean isDeleted);
}
