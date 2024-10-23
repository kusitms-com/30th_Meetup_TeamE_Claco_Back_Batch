package com.curateme.clacobatchserver.service;

import com.curateme.clacobatchserver.entity.ConcertEntity;
import com.curateme.clacobatchserver.repository.ConcertRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ConcertCategoryExtractor {

    private final RestTemplate restTemplate;
    private final ConcertRepository concertRepository;

    @Autowired
    public ConcertCategoryExtractor(RestTemplate restTemplate, ConcertRepository concertRepository) {
        this.restTemplate = restTemplate;
        this.concertRepository = concertRepository;
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<ConcertEntity> concerts = concertRepository.findAll();

        for (ConcertEntity concert : concerts) {
            String introduction = concert.getStyurl();

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("image_url", introduction);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:8080/categories", requestEntity, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Map<String, Object> clovaResponse = (Map<String, Object>) responseBody.get("clova_response");

                List<String> categories = (List<String>) clovaResponse.get("categories");
                concert.setCategories(categories);

                concertRepository.save(concert);
            }
        }
        return RepeatStatus.FINISHED;
    }

}
