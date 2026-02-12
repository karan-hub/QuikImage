package com.image.quickimage.image.repository;

import com.image.quickimage.image.model.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity ,Long> {
    Optional<ImageEntity> findByName(String name);

    boolean existsByName(String baseName);
}
