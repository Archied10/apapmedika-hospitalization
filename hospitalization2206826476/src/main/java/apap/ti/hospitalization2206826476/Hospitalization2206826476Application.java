package apap.ti.hospitalization2206826476;

import apap.ti.hospitalization2206826476.model.Facility;
import apap.ti.hospitalization2206826476.model.Nurse;
import apap.ti.hospitalization2206826476.service.FacilityService;
import apap.ti.hospitalization2206826476.service.NurseService;
import jakarta.transaction.Transactional;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

@SpringBootApplication
public class Hospitalization2206826476Application {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(Hospitalization2206826476Application.class, args);
    }

    @Bean
	@Transactional
    public CommandLineRunner run(NurseService nurseService, FacilityService facilityService) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));

            // Create fake nurses
			var nurse = new Nurse();
			nurse.setId(UUID.randomUUID());
			nurse.setName(faker.name().fullName());
			nurse.setEmail(faker.internet().emailAddress());
			nurse.setGender(faker.bool().bool());
            nurseService.addNurse(nurse);

            // Create fake facility
			var facility = new Facility();
			facility.setId(UUID.randomUUID());
			facility.setName(faker.food().dish());
			facility.setFee(faker.number().randomDouble(2, 10, 100));
            facilityService.addFacility(facility);
        };
    }
}