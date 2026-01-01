package com.phiny.labs.courseservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phiny.labs.courseservice.enums.CourseStatus;
import com.phiny.labs.courseservice.util.Util;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
@RequiredArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "title", nullable = false)
    @NonNull
    private String title;

    @Column(name = "description", nullable = false, length = 10000)
    private String description;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "start_date")
    private Timestamp startDate;

    @Column(name = "end_date")
    private Timestamp endDate;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.UPCOMING;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;

    @Column(name = "updated")
    @UpdateTimestamp
    private Timestamp updated;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "course_tags", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "course_categories", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @OneToMany(mappedBy="course")
    @JsonIgnore
    private Set<Rating> ratings;

    @OneToMany(mappedBy="course")
    @JsonIgnore
    private Set<Topic> topics;

    @PrePersist
    public void generateCourseCode(){
        if(this.code==null){ this.code = Util.randomCodeGenerator();}
    }

    @Formula("(select coalesce(avg(cr.rating), 0.0) from course_ratings cr where cr.course_id=id)")
    private double averageRating = 0;

}
