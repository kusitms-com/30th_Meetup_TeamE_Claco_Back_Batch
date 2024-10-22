package com.curateme.clacobatchserver.service;

import com.curateme.clacobatchserver.entity.BeforeEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.batch.item.ItemReader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class KopisApiReader implements ItemReader<BeforeEntity> {

    private final RestTemplate restTemplate;
    private final String apiUrl = "http://www.kopis.or.kr/openApi/restful/pblprfr?service=f222668534db409b8769f640387de9c3";
    private int currentPage = 1;
    private List<BeforeEntity> beforeEntities = new ArrayList<>();
    private int index = 0;

    public KopisApiReader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BeforeEntity read() throws Exception {

        if (index >= beforeEntities.size()) {
            loadNextPage();
            index = 0;
        }

        if (beforeEntities.isEmpty()) {
            return null;
        }

        return beforeEntities.get(index++);
    }

    private void loadNextPage() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedStartDate = startDate.format(formatter);
        String formattedEndDate = endDate.format(formatter);

        String requestUrl = String.format("%s&stdate=%s&eddate=%s&rows=10&cpage=%d",
            apiUrl, formattedStartDate, formattedEndDate, currentPage++);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BeforeEntity[]> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, BeforeEntity[].class);

            if (response.getBody() != null && response.getBody().length > 0) {
                beforeEntities.clear();
                for (BeforeEntity Beforeentity : response.getBody()) {
                    BeforeEntity beforeEntity = new BeforeEntity();
                    beforeEntity.setConcertDetails(
                        Beforeentity.getMt20id(),
                        Beforeentity.getPrfnm(),
                        Beforeentity.getPrfpdfrom(),
                        Beforeentity.getPrfpdto(),
                        Beforeentity.getFcltynm(),
                        Beforeentity.getPoster(),
                        Beforeentity.getArea(),
                        Beforeentity.getGenrenm(),
                        Beforeentity.getOpenrun(),
                        Beforeentity.getPrfstate()
                    );

                    beforeEntities.add(beforeEntity);
                }
            } else {
                beforeEntities.clear();
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            beforeEntities.clear();
        }
    }
}
