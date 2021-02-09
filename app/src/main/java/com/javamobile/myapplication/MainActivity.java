package com.javamobile.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickButton(View v) {
        //Endereço do webservice
        String url = "http://data.fixer.io/api/latest?access_key=fd6b686b208c4f659614b85bae807a0a&symbols=USD,AUD,CAD,PLN,MXN,BRL";

        // criar uma tarefa assíncrona que realiza a requisição do webservice
        // e retorna o valor do dolar em um toast
        System.out.println(new DownloadTask().execute(url));

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadContent(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve data. URL may be invalid.";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            // Ao terminar de carregar, irá ler o conteúdo e disparar o Toast com o valo do dolar
            try {
                JSONObject parent_obj = new JSONObject(result).getJSONObject("rates");

                Toast.makeText(MainActivity.this, "Euro está "+parent_obj.getString("BRL"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private String downloadContent(String myurl) throws IOException {
        InputStream is = null;
        int length = 500;

        // Realiza o request e retorna o resultado como String
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds*/);
            conn.setConnectTimeout(15000 /* milliseconds*/);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Converte o InputStream para uma String
            String contentAsString = convertInputStreamToString((InputStream) is, length);
            return contentAsString;
        } finally {
            if(is != null){
                is.close();
            }
        }
    }

    private String convertInputStreamToString(InputStream stream, int length) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[length];
        reader.read(buffer);
        return new String(buffer);
    }
}