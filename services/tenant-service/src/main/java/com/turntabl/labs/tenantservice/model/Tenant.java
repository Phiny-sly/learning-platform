package com.turntabl.labs.tenantservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tenant")
@RequiredArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "created")
    @CreationTimestamp
    private Timestamp created;

    @Column(name = "updated")
    @UpdateTimestamp
    private Timestamp updated;

    @Column(name = "email", nullable = false, unique = true)
    @NonNull
    private String email;

    @Column(name = "phone", unique = true)
//    validate phone number, try with regex -> ^[0-9+][0-9]*$ or [0-9]{10}
    private String phone;

    @Column(name="is_active", columnDefinition = "boolean default false", nullable = false)
    private Boolean isActive = false;

}
