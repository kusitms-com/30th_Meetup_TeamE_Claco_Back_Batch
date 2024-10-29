package com.curateme.clacobatchserver.repository;

import com.curateme.clacobatchserver.entity.ConcertCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertCategoryRepository extends JpaRepository<ConcertCategory, Long> {

}
