package com.example.dmitrii_bashev.lamp_remote_control;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.os.AsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //объявяем кнопки
    Button button_ON;
    Button button_OFF;
    Button button_plus_cyclic_increase;
    Button button_minus_cyclic_increase;
    Button button_plus;
    Button button_minus;
    Button line_button;

    BluetoothSocket clientSocket;

    MyTask mt;

    final String TAG = "status";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //соединяем кнопки
        button_ON = findViewById(R.id.button_ON);
        button_ON.setOnClickListener(this);
        button_OFF = findViewById(R.id.button_OFF);
        button_OFF.setOnClickListener(this);
        button_plus_cyclic_increase = findViewById(R.id.button_plus_cyclic_increase);
        button_plus_cyclic_increase.setOnClickListener(this);
        button_minus_cyclic_increase = findViewById(R.id.button_minus_cyclic_increase);
        button_minus_cyclic_increase.setOnClickListener(this);
        button_plus = findViewById(R.id.button_plus);
        button_plus.setOnClickListener(this);
        button_minus = findViewById(R.id.button_minus);
        button_minus.setOnClickListener(this);
        line_button = findViewById(R.id.line_button);
        line_button.setOnClickListener(this);

        //опрашиваем bluetooth
        //mt = new MyTask();
        //mt.execute();
    }

    protected void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (clientSocket != null) {
            if (clientSocket.isConnected()) {
                return;
            }
        }
        mt = new MyTask();
        mt.execute();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

            if (bluetooth.isEnabled()){

                String adapterMac = "30:35:AD:A6:D3:8F";

                try {
                    //BluetoothDevice device = bluetooth.getRemoteDevice("20:16:06:22:06:40");
                    BluetoothDevice device = bluetooth.getRemoteDevice(adapterMac);

                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});

                    clientSocket = (BluetoothSocket) m.invoke(device, 1);
                    clientSocket.connect();
                }
                catch (IOException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
                catch (SecurityException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
                catch (NoSuchMethodException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
                catch (IllegalArgumentException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
                catch (IllegalAccessException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }
                catch (InvocationTargetException e) {
                    Log.d("BLUETOOTH", e.getMessage());
                }

                //Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();
            }

            Intent intent = new Intent();
            intent.setAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, 1);

            return null;
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_ON:
                sendData((byte) 255);
                sendData((byte) 0);
                sendData((byte) 0);
                break;
            case R.id.button_OFF:
                sendData((byte) 0);
                sendData((byte) 0);
                sendData((byte) 0);
                break;
            case R.id.button_plus_cyclic_increase:
                sendData((byte) 255);
                sendData((byte) 145);
                sendData((byte) 0);
                break;
            case R.id.button_minus_cyclic_increase:
                sendData((byte) 255);
                sendData((byte) 45);
                sendData((byte) 0);
                break;
            case R.id.button_plus:
                sendData((byte) 255);
                sendData((byte) 100);
                sendData((byte) 0);
                break;
            case R.id.button_minus:
                sendData((byte) 255);
                sendData((byte) 90);
                sendData((byte) 0);
                break;
            case R.id.line_button:
                sendData((byte) 255);
                sendData((byte) 55);
                sendData((byte) 0);
                break;
            }
    }


    private void sendData(byte value) {
        try {
            OutputStream outStream = clientSocket.getOutputStream();
            outStream.write(value);
        } catch (IOException e) {
            //Если есть ошибки, выводим их в лог
            Log.d("BLUETOOTH", e.getMessage());
        }
    }
}