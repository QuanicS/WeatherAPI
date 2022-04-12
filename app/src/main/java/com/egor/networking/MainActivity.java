package com.egor.networking;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText edt;
    TextView name_city;
    JSONObject jsonObject;
    TextView temp;
    TextView feel_temp;
    TextView descr;
    TextView humi;
    TextView wind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt = findViewById(R.id.edittext);
        name_city = findViewById(R.id.name_city);
        temp = findViewById(R.id.temp);
        feel_temp = findViewById(R.id.feel_temp);
        descr = findViewById(R.id.descr);
        humi = findViewById(R.id.humi);
        wind = findViewById(R.id.wind);
    }

    public void btnWeather(View view) {
        hideKeyboard(this);
        String city = edt.getText().toString();
        if(city.equals("")){
            Toast.makeText(this, "введите текст", Toast.LENGTH_SHORT).show();
        }
        else{
            String key = "aa8cbcd4c013bc0de455edf300e58c4a";
            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
            new getUrlData().execute(url);

        }
        edt.getText().clear();
        edt.clearFocus();
    }

    private class getUrlData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            name_city.setText("Ожидайте...");

        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null){
                    connection.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                if(result == null){
                    name_city.setText("Ошибка");
                    Toast.makeText(getApplicationContext(), "Такого города не существует", Toast.LENGTH_SHORT).show();
                    temp.setText("");
                    feel_temp.setText("");
                    descr.setText("");
                    humi.setText("");
                    wind.setText("");
                }
                else {
                    JSONObject jsonObject = new JSONObject(result);
                    name_city.setText(jsonObject.getString("name"));
                    temp.setText("Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C");
                    feel_temp.setText("Ощущается как: " + jsonObject.getJSONObject("main").getDouble("feels_like") + "°C");
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject object =  jsonArray.getJSONObject(i);
                        descr.setText("Описание: " + object.getString("description"));
                    }
                    humi.setText("Влажность: " + jsonObject.getJSONObject("main").getDouble("humidity") + "%");
                    wind.setText("Скорость ветра: " + jsonObject.getJSONObject("wind").getDouble("speed") + "м/с");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}