package com.catsoftware.adisyon.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "siparislerTablosu")
public class SiparisSatiri {
    @PrimaryKey(autoGenerate = true)
    public int sId;

    @ColumnInfo(name = "saat")
    public int saat;

    @ColumnInfo(name = "dakika")
    public int dakika;

    @ColumnInfo(name = "surucu")
    public String surucu;

    @ColumnInfo(name = "odemeYontemi")
    public String odemeYontemi;

    @ColumnInfo(name = "ucret")
    public Double ucret;

    //TODO: getter ve setterlar gerekli mi?
    public int getSaat() {
        return saat;
    }

    public void setSaat(int saat) {
        this.saat = saat;
    }

    public int getDakika() {
        return dakika;
    }

    public void setDakika(int dakika) {
        this.dakika = dakika;
    }

    public String getSurucu() {
        return surucu;
    }

    public void setSurucu(String surucu) {
        this.surucu = surucu;
    }

    public String getOdemeYontemi() {
        return odemeYontemi;
    }

    public void setOdemeYontemi(String odemeYontemi) {
        this.odemeYontemi = odemeYontemi;
    }

    public Double getUcret() {
        return ucret;
    }

    public void setUcret(Double ucret) {
        this.ucret = ucret;
    }
}