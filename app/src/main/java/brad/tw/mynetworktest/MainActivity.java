package brad.tw.mynetworktest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText input;
    private UIHandler handler;
    private ImageView img;
    private Bitmap bmpImage;
    private File sdroot;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
        }

        sdroot = Environment.getExternalStorageDirectory();

        img = (ImageView)findViewById(R.id.img);

        handler = new UIHandler();
        input = (EditText)findViewById(R.id.inputData);

        pDialog = new ProgressDialog(this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Downloading.....");



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
//            URL url = new URL("http://www.brad.tw/");
            URL url = new URL("http://10.0.3.2/");
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

    public void http3(View v){
        pDialog.show();
        new Thread(){
            @Override
            public void run() {
                doHttp3();
            }
        }.start();
    }

    private void doHttp3(){

        try {
            URL url =
                    new URL("http://pdfmyurl.com/?url=" +
                            "http://www.gamer.com.tw");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            InputStream in = conn.getInputStream();

            FileOutputStream fout =
                    new FileOutputStream(new File(sdroot,"brad.pdf"));
            byte[] buf = new byte[4096]; int len;
            while ( (len = in.read(buf, 0, buf.length)) != -1){
                fout.write(buf, 0, len);
            }
            fout.flush();
            fout.close();
            Log.v("brad", "Download OK");
        } catch (Exception e) {
            Log.v("brad", "Download Fail");
        } finally {
            handler.sendEmptyMessage(2);
        }
    }
    public void http4(View v){
        new Thread(){
            @Override
            public void run() {
                doHttp4();
            }
        }.start();
    }

    private void doHttp4(){

        try {
            URL url =
                    new URL("http://data.coa.gov.tw/Service/OpenData/EzgoTravelFoodStay.aspx");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    conn.getInputStream()));
            String strJSON = reader.readLine();
            Log.v("brad", "len:" + strJSON.length() + ":" + strJSON);
            parseJSON(strJSON);


        } catch (Exception e) {
        }
    }
    public void http5(View v){
        new Thread(){
            @Override
            public void run() {
                doHttp5();
            }
        }.start();
    }

    private void doHttp5(){
        try {
            MultipartUtility mu =
                    new MultipartUtility(
                            "http://10.0.3.2/check.php","UTF-8");
            mu.addFormField("account","brad");
            mu.addFormField("passwd","123456");
            List<String> ret = mu.finish();
            Log.v("brad", ret.get(0));


        } catch (Exception e) {
        }
    }

    private void parseJSON(String json){
        try {
            JSONArray root = new JSONArray(json);
            for (int i=0; i<root.length(); i++){
                JSONObject item = root.getJSONObject(i);
                String name = item.getString("Name");
                String addr = item.getString("Address");
                String tel = item.getString("Tel");
                Log.v("brad", name + ":" + addr + ":" + tel);
            }



        } catch (JSONException e) {
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
                case 2:
                    pDialog.dismiss();
                    break;
            }
        }
    }

}
