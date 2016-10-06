package brad.tw.mynetworktest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText input;
    private UIHandler handler;
    private ImageView img;
    private Bitmap bmpImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView)findViewById(R.id.img);

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

    public void tcpSend(View v){
        new Thread(){
            @Override
            public void run() {
                byte[] buf = input.getText().toString().getBytes();
                try {
                    Socket socket = new Socket(
                            InetAddress.getByName("10.0.3.2"), 9999);
                    OutputStream out = socket.getOutputStream();
                    out.write(buf);
                    out.flush();
                    out.close();

                    socket.close();
                }catch (Exception ee){
                    Log.v("brad", ee.toString());
                }
            }
        }.start();
    }

    public void http1(View v){
        new Thread(){
            @Override
            public void run() {
                doHttp1();
            }
        }.start();
    }

    private void doHttp1(){
        try {
            URL url = new URL("http://www.brad.tw/");
            HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
            conn.connect();
            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
            String line;
            while ( (line = reader.readLine()) != null){
                Log.v("brad", line);
            }
            reader.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void http2(View v){
        new Thread(){
            @Override
            public void run() {
                doHttp2();
            }
        }.start();
    }

    private void doHttp2(){

        try {
            URL url =
                    new URL("http://www.technobuffalo.com/wp-content/uploads/2016/10/Google-Pixel-and-Pixel-XL-1-1280x720.jpg");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            bmpImage = BitmapFactory.decodeStream(conn.getInputStream());
            handler.sendEmptyMessage(1);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    input.setText("");
                    break;
                case 1:
                    img.setImageBitmap(bmpImage);
                    break;
            }
        }
    }

}
