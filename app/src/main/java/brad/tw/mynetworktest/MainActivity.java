package brad.tw.mynetworktest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    private EditText input;
    private UIHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new UIHandler();
        input = (EditText)findViewById(R.id.inputData);
    }

    public void udpSend(View v){
        new Thread(){
            @Override
            public void run() {
                sendUDP();
            }
        }.start();
    }

    private void sendUDP(){
        byte[] buf = input.getText().toString().getBytes();
        try {
            DatagramSocket socket =
                    new DatagramSocket();
            DatagramPacket packet =
                    new DatagramPacket(
                            buf,buf.length,
                            InetAddress.getByName(
                                    "10.0.3.2"),
                            8888);
            socket.send(packet);
            socket.close();

            Log.v("brad", "Send UDP OK");
        }catch(Exception ee){
            Log.v("brad", ee.toString());
        }
        handler.sendEmptyMessage(0);
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            input.setText("");
        }
    }

}
