package pl.me.sqlitetest3.weatherlibrary;

import pl.me.sqlitetest3.additional.Data;
import pl.me.sqlitetest3.additional.Point;
import pl.me.sqlitetest3.additional.Weather;

/**
 * Created by Piotr1 on 2016-04-10.
 */
public interface PobieraczPogodyInt {
    Weather pobierzPogodeDlaPunktu(Point selectedPoint, Data date);
}
