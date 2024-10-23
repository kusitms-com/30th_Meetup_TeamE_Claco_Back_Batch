package com.curateme.clacobatchserver.service;

import com.curateme.clacobatchserver.entity.ConcertEntity;
import com.curateme.clacobatchserver.repository.ConcertRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class KopisEntityWriter implements ItemWriter<ConcertEntity> {

    private final ConcertRepository concertRepository;

    public KopisEntityWriter(ConcertRepository concertRepository) {

        this.concertRepository = concertRepository;
    }

    @Override
    public void write(Chunk<? extends ConcertEntity> items) throws Exception {

        concertRepository.saveAll(items.getItems());
    }


}