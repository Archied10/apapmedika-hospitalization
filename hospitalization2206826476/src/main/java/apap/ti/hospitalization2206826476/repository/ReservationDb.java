package apap.ti.hospitalization2206826476.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import apap.ti.hospitalization2206826476.model.Reservation;

@Repository
public interface ReservationDb extends JpaRepository<Reservation, String>{
       List<Reservation> findAllByOrderByCreatedDateDesc();

       @Query("SELECT TO_CHAR(r.createdDate, 'Month') as month, COUNT(r) as count " +
              "FROM Reservation r " +
              "WHERE EXTRACT(YEAR FROM r.createdDate) = :year " +
              "GROUP BY EXTRACT(MONTH FROM r.createdDate), TO_CHAR(r.createdDate, 'Month') " +
              "ORDER BY EXTRACT(MONTH FROM r.createdDate)")
       List<Object[]> getMonthlyReservationCount(@Param("year") int year);

       @Query("SELECT CONCAT('Q', EXTRACT(QUARTER FROM r.createdDate)) as quarter, COUNT(r) as count " +
              "FROM Reservation r " +
              "WHERE EXTRACT(YEAR FROM r.createdDate) = :year " +
              "GROUP BY EXTRACT(QUARTER FROM r.createdDate) " +
              "ORDER BY EXTRACT(QUARTER FROM r.createdDate)")
       List<Object[]> getQuarterlyReservationCount(@Param("year") int year);
}
