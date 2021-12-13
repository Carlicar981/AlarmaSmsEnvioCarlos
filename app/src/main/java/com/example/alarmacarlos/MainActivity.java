package com.example.alarmacarlos;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    Button seleccionarHora,guardar,eliminar,seleccionarFecha;
    TextView mostrarHora,mostrarFecha;
    //configurar para determinar la fecha actual del dispositivo
    Calendar actual = Calendar.getInstance();
    //
    Calendar calendar = Calendar.getInstance();

    private int minutos,hora,dia,mes,anio;

    // Nombre de la clase para el log
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_MESSAGE = "Primera_Activity";

    private EditText enviarDatos;

    private TextView mostrarRespuestaTxt;

    // NUEVA MANERA DE LANZAR UNA ACTIVIDAD DADO QUE startActivityForResult ESTÁ DEPRECATED
    // DEFINIMOS/CREAMOS UN LANZADOR DE ACTIVITY PARA OBTENER UN RESULTADO
    // Primer parámetro: contrato. Inicio la actividad para un resultado
    // Segundo parámetro: resultado a la llamada.
    ActivityResultLauncher<Intent> secondActivityResultLauncher = registerForActivityResult(

            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                //Se implementa el método onActivityResult
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Se comprueba que el código del resultado sea correcto
                    //Es decir, que reciba lo que debe recibir
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String reply = null;
                        //Si existen datos, se cargan en reply desde la second activity
                        if (data != null) {
                            reply = data.getStringExtra(SecondActivity.EXTRA_REPLY);
                        }

                        //Se fijan los datos de la respuesta
                        mostrarRespuestaTxt.setText(reply);
                        mostrarRespuestaTxt.setVisibility(View.VISIBLE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se inicializan las variables
        enviarDatos = findViewById(R.id.enviarDatos);
        mostrarRespuestaTxt= findViewById(R.id.mostrarRespuestaTxt);

        seleccionarHora= findViewById(R.id.selectHora);
        mostrarHora=findViewById(R.id.txt_hora);
        seleccionarFecha=findViewById(R.id.selecFecha);
        mostrarFecha=findViewById(R.id.txt_fecha);
        guardar=findViewById(R.id.guardar);
        eliminar=findViewById(R.id.eliminar);

        seleccionarHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            hora=actual.get(Calendar.HOUR_OF_DAY);
            minutos=actual.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int h, int m) {
                        calendar.set(Calendar.HOUR_OF_DAY,h);
                        calendar.set(Calendar.MINUTE,m);
                        System.out.println(calendar.toString());
                        mostrarHora.setText(String.format("%02d:%02d",h,m));
                        System.out.println("Selector_Hora "+calendar.getTimeInMillis());

                    }
                },hora,minutos,true);
                //para que muestre la hora
                timePickerDialog.show();
            }
        });

        seleccionarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                anio = actual.get(Calendar.YEAR);
                mes = actual.get(Calendar.MONTH);
                dia = actual.get(Calendar.DAY_OF_WEEK);

                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        calendar.set(Calendar.DAY_OF_WEEK,day);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.YEAR,year);
                        Toast.makeText(MainActivity.this, getString(R.string.app_name),
                                Toast.LENGTH_LONG).show();
                        mostrarFecha.setText(String.format("%02d/%02d/%02d",day,month,year));
                        System.out.println("Selector_Fecha "+calendar.getTime());

                    }
                },anio,mes,dia);
                datePickerDialog.show();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"ALARMA GUARDADA!",Toast.LENGTH_SHORT).show();
                Utils.setAlarm(1,calendar.getTimeInMillis(),MainActivity.this);
                System.out.println("Boton_Guardar "+calendar.getTime());
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"ALARMA ELIMINADA!",Toast.LENGTH_SHORT).show();
            }
        });
        // Comprobamos si la app tiene los permisos necesarios y si no los tiene los pedimos
        this.askForPermission(Manifest.permission.SEND_SMS);
        this.askForPermission(Manifest.permission.READ_SMS);
        this.askForPermission(Manifest.permission.RECEIVE_SMS);
        this.askForPermission(Manifest.permission.BROADCAST_SMS);

        // Iniciamos la clase que gestiona los SMS
        final SmsManager smsManager = SmsManager.getDefault();

        // Iniciamos la clase que tenemos para controlar los SMS recibidos
        new SMSReceiver();

        // Envio de SMS al hacer click en el botón
        findViewById(R.id.buttonSms).setOnClickListener((view) -> {
            final String phone = ((TextView) findViewById(R.id.telefono)).getText().toString();
            final String msg = ((TextView) findViewById(R.id.sms)).getText().toString();

            // Método para enviar el SMS. Es multipart por si el mensaje es mayor que 160 caracteres.
            // Usamos el divideMessage para que el propio gestor divida los mensajes correctamente.
            smsManager.sendMultipartTextMessage(phone, null, smsManager.divideMessage(msg), null, null);

            // Toast informativo
            Toast.makeText(this, "Mensaje enviado!", Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * Método para comprabar y pedir permisos
     *
     * @param permission El permiso que queremos comprobar y/o pedir
     * @see Manifest.permission
     */
    private void askForPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED)
            return;
        ActivityCompat.requestPermissions(this, new String[]{permission}, PackageManager.PERMISSION_GRANTED);
    }
    /**
     * Gestiona el evento onclick. Obtiene el valor del Edit text principal
     * creando un intent y lanzando la segunda actividad con un intent
     * El retorno del intent de la segunda actividad es onActivityResult().
     *
     * @param view The view (Button) that was clicked.
     */
    public void launchSecondActivity(View view) {
        TextView telefono = (TextView)findViewById(R.id.telefono);
        TextView mensaje = (TextView)findViewById(R.id.sms);
        Log.d(LOG_TAG, "Botón pulsado");
        Intent intent = new Intent(this, SecondActivity.class);
        String message = enviarDatos.getText().toString()+"\n La alarma suena a las: "
                +mostrarHora.getText().toString()+"\n El telefono del sms es: "
                +telefono.getText().toString()+"\n El mensaje es: "+mensaje.getText().toString();
        System.out.println(message);
        //Indico quién envía el mensaje y el mensaje que se envía
        intent.putExtra(EXTRA_MESSAGE, message);
        //lanza la segunda activity
        secondActivityResultLauncher.launch(intent);


    }
}