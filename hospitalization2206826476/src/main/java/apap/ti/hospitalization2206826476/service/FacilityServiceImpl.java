package apap.ti.hospitalization2206826476.service;

import java.util.List;
import java.util.UUID;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Facility;
import apap.ti.hospitalization2206826476.repository.FacilityDb;
import jakarta.persistence.EntityManager;

@Service
public class FacilityServiceImpl implements FacilityService {
    private final FacilityDb facilityDb;
    private final EntityManager entityManager;

    public FacilityServiceImpl(
        FacilityDb facilityDb,
        EntityManager entityManager
    ) {
        this.facilityDb = facilityDb;
        this.entityManager = entityManager;
    }

    @Override
    public Facility addFacility(Facility facility) {
        return facilityDb.save(facility);
    }

    @Override
    public Facility getFacilityById(UUID facilityId) {
        for (Facility facility: getAllFacilities(false)) {
            if (facility.getId().equals(facilityId)) {
                return facility;
            }
        }

        return null;
    }

    @Override
    public List<Facility> getAllFacilities(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedFacilityFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Facility> facilities = facilityDb.findAll();
        session.disableFilter("deletedFacilityFilter");
        return facilities;
    }
}
