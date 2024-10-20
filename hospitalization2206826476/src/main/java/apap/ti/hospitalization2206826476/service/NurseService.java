package apap.ti.hospitalization2206826476.service;

import java.util.List;
import java.util.UUID;

import apap.ti.hospitalization2206826476.model.Nurse;

public interface NurseService {
    Nurse addNurse(Nurse nurse);
    Nurse getNurseById(UUID nurseId);
    List<Nurse> getAllNurses(boolean isDeleted);
}
