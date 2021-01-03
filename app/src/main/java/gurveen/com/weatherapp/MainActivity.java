package gurveen.com.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText cityEditText;
    TextView weatherTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityEditText = findViewById(R.id.cityEditText);
        weatherTextView = findViewById(R.id.weatherTextView);


    }

    public void getWeather(View view){

        String encodedCityName = null;
        try {
            encodedCityName = URLEncoder.encode(cityEditText.getText().toString(),"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
        }

        DownloadTask task = new DownloadTask();
        task.execute("https://openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);
    }

    public class DownloadTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1){
                    char c = (char) data;
                    result += c;
                    data = reader.read();
                }

                return result;
            }catch (Exception e){
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                String weatherInfo = jsonObject.getString("weather");

                JSONArray jsonArray = new JSONArray(weatherInfo);

                String message = "";

                for (int i = 0;i<jsonArray.length();i++){
                    JSONObject jsonPart = jsonArray.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (!main.equals("") && !description.equals("")){
                         message = main + ":"+description;
                    }

                }

                if (!message.equals("")){
                    weatherTextView.setText(message);
                }else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
