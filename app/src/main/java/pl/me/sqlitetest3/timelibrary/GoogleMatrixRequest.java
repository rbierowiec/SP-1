package pl.me.sqlitetest3.timelibrary;

import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class GoogleMatrixRequest extends AsyncTask<String, Void, String> {
    String odp;

    OkHttpClient client = new OkHttpClient();

    public String doInBackground(String... urls) {
        String url = urls[0];
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            //System.out.println(response.body().string());
            odp = response.body().string();
            return odp;
        } catch(IOException ex){
            ex.printStackTrace();
            return "blad";
        }
    }

//    protected void onPostExecute(Void result) {
//        //do stuff
//        getOdp();
//    }
//
//    public String getOdp(){
//        return odp;
//    }

//    public static void main(String[] args) throws IOException {
//        GoogleMatrixRequest request = new GoogleMatrixRequest();
//        String url_request = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC%7CSeattle&destinations=San+Francisco%7CVictoria+BC&mode=bicycling&language=fr-FR&key=" + API_KEY;
//
//        String response = request.doInBackground(url_request);
//        System.out.println(response);
//    }
}
