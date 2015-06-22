package com.example.esquivel.anpr_imagin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


public class SalidaActivity extends Activity {
    private EditText Txtvoz;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    private ImageButton btnBuscar;
    private ImageButton bt_Camara;
    private ImageButton bt_start;
    private ImageView img;
    private Vector<String> nombres;
    private Vector<String> telefonos;
    final Context context = this;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.acivity_salida);
        btnBuscar = (ImageButton)findViewById(R.id.ibtnBuscarS);
        final EditText text = (EditText) findViewById(R.id.txtPlaca2);
        bt_start = (ImageButton)findViewById(R.id.ibtnVozS);
        bt_Camara  = (ImageButton) findViewById(R.id.ibtnCamaraS);
        final Gson gsson;
        gsson = new Gson();
        bt_Camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,savedInstanceState);
                startActivityForResult(cameraIntent, 0);
            }
        });
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
        final EditText TxtPlaca = (EditText)findViewById(R.id.txtPlaca2);
        final RegistroDTO Reg = new RegistroDTO();
        final String placa = text.getText().toString();
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!verificaConexion(SalidaActivity.this)) {
                    Toast.makeText(getApplicationContext(),
                            "Comprueba tu conexión a Internet.", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (TxtPlaca.getText().length() < 5) {
                        Toast.makeText(getApplicationContext(),
                                "Digite una placa valida", Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        GetAsyncrona RegG = new GetAsyncrona();
                        try {
                            String ban = RegG.execute("http://190.109.185.138/ANPR/api/Registro/" + TxtPlaca.getText().toString()).get();
                            Log.e("Sadainer", ban.toString());
                            RegistroDTO RegS = new RegistroDTO();
                            RegS = gsson.fromJson(ban.toString(), RegistroDTO.class);
                            if (RegS.getIdRegistro().toString() != "0") {
                                String[] array = RegS.getFechaRegistro().toString().split("T");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setMessage("ID Usuario: " + RegS.getUsuarioCedula().toString() + "\n" +
                                        "Fecha Ingreso: " + array[0].toString() + "\n" +
                                        "Hora: " + array[1].toString() + "\n" +
                                        "Placa: " + RegS.getPlaca().toString() + "\n")
                                        .setTitle("¿Confirmar Salida?")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                String Res = new String();
                                                Reg.setPlaca(TxtPlaca.getText().toString());
                                                Reg.setUsuarioCedula("1065582510");
                                                Reg.setTipoRegistro("S");
                                                Reg.setIdRegistro("0");
                                                Reg.setFechaRegistro("2000-01-01 00:00:00");
                                                Res = gsson.toJson(Reg);
                                                TxtPlaca.setText("");
                                                PostAsyncrona Reg = new PostAsyncrona(Res);
                                           /* Reg.SetContext(MainActivity.this);*/
                                                try {
                                                    boolean ban = Reg.execute("http://190.109.185.138/ANPR/api/Registro").get();

                                                    Toast.makeText(getApplicationContext(), "Salida " + TxtPlaca.getText().toString() + " registrada",
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Error al registrar Salida",
                                                            Toast.LENGTH_SHORT).show();
                                                } catch (ExecutionException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Error al registrar Salida",
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {

                                                dialog.cancel();
                                            }
                                        });

                                builder.create();
                                builder.show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Entrada no registrada",
                                        Toast.LENGTH_SHORT).show();
                            }


                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        } catch (ExecutionException e) {
                            e.printStackTrace();

                        }

                    }
                }
            }
        });
    }


    private void startVoiceRecognitionActivity() {
        // Definición del intent para realizar en análisis del mensaje
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ...");

        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    //Recogemos los resultados del reconocimiento de voz
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String[] pla;
        String placa="";
        super.onActivityResult(requestCode, resultCode, data);
        Txtvoz = (EditText) findViewById(R.id.txtPlaca2);
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
    }

    private String getCode(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String photoCode = "pic_" + date;
        return photoCode;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acivity_salida, menu);
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

    public static boolean verificaConexion(Context ctx) {
        boolean bConectado;
         bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // No sólo wifi, también GPRS
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        // este bucle debería no ser tan ñapa
        for (int i = 0; i < 2; i++) {
            // ¿Tenemos conexión? ponemos a true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }
}
