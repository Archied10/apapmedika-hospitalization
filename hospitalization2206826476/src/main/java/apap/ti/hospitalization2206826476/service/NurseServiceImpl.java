package apap.ti.hospitalization2206826476.service;

import java.util.List;
import java.util.UUID;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Nurse;
import apap.ti.hospitalization2206826476.repository.NurseDb;
import jakarta.persistence.EntityManager;

@Service
public class NurseServiceImpl implements NurseService {
    private final NurseDb nurseDb;
    private final EntityManager entityManager;

    public NurseServiceImpl(
        NurseDb nurseDb,
        EntityManager entityManager
    ) {
        this.nurseDb = nurseDb;
        this.entityManager = entityManager;
    }

    @Override
    public Nurse addNurse(Nurse nurse) {
        return nurseDb.save(nurse);
    }

    @Override
    public Nurse getNurseById(UUID nurseId) {
        for (Nurse nurse: getAllNurses(false)) {
            if (nurse.getId().equals(nurseId)) {
                return nurse;
            }
        }

        return null;
    }

    @Override
    public List<Nurse> getAllNurses(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedNurseFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Nurse> nurses = nurseDb.findAll();
        session.disableFilter("deletedNurseFilter");
        return nurses;
    }
}
