package com.turntabl.labs.contentmanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Multimedia {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "url", nullable = false, unique = true)
    private String url;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private String createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private String updatedAt;

    @Column(name = "course_id")
    private String courseId;

    @Column(name = "lesson_id")
    private String lessonId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "description", length = 2000)
    private String description;

    public Multimedia(String title, MediaType mediaType, String url) {
        this.title = title;
        this.mediaType = mediaType;
        this.url = url;
    }

}
