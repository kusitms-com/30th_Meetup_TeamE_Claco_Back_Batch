package com.curateme.clacobatchserver.service;

import com.curateme.clacobatchserver.entity.BeforeEntity;
import com.curateme.clacobatchserver.repository.BeforeRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class KopisEntityWriter implements ItemWriter<BeforeEntity> {

    private final BeforeRepository beforeRepository;

    public KopisEntityWriter(BeforeRepository beforeRepository) {

        this.beforeRepository = beforeRepository;
    }

    @Override
    public void write(Chunk<? extends BeforeEntity> items) throws Exception {

        beforeRepository.saveAll(items.getItems());
    }


}