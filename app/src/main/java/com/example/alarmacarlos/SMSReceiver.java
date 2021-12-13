package com.example.alarmacarlos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtenemos el SMS del BroadcastReceiver
        final Bundle bundle = intent.getExtras();
        final StringBuilder sb = new StringBuilder();
        SmsMessage[] smss;


            // Obtenemos el SMS recibido
            final Object[] pdus = (Object[]) bundle.get("pdus");

            // Iniciamos el Array de SMS a la cantidad de SMS recibidos
            smss = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                // Le asignamos un valor al SMS gracias a la PDU recibida
                smss[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Guardamos la información del SMS y la mostramos
                sb.append("SMS de ").append(smss[i].getOriginatingAddress()).append(":\n");
                sb.append(smss[i].getMessageBody()).append("\n");
            }
            Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
        }
    }

