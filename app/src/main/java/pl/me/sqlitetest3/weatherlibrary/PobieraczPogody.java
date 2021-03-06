package pl.me.sqlitetest3.weatherlibrary;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.me.sqlitetest3.additional.Data;
import pl.me.sqlitetest3.additional.Point;
import pl.me.sqlitetest3.additional.Weather;

/**
 * Created by Piotr1 on 2016-04-10.
 *
 */
public class PobieraczPogody implements PobieraczPogodyInt {
    private static List<Weather> stacje = new ArrayList<>();
    private static Document doc;

    @Override
    public Weather pobierzPogodeDlaPunktu(Point selectedPoint, Data date) {
        String url = "http://www.traxelektronik.pl/pogoda/zbiorcza.php?" +
                "RejID=14&czas=0&lang=0&over=0"+
                "&rok="+ date.getRok() +
                "&mies="+String.format("%02d", date.getMiesiac())+
                "&dzien="+String.format("%02d", date.getDzien())+
                "&godz="+String.format("%02d", date.getGodzina())+
                "&min="+String.format("%02d", date.getMinuta());

        new HTMLDownloader().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, url);
        System.out.println(url);
        while(stacje.size() < 27){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        double distance = -1;
        Weather najblizszaStacja = new Weather();
        for(int i =0 ; i<27; i++){
            Weather stacja = stacje.get(i);
            if(selectedPoint.countDistance(this.zwrocWspolrzedneStacji(stacja.getName())) < distance || distance == -1){
                if(!stacja.getAirTemperature().equals("-") && !(stacja.getWindSpeed().equals("-")) && !(stacja.getRain().equals("-")) && !(stacja.getSurface().equals("-"))){
                    distance = selectedPoint.countDistance(this.zwrocWspolrzedneStacji(stacja.getName()));
                    najblizszaStacja = stacja;
                }
            }
        }

        return najblizszaStacja;
    }

    public class HTMLDownloader extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params){
            try {
                if (doc == null)
                    doc = Jsoup.connect(params[0]).get();
                Elements tableElements = doc.select("table#male");

                Elements tableRowElements = tableElements.select(":not(thead) tr");
                for (int i = 6; i < tableRowElements.size()-1; i++) {
                    Element row = tableRowElements.get(i);

                    Elements rowItems = row.select("td");

                    Weather stacja = new Weather();

                    stacja.setName(rowItems.get(0).text().substring(1));
                    stacja.setAirTemperature(rowItems.get(1).text().replaceAll("[↓↑-]", ""));
                    if (stacja.getAirTemperature().equals(""))
                        stacja.setAirTemperature("-");
                    stacja.setRain(rowItems.get(11).text());
                    stacja.setSurface(rowItems.get(14).text());
                    stacja.setWindSpeed(rowItems.get(19).text().replaceAll("[↓↑-]", ""));
                    if (stacja.getWindSpeed().equals("")) stacja.setWindSpeed("-");

                    stacje.add(stacja);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return "-----------";
        }
    }

    private Point zwrocWspolrzedneStacji(String nazwa){
        switch(nazwa){
            case "29 Listopada / Kuźnicy": return new Point(19.962127, 50.097978);
            case "29 Listopada / Stwosza": return new Point(19.945981, 50.074132);
            case "Botewa / Półłanki": return new Point(20.042677, 50.038715);
            case "Jana Pawła II": return new Point(20.016945, 50.073224);
            case "Jasnogórska": return new Point(19.889032, 50.100380);
            case "Konopnickiej": return new Point(19.934699, 50.044789);
            case "Księcia Józefa": return new Point(19.903005, 50.049011);
            case "Lipska / Mierzeja Wiślana": return new Point(20.004304, 50.039443);
            case "Mickiewicza / Czarnowiejska": return new Point(19.924232, 50.065976);
            case "Mikołajczyka": return new Point(20.016164, 50.092270);
            case "Most Kotlarski": return new Point(19.960766, 50.052990);
            case "Na Zjeździe": return new Point(19.954872, 50.046794);
            case "Opolska / Wyki": return new Point(19.922824, 50.091112);
            case "Pasternik": return new Point(19.872194, 50.096184);
            case "Powstańców Śląskich": return new Point(19.952181, 50.038623);
            case "Powstańców / Strzelców": return new Point(19.972997, 50.097989);
            case "Przegorzały - obwodnica": return new Point(19.868425, 50.045799);
            case "Rondo Barei": return new Point(19.974094, 50.088832);
            case "Rondo Kocmyrzowskie": return new Point(20.026936, 50.079920);
            case "Rondo Polsadu": return new Point(19.971156, 50.085216);
            case "Sawickiego / Medweckiego": return new Point(20.002446, 50.076027);
            case "Skotnicka / Babińskiego": return new Point(19.873626, 50.006633);
            case "Wielicka / Nowosądecka": return new Point(19.979497, 50.024100);
            case "Wielicka / Rydygiera": return new Point(20.011096, 50.008913);
            case "Wybickiego / Łokietka": return new Point(19.924097, 50.083828);
            case "Zakopiańska / Taklińskiego": return new Point(19.914691, 49.987256);
            case "Zielna / Monte Cassino": return new Point(19.916012, 50.044321);
            default: return new Point(0,0);
        }
    }
}
