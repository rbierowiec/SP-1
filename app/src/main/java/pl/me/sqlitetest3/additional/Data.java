package pl.me.sqlitetest3.additional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Data {
    private int rok;
    private int miesiac;
    private int dzien;
    private int godzina;
    private int minuta;
    private int sekunda;

    public Data(int rok, int miesiac, int dzien, int godzina, int minuta) {
        this.rok = rok;
        this.miesiac = miesiac;
        this.dzien = dzien;
        this.godzina = godzina;
        this.minuta = minuta;
        this.sekunda = sekunda;
    }

    public int getRok() {
        return rok;
    }

    public void setRok(int rok) {
        this.rok = rok;
    }

    public int getMiesiac() {
        return miesiac;
    }

    public void setMiesiac(int miesiac) {
        this.miesiac = miesiac;
    }

    public int getDzien() {
        return dzien;
    }

    public void setDzien(int dzien) {
        this.dzien = dzien;
    }

    public int getGodzina() {
        return godzina;
    }

    public void setGodzina(int godzina) {
        this.godzina = godzina;
    }

    public int getMinuta() {
        return minuta;
    }

    public void setMinuta(int minuta) {
        this.minuta = minuta;
    }

    public int getSekunda() {
        return sekunda;
    }

    public void setSekunda(int sekunda) {
        this.sekunda = sekunda;
    }

    public static long convertDataToMilis(String dataText) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
//        String text = this.rok + "-" + String.format("%02d",this.miesiac) + "-" + String.format("%02d",this.dzien) + " " + String.format("%02d",this.godzina) + ":" + String.format("%02d",this.minuta) + ":" + String.format("%02d",this.sekunda);

        Date date = null;
        try {
            date = format.parse(dataText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        return millis;
    }

    public static String convertMilisToData(long milis) {
        Date date = new Date(milis);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMAN);
        String dateFormatted = formatter.format(date);

        return dateFormatted;
    }

    @Override
    public String toString() {
        return "Data{" +
                "rok=" + rok +
                ", miesiac=" + miesiac +
                ", dzien=" + dzien +
                ", godzina=" + godzina +
                ", minuta=" + minuta +
                '}';
    }
}
