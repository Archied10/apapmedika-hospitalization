package apap.ti.hospitalization2206826476.model;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservation")
@SQLDelete(sql = "UPDATE reservation SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted IS NULL")
public class Reservation {
    @Id
    private String id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "date_in", nullable = false)
    private Date dateIn;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    @Column(name = "date_out", nullable = false)
    private Date dateOut;

    @NotNull
    @Column(name = "total_fee", nullable = false)
    private double totalFee;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @NotNull
    @Column(name = "assigned_nurse_id", nullable = false)
    private UUID assignedNurseID;

    @NotNull
    @Column(name = "room_id", nullable = false)
    private String roomId;

    @ManyToMany
    @JoinTable(name = "facility_reservation", joinColumns = @JoinColumn(name = "id_reservation"),
            inverseJoinColumns = @JoinColumn(name = "id_facility"))
    @SQLRestriction("is_deleted IS NULL")
    List<Facility> facilities;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", updatable = false, nullable = false)
    private Date createdDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", nullable = false)
    private Date updatedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;
}
