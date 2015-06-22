package com.example.esquivel.anpr_imagin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;

/**
 * Created by Rafael on 11/06/2015.
 */
public class UploaderFoto extends AsyncTask<String, Integer, Void>  {

    ProgressDialog pDialog;
    String miFoto = "";
    private Context cnt = null;
    public void SetContext(Context data) {
        cnt = data;
    }
    @Override
    protected Void doInBackground(String... params) {
        miFoto = params[0];
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httppost = new HttpPost("http://190.109.185.138/ANPR/API/Subir");
            File file = new File(miFoto);
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody foto = new FileBody(file, "image/jpeg");
            mpEntity.addPart("fotoUp", foto);
            httppost.setEntity(mpEntity);
            httpclient.execute(httppost);
            httpclient.getConnectionManager().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(cnt);
        pDialog.setMessage("Subiendo la imagen, espere." );
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.show();
    }
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        pDialog.dismiss();
    }
}
