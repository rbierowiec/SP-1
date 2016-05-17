package pl.me.sqlitetest3.additional;

/**
 * Created by Piotr1 on 2016-04-10.
 */
public class Weather {
    // Nazwa stacji, z której zostały pobrane opady
    private String name;
    // Temperatura powietrza
    private String airTemperature;
    // Opady
    private String rain;
    // Nawierzchnia
    private String surface;
    // Prędkość wiatru
    private String windSpeed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(String airTemperature) {
        this.airTemperature = airTemperature;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getSurface() {
        return surface;
    }

    public void setSurface(String surface) {
        this.surface = surface;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
