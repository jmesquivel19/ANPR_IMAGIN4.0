package com.example.esquivel.anpr_imagin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import static android.provider.MediaStore.EXTRA_OUTPUT;

public class EntradaActivity extends Activity {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    private ImageButton btnBuscar;
    private EditText Txtvoz;
    private ImageButton bt_Camara;
    private ImageButton bt_start;
    private ImageView guardarfoto;
    private Vector<String> nombres;
    private Vector<String> telefonos;
    private Validaciones validacion;
    private RadioButton RdAuto;
    private Bitmap bmp;
    private RadioButton RdMoto;
    private RadioGroup RdVehiculo;
    private EditText txtPLaca1;
    private String placaentrada="";
    private String fechaentrada="";
    private double aleatorio;
    private String foto;
    private static final int CAMERA_PIC_REQUEST = 1337;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_entrada);
        btnBuscar = (ImageButton)findViewById(R.id.btnBuscar);
        bt_Camara  = (ImageButton) findViewById(R.id.ibtnCamara);
        bt_start = (ImageButton)findViewById(R.id.ibtnVoz);
        final EditText TxtPlaca = (EditText)findViewById(R.id.txtPlaca);
        final RegistroDTO Reg = new RegistroDTO();
        final Gson gsson;
        final Picasso pi;
        gsson = new Gson();


        bt_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if(placaentrada!="" && fechaentrada!="") {

                        foto = Environment.getExternalStorageDirectory() + "/"+ placaentrada.toString() + "_" + fechaentrada.toString()+".jpg";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        Uri output = Uri.fromFile(new File(foto));
                        intent.putExtra(EXTRA_OUTPUT, output);
                        startActivityForResult(intent, CAMERA_PIC_REQUEST);
                        placaentrada = "";
                        fechaentrada = "";
                    }else{
                        Toast.makeText(getApplicationContext(), "Antes debes registrar una entrada",
                                Toast.LENGTH_SHORT).show();
                    }


            }
        });
        bt_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Date fecha=new Date();
                String Res = new String();
                String respuesta;
                String placa;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                String currentDateandTime = sdf.format(fecha);
                SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentDateandTimenew = sdf.format(fecha);
                validacion=new Validaciones();
                respuesta= validacion.Validacion(TxtPlaca.getText().toString(), radioboton());
                if(respuesta!= "1" && respuesta!= "0" ) {
                    Reg.setPlaca(TxtPlaca.getText().toString());
                    Reg.setUsuarioCedula("1065582510");
                    Reg.setTipoRegistro("E");
                    Reg.setIdRegistro("0");
                    Reg.setFechaRegistro(currentDateandTime);
                    Res=gsson.toJson(Reg);
                    PostAsyncrona Reg = new PostAsyncrona(Res);
                    try {
                        boolean band = Reg.execute("http://190.109.185.138/ANPR/api/Registro").get();
                        if (band == true)
                        {
                            placaentrada=TxtPlaca.getText().toString();
                            fechaentrada=currentDateandTimenew;
                            Toast.makeText(getApplicationContext(), respuesta  +"     Entrada " + TxtPlaca.getText().toString() + " registrada",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else
                        {Toast.makeText(getApplicationContext(), "Error al guardar",
                                Toast.LENGTH_SHORT).show();

                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }else if(respuesta=="1")
                {Toast.makeText(getApplicationContext(), "Placa invalida",
                        Toast.LENGTH_SHORT).show();
                }
                else
                {Toast.makeText(getApplicationContext(), "Digite placa",
                        Toast.LENGTH_SHORT).show();
                 }
                TxtPlaca.getText().clear();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entrada, menu);
        return true;
    }

    private String radioboton(){
        RdAuto= (RadioButton) findViewById(R.id.RdAuto);
        RdMoto= (RadioButton) findViewById(R.id.RdMoto);
        if(RdAuto.isChecked()==true){
            return "0";
        }else {
            return "1";
        }
    }

    private void startVoiceRecognitionActivity() {
        // Definición del intent para realizar en análisis del mensaje
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla...");
        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    //Recogemos los resultados del reconocimiento de voz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] pla;
        String placa="";
        super.onActivityResult(requestCode, resultCode, data);
        Txtvoz = (EditText) findViewById(R.id.txtPlaca);
        //Si el reconocimiento a sido bueno
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
            //El intent nos envia un ArrayList aunque en este caso solo
            //utilizaremos la pos.0
            ArrayList<String> matches = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            //Separo el texto en palabras.

            pla = matches.get(0).toString().split(" ");

            for (int i = 0; i < pla.length; i++) {
                bucle2:
                for(int j=0; j<pla[i].length();j++){
                    if(Character.isDigit(pla[i].toString().charAt(j))){
                        placa += pla[i].toString();
                        break bucle2;
                    }
                }

                if(Character.isLetter(pla[i].toString().charAt(0))){
                    placa += pla[i].toString().charAt(0);
                }

            }
            Txtvoz.setText(placa.toUpperCase());

        }
      /*  if (requestCode==CAMERA_PIC_REQUEST && resultCode == RESULT_OK){
                File file = new File(foto);
                if (file.exists()) {
                    UploaderFoto nuevaTarea = new UploaderFoto();
                    nuevaTarea.SetContext(EntradaActivity.this);
                    nuevaTarea.execute(foto);
                    Toast.makeText(getApplicationContext(), "Guardo", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "No se ha realizado la foto", Toast.LENGTH_SHORT).show();

            }*/



    }
    private String getCode(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String photoCode = "pic_" + date;
        return photoCode;
    }

//Le indicamos al spinner el adaptador a usar

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

}
