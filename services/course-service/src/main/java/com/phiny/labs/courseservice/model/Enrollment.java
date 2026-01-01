package com.phiny.labs.courseservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phiny.labs.courseservice.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enrollments")
@RequiredArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;

    @Column(name = "updated")
    @UpdateTimestamp
    private Timestamp updated;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @Column(name = "student_id", nullable = false)
    @NonNull
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Course course;

    @AssertTrue(message = "Course Author cannot self enroll")
    public boolean ownerCannotSelfEnroll(){
        return this.studentId != this.course.getId();
    }

    @PrePersist
    public void initialiseStatus(){
        if(this.status==null){ this.status = EnrollmentStatus.ACTIVE;}
    }

}
