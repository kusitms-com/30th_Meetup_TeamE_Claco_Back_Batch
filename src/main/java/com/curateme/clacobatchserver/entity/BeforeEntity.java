package com.curateme.clacobatchserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BeforeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "concertId")
    private String mt20id;

    @Column(name = "concertName")
    private String prfnm;

    @Column(name = "startDate")
    private String prfpdfrom;

    @Column(name = "endDate")
    private String prfpdto;

    @Column(name = "facilityName")
    private String fcltynm;

    @Column(name = "poster")
    private String poster;

    @Column(name = "area")
    private String area;

    @Column(name = "genre")
    private String genrenm;

    @Column(name = "openrun")
    private String openrun;

    @Column(name = "status")
    private String prfstate;

    public void setConcertDetails(String mt20id, String prfnm, String prfpdfrom, String prfpdto,
        String fcltynm, String poster, String area, String genrenm,
        String openrun, String prfstate) {
        this.mt20id = mt20id;
        this.prfnm = prfnm;
        this.prfpdfrom = prfpdfrom;
        this.prfpdto = prfpdto;
        this.fcltynm = fcltynm;
        this.poster = poster;
        this.area = area;
        this.genrenm = genrenm;
        this.openrun = openrun;
        this.prfstate = prfstate;
    }
}
