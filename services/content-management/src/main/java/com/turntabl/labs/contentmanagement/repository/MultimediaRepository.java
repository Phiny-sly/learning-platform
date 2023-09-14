package com.turntabl.labs.contentmanagement.repository;

import com.turntabl.labs.contentmanagement.entity.Multimedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MultimediaRepository extends JpaRepository<Multimedia, Long> {
    Optional<Multimedia> findByUrl(String url);
}
