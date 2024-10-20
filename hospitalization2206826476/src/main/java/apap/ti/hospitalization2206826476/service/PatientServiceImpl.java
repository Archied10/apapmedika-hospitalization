package apap.ti.hospitalization2206826476.service;

import java.util.List;

import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import apap.ti.hospitalization2206826476.model.Patient;
import apap.ti.hospitalization2206826476.repository.PatientDb;
import jakarta.persistence.EntityManager;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientDb patientDb;
    private final EntityManager entityManager;

    public PatientServiceImpl(
        PatientDb patientDb,
        EntityManager entityManager
    ) {
        this.patientDb = patientDb;
        this.entityManager = entityManager;
    }

    @Override
    public Patient addPatient(Patient patient) {
        return patientDb.save(patient);
    }

    @Override
    public Patient getPatientByNIK(String patientNIK) {
        for (Patient patient: getAllPatient(false)) {
            if (patient.getNIK().equals(patientNIK)) {
                return patient;
            }
        }
        return null;
    }

    @Override
    public List<Patient> getAllPatient(boolean isDeleted) {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("deletedPatientFilter");
        filter.setParameter("isDeleted", isDeleted);
        List<Patient> patients =  patientDb.findAll();
        session.disableFilter("deletedPatientFilter");
        return patients;
    }

    @Override
    public int countPatient() {
        return getAllPatient(false).size();
    }
}
