package com.curateme.clacobatchserver.repository;

import com.curateme.clacobatchserver.entity.ConcertEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConcertRepository extends JpaRepository<ConcertEntity, Long> {
    @Query("SELECT c FROM ConcertEntity c LEFT JOIN FETCH c.categories")
    List<ConcertEntity> getAllConcertsWithCategories();
}
