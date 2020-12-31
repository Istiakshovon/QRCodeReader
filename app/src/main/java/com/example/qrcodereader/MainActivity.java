package com.example.qrcodereader;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnBarCodeReader;
    TextView txtBarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBarCodeReader = findViewById(R.id.btn_bar_code_reader);
        txtBarCode = findViewById(R.id.txt_barcode);

        btnBarCodeReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=23){
                    requestCameraPermission();
                }else{
                    openBarCode();
                }
            }
        });
    }

    public void openBarCode(){
        Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                String contents = data.getStringExtra("SCAN_RESULT");

                Log.d("BARCODE_TEXT",contents);
                if(contents.contains("http")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(contents));
                    startActivity(intent);
                }
                else if(contents.contains("tel:")){
                    StringTokenizer tokens = new StringTokenizer(contents,":");
                    tokens.nextToken();
                    String contact = tokens.nextToken();
                    Log.d("CONTACT_NUMBER",contact);
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+contact));
                    startActivity(intent);
                }else if(contents.contains("SMSTO")){
                    StringTokenizer tokens = new StringTokenizer(contents,":,:");
                    tokens.nextToken();
                    String contact = tokens.nextToken();
                    String message = tokens.nextToken();

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address",contact);
                    sendIntent.putExtra("sms_body", message);
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);
//                    txtBarCode.setText(contact+"\n"+message);
                }
                else if(contents.contains("mailto")){
                    StringTokenizer tokens = new StringTokenizer(contents, ":,?,=,&,=");
                    tokens.nextToken();
                    String email = tokens.nextToken();
                    tokens.nextToken();
                    String subject = tokens.nextToken();
                    tokens.nextToken();
                    String message = tokens.nextToken();

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ email});
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);

                    //need this to prompts email client only
                    intent.setType("message/rfc822");

                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }
                else if(contents.contains("WIFI")){
                    StringTokenizer tokens = new StringTokenizer(contents, ":,;");
                    tokens.nextToken();
                    String idLabel = tokens.nextToken();
                    String idLabelValue = tokens.nextToken();
                    String ssidLabel = tokens.nextToken();
                    String ssid = tokens.nextToken();
                    String passwordLabel = tokens.nextToken();
                    String password= tokens.nextToken();


//                    createWifiCfg(idLabelValue,password,ssid);

                    txtBarCode.setText(idLabel+": "+idLabelValue+"\n"+ssidLabel+" : "+ssid+"\n"+passwordLabel+" : "+password);
//                    txtBarCode.setText(contents);
                }else if(contents.contains("MECARD")){
//                    : : , ; : ; : ; : ; : ; : ; : ; : ; : ,, , , , ,
                    StringTokenizer tokens = new StringTokenizer(contents,":,:,,,;,:,;,:,;,:,;,:,;,:,;,:,;,:,;,:,,,,,,,,,,,");
                    String mecardLabel = tokens.nextToken();
                    String mecard = tokens.nextToken();
                    String lastName = tokens.nextToken();
                    String firstName = tokens.nextToken();
                    String nicknameLabel = tokens.nextToken();
                    String nickname = tokens.nextToken();
                    String contact1Label = tokens.nextToken();
                    String contact1 = tokens.nextToken();
                    String contact2Label = tokens.nextToken();
                    String contact2 = tokens.nextToken();
                    String contact3Label = tokens.nextToken();
                    String contact3 = tokens.nextToken();
                    String emailLabel = tokens.nextToken();
                    String email = tokens.nextToken();
                    String bdayLabel = tokens.nextToken();
                    String bday = tokens.nextToken();
                    String noteLabel = tokens.nextToken();
                    String note = tokens.nextToken();
                    String addressLabel = tokens.nextToken();
                    String street = tokens.nextToken();
                    String city = tokens.nextToken();
                    String state = tokens.nextToken();
                    String zipCode = tokens.nextToken();
                    String country = tokens.nextToken();


                    Log.d("MECARD",mecardLabel+" : "+mecard);
                    Log.d("lastName",lastName);
                    Log.d("firstName",firstName);
                    Log.d("nickname",nicknameLabel+" : "+nickname);
                    Log.d("contact1",contact1Label+" : "+contact1);
                    Log.d("contact2",contact2Label+" : "+contact2);
                    Log.d("contact3",contact3Label+" : "+contact3);
                    Log.d("email",emailLabel+" : "+email);
                    Log.d("bday",bdayLabel+" : "+bday);
                    Log.d("note",noteLabel+" : "+note);
                    Log.d("address",addressLabel+" : "+street);
                    Log.d("city",city);
                    Log.d("state",state);
                    Log.d("zipCode",zipCode);
                    Log.d("country",country);

                    txtBarCode.setText("Name : "+firstName+" "+lastName+
                                        "\n"+"Nickname : "+nickname+
                                        "\n"+"Phone 1 : "+contact1+
                                        "\n"+"Phone 2 : "+contact2+
                                        "\n"+"Phone 3 : "+contact3+
                                        "\n"+"Email : "+email+
                                        "\n"+"Birthday : "+bday+
                                        "\n"+"Note : "+note+
                                        "\n"+"Address : "+street+","+city+","+country+","+zipCode
                    );
                }
                else{
                    txtBarCode.setText(contents);
                }


                Log.d("TAG","contents : "+contents);
            }else if(resultCode == RESULT_CANCELED){
                Log.d("TAG","RESULT_CANCELED");
            }
        }
    }

    private void requestCameraPermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openBarCode();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if(response.isPermanentlyDenied()){
                            Toast.makeText(MainActivity.this,"Camera Permission Required",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    public static WifiConfiguration createWifiCfg(String ssid, String password, String type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        config.SSID = "\"" + ssid + "\"";

        if(type.equals("nopass")){
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


            Log.d("CONNECTION_STATUS","connected");

        }
        else if(type.equals("WEP")){
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

            Log.d("CONNECTION_STATUS","connected");
        }else if(type.equals("WPA")){
            config.preSharedKey = "\""+password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;

            Log.d("CONNECTION_STATUS","connected");
        }

        return config;
    }
}