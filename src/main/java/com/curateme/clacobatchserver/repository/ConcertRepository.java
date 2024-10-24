package com.curateme.clacobatchserver.repository;

import com.curateme.clacobatchserver.entity.ConcertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<ConcertEntity, Long> {

}
