package main.run;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import main.weatherlib.PobieraczPogody;
import com.softeq.android.prepopdb.R;

import main.additional.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dzialaj(View view){
        PobieraczPogody stacja = new PobieraczPogody();
        Weather pogoda = stacja.pobierzPogodeDlaPunktu(new Point(19.815901, 50.039030), new Data(2016,4,9,21,0,0));
        System.out.println("Najbliższa stacja: "+pogoda.name+", temperatura: "+pogoda.airTemperature+", opady: "+pogoda.rain+", nawierzchnia: "+pogoda.surface+", prędkość wiatru: "+pogoda.windSpeed);
    }
}
