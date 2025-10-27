package com.netgovpn.freedom;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DonateActivity extends AppCompatActivity {

    private ImageView qrcode, copyimg;
    private TextView address_txt;
    private Spinner spinnerCrypto, spinnerNetwork;

    private JSONObject wallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        copyimg = findViewById(R.id.copyimg);
        address_txt = findViewById(R.id.address_txt);
        qrcode = findViewById(R.id.qrcode);
        spinnerCrypto = findViewById(R.id.spinner_crypto);
        spinnerNetwork = findViewById(R.id.spinner_network);

        try {
            String jsonString = "{\n" +
                    "    \"BTC\": {\n" +
                    "    \"Bitcoin (bech32)\": {\n" +
                    "      \"label\": \"Bitcoin (bech32)\",\n" +
                    "      \"address\": \"bc1qexampleaddressxxxxxxxxxxxxxxxxxxxxxxxxx\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"TON\": {\n" +
                    "    \"Toncoin\": {\n" +
                    "      \"label\": \"Toncoin\",\n" +
                    "      \"address\": \"UQAYiAdlmIbn2ypieLPICiRpnusEWMBLfS-JSV3_3T0PlY6l\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"USDT\": {\n" +
                    "      \"USDT (ERC20 - Ethereum)\": {\n" +
                    "        \"label\": \"USDT (ERC20 - Ethereum)\",\n" +
                    "        \"address\": \"0xYourERC20AddressHere0123456789abcdef\"\n" +
                    "      },\n" +
                    "      \"USDT (BEP20 - BSC)\": {\n" +
                    "        \"label\": \"USDT (BEP20 - BSC)\",\n" +
                    "        \"address\": \"0xYourBEP20AddressHere0123456789abcdef\"\n" +
                    "      },\n" +
                    "      \"USDT (TRC20 - Tron)\": {\n" +
                    "        \"label\": \"USDT (TRC20 - Tron)\",\n" +
                    "        \"address\": \"TYourTronAddressHere1234567890\"\n" +
                    "      }\n" +
                    "    },\n" +
                    "    \"BNB\": {\n" +
                    "      \"BEP2\": {\n" +
                    "        \"label\": \"BNB (BEP2 - Binance Chain)\",\n" +
                    "        \"address\": \"bnb1exampleaddressxxxxxxxxxxxx\"\n" +
                    "      },\n" +
                    "      \"BEP20\": {\n" +
                    "        \"label\": \"BNB (BEP20 - BSC as 0x address)\",\n" +
                    "        \"address\": \"0xYourBep20AddressForBNB0123456789abcdef\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "}\n";

            JSONObject walletsObject = new JSONObject(jsonString);

            String walletsJson = walletsObject != null ? walletsObject.toString() : "{}";


            SharedPreferences prefs = getDefaultSharedPreferences(DonateActivity.this);
            if ("".equals(prefs.getString("wallets_json", ""))) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("wallets_json", walletsJson);
                editor.apply();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String walletsJson = prefs.getString("wallets_json", "{}");

        try {
            wallets = new JSONObject(walletsJson);
        } catch (JSONException e) {
            e.printStackTrace();
            wallets = new JSONObject();
        }


        List<String> cryptoList = new ArrayList<>();
        Iterator<String> keys = wallets.keys();
        while (keys.hasNext()) {
            cryptoList.add(keys.next());
        }
        ArrayAdapter<String> cryptoAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, cryptoList);
        cryptoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCrypto.setAdapter(cryptoAdapter);


        spinnerCrypto.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedCrypto = cryptoList.get(position);
                populateNetworkSpinner(selectedCrypto);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });


        spinnerNetwork.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateAddressAndQr();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });


        View.OnClickListener copyListener = v -> copyToClipboard(DonateActivity.this, address_txt.getText().toString());
        address_txt.setOnClickListener(copyListener);
        qrcode.setOnClickListener(copyListener);
        copyimg.setOnClickListener(copyListener);
    }

    private void populateNetworkSpinner(String crypto) {
        List<String> networkList = new ArrayList<>();
        try {
            if (wallets.has(crypto)) {
                JSONObject cryptoObj = wallets.getJSONObject(crypto);
                Iterator<String> keys = cryptoObj.keys();
                while (keys.hasNext()) {
                    networkList.add(keys.next());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> networkAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, networkList);
        networkAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerNetwork.setAdapter(networkAdapter);


        updateAddressAndQr();
    }

    private void updateAddressAndQr() {
        String selectedCrypto = (String) spinnerCrypto.getSelectedItem();
        String selectedNetwork = (String) spinnerNetwork.getSelectedItem();
        if (selectedCrypto == null || selectedNetwork == null) return;

        try {
            JSONObject cryptoObj = wallets.getJSONObject(selectedCrypto);
            JSONObject networkObj = cryptoObj.getJSONObject(selectedNetwork);
            String address = networkObj.getString("address");

            address_txt.setText(address);
            generateRoundedQrCode(address, 300, 300, 20);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied_address", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, getText(R.string.copyed), Toast.LENGTH_SHORT).show();
    }

    private void generateRoundedQrCode(String url, int width, int height, int cornerRadius) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(url, BarcodeFormat.QR_CODE, width, height);

            RoundedBitmapDrawable roundedDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedDrawable.setCornerRadius(cornerRadius);
            roundedDrawable.setAntiAlias(true);

            qrcode.setImageDrawable(roundedDrawable);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
