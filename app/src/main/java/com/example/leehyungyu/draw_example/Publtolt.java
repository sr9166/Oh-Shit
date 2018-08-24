package com.example.leehyungyu.draw_example;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by LeeHyunGyu on 2018-05-06.
 */

public class Publtolt implements Parcelable {
    String PBCTLT_PLC_NM;                           //화장실명
    String REFINE_ROADNM_ADDR;      // 주소
    Double REFINE_WGS84_LAT; //위도
    Double REFINE_WGS84_LOGT; //경도
    Double Distance;    // 거리
    String MANAGE_INST_TELNO; // 전화번호
    String OPEN_TM_INFO;    // 개방시간

    public String getMANAGE_INST_TELNO() {
        return MANAGE_INST_TELNO;
    }

    public void setMANAGE_INST_TELNO(String MANAGE_INST_TELNO) {
        this.MANAGE_INST_TELNO = MANAGE_INST_TELNO;
    }

    public String getOPEN_TM_INFO() {
        return OPEN_TM_INFO;
    }

    public void setOPEN_TM_INFO(String OPEN_TM_INFO) {
        this.OPEN_TM_INFO = OPEN_TM_INFO;
    }

    public Double getDistance() {
        return Distance;
    }

    public void setDistance(Double distance) {
        Distance = distance;
    }

    public static Creator<Publtolt> getCREATOR() {
        return CREATOR;
    }

    public String getPBCTLT_PLC_NM() {
        return PBCTLT_PLC_NM;
    }

    public void setPBCTLT_PLC_NM(String PBCTLT_PLC_NM) {
        this.PBCTLT_PLC_NM = PBCTLT_PLC_NM;
    }

    public String getREFINE_ROADNM_ADDR() {
        return REFINE_ROADNM_ADDR;
    }

    public void setREFINE_ROADNM_ADDR(String REFINE_ROADNM_ADDR) {
        this.REFINE_ROADNM_ADDR = REFINE_ROADNM_ADDR;
    }

    public Double getREFINE_WGS84_LAT() {
        return REFINE_WGS84_LAT;
    }

    public void setREFINE_WGS84_LAT(Double REFINE_WGS84_LAT) {
        this.REFINE_WGS84_LAT = REFINE_WGS84_LAT;
    }

    public Double getREFINE_WGS84_LOGT() {
        return REFINE_WGS84_LOGT;
    }

    public void setREFINE_WGS84_LOGT(Double REFINE_WGS84_LOGT) {
        this.REFINE_WGS84_LOGT = REFINE_WGS84_LOGT;
    }

    public Publtolt() {

    }

    public Publtolt(String PBCTLT_PLC_NM, String REFINE_ROADNM_ADDR, Double REFINE_WGS84_LAT, Double REFINE_WGS84_LOGT, Double distance, String MANAGE_INST_TELNO, String OPEN_TM_INFO) {
        this.PBCTLT_PLC_NM = PBCTLT_PLC_NM;
        this.REFINE_ROADNM_ADDR = REFINE_ROADNM_ADDR;
        this.REFINE_WGS84_LAT = REFINE_WGS84_LAT;
        this.REFINE_WGS84_LOGT = REFINE_WGS84_LOGT;
        Distance = distance;
        this.MANAGE_INST_TELNO = MANAGE_INST_TELNO;
        this.OPEN_TM_INFO = OPEN_TM_INFO;
    }

    public Publtolt(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PBCTLT_PLC_NM);
        dest.writeString(REFINE_ROADNM_ADDR);
        dest.writeDouble(REFINE_WGS84_LAT);
        dest.writeDouble(REFINE_WGS84_LOGT);
        dest.writeDouble(Distance);
        dest.writeString(MANAGE_INST_TELNO);
        dest.writeString(OPEN_TM_INFO);
    }

    private void readFromParcel(Parcel in) {
        PBCTLT_PLC_NM = in.readString();
        REFINE_ROADNM_ADDR = in.readString();
        REFINE_WGS84_LAT = in.readDouble();
        REFINE_WGS84_LOGT = in.readDouble();
        Distance = in.readDouble();
        MANAGE_INST_TELNO = in.readString();
        OPEN_TM_INFO = in.readString();
    }

    public static final Parcelable.Creator<Publtolt> CREATOR = new Parcelable.Creator<Publtolt>() {
        @Override
        public Publtolt createFromParcel(Parcel in) {
            return new Publtolt(in);
        }

        @Override
        public Publtolt[] newArray(int size) {
            return new Publtolt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}

class DistanceCMP implements Comparator<Publtolt> {
    @Override
    public int compare(Publtolt publtolt1, Publtolt publtolt2) {
        return publtolt1.getDistance().compareTo(publtolt2.getDistance());
    }
}

class NameCMP implements Comparator<Publtolt> {
    @Override
    public int compare(Publtolt publtolt1, Publtolt publtolt2) {
        return publtolt1.getPBCTLT_PLC_NM().compareTo(publtolt2.getPBCTLT_PLC_NM());
    }
}
