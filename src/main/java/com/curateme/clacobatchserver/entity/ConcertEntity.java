package com.curateme.clacobatchserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertEntity {

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

    @Column(name = "cast")
    private String prfcast;

    @Column(name = "crew")
    private String prfcrew;

    @Column(name = "runtime")
    private String prfruntime;

    @Column(name = "age")
    private String prfage;

    @Column(name = "companyName")
    private String entrpsnm;

    @Column(name = "companyNameP")
    private String entrpsnmP;

    @Column(name = "companyNameA")
    private String entrpsnmA;

    @Column(name = "companyNameH")
    private String entrpsnmH;

    @Column(name = "companyNameS")
    private String entrpsnmS;

    @Column(name = "seatGuidance")
    private String pcseguidance;

    @Column(name = "visit")
    private String visit;

    @Column(name = "child")
    private String child;

    @Column(name = "daehakro")
    private String daehakro;

    @Column(name = "festival")
    private String festival;

    @Column(name = "musicalLicense")
    private String musicallicense;

    @Column(name = "musicalCreate")
    private String musicalcreate;

    @Column(name = "updateDate")
    private String updatedate;

    @Column(name = "scheduleGuidance", length = 1000)
    private String dtguidance;

    @Column(name = "introduction")
    private String styurl;

    @ElementCollection
    @Column(name = "categories")
    private List<String> categories;

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

    public void setAdditionalConcertDetails(String prfcast, String prfcrew, String prfruntime, String prfage,
        String entrpsnm, String entrpsnmP, String entrpsnmA, String entrpsnmH,
        String entrpsnmS, String pcseguidance, String visit, String child,
        String daehakro, String festival, String musicallicense,
        String musicalcreate, String updatedate, String dtguidance) {
        this.prfcast = prfcast;
        this.prfcrew = prfcrew;
        this.prfruntime = prfruntime;
        this.prfage = prfage;
        this.entrpsnm = entrpsnm;
        this.entrpsnmP = entrpsnmP;
        this.entrpsnmA = entrpsnmA;
        this.entrpsnmH = entrpsnmH;
        this.entrpsnmS = entrpsnmS;
        this.pcseguidance = pcseguidance;
        this.visit = visit;
        this.child = child;
        this.daehakro = daehakro;
        this.festival = festival;
        this.musicallicense = musicallicense;
        this.musicalcreate = musicalcreate;
        this.updatedate = updatedate;
        this.dtguidance = dtguidance;
    }

    public void setStyurl(String styurl){
        this.styurl =styurl;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
