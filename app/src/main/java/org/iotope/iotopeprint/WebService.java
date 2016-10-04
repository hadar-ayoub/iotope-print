package org.iotope.iotopeprint;

/**
 * Created by hadarayoub on 28/09/2016.
 */

import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class WebService {

    private String url;
    HashMap<String,HashMap> inscrits;

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return this.url;
    }

    public HashMap<String,HashMap> getServerData() {
        InputStream is = null;
        String result = "";
        String apiKey = "fs5560ea2a1775da7dc242e0678cb9e9d0cb86b0";


        // Envoie de la commande http
        is = SendHttpRequest(apiKey);

        // Convertion de la requête en string
        result = ConvertResponse(is);

        // Parse les données JSON
        inscrits = ParseData(result);

        return inscrits;

    }

    public HashMap ParseData(String result){
        try{
            JSONArray jArray = new JSONArray(result);
            inscrits = new HashMap();
            for (int j=0; j<jArray.length(); j++){
                HashMap<String, String> guest = new HashMap();
                JSONObject jsonData = jArray.getJSONObject(j);
                guest.put("full_name",jsonData.getString("full_name"));
                guest.put("pass",jsonData.getString("pass"));
                guest.put("company",jsonData.getString("company"));
                guest.put("email",jsonData.getString("email"));
                guest.put("conf_day",jsonData.getString("conf_day"));
                inscrits.put(jsonData.getString("registration_code"),guest);
            }

        }catch(JSONException e){
            Log.e("log_tag", "Erreur dans le parsing des data : " + e.toString());
        }
        return  inscrits;
    }

    public String ConvertResponse(InputStream is){
        String result = "";
        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result=sb.toString();
        }catch(Exception e){
            Log.e("log_tag", "Erreur dans la conversion du résultat : " + e.toString());
        }
        return result;
    }

    public InputStream SendHttpRequest(String apiKey){
        InputStream is = null;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("tag","P"));
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setHeader("X-API-KEY",apiKey);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        }catch(Exception e){
            Log.e("log_tag", "Erreur dans la connexion HTTP : " + e.toString());
        }
        return is;
    }
}
