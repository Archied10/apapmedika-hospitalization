package apap.ti.hospitalization2206826476.service;

import java.util.List;

import apap.ti.hospitalization2206826476.model.Patient;

public interface PatientService {
    Patient addPatient(Patient patient);
    Patient getPatientByNIK(String patientNIK);
    List<Patient> getAllPatient(boolean isDeleted);
    int countPatient();
}
