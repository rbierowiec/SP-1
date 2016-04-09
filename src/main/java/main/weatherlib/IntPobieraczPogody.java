package main.weatherlib;

import main.additional.Data;
import main.additional.Point;
import main.additional.Weather;

public interface IntPobieraczPogody {
    Weather pobierzPogodeDlaPunktu(Point selectedPoint, Data date);
}
