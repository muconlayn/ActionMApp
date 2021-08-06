package com.action.uygulamasi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfilActivity extends AppCompatActivity {

    private String id;
    private Bitmap bitmap;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String url_profil_guncelle = "http://10.0.2.2/Action/profilFotoYukle.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        id = preferences.getString("id", "-1");


        findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfilActivity.this, findViewById(R.id.imageView));
                popupMenu.getMenu().add("Kamera");
                popupMenu.getMenu().add("Galeri");
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle() == "Galeri") {
                            resimSecmeIstegi();
                        }

                        if (item.getTitle() == "Kamera") {

                        }
                        return true;
                    }
                });
            }
        });
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void resimSecmeIstegi() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Resim seçiniz"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imageView = (ImageView) findViewById(R.id.profile_image2);
            imageView.setImageBitmap(bitmap);
        }
    }


    private void profiliGuncellemeIstegiGonder() {
        final ProgressDialog loading = ProgressDialog.show(ProfilActivity.this, "Profil güncelleniyor...", "Lütfen bekleyiniz...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil_guncelle, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.d("Json verisi: ", response);

                String durum = null;
                String mesaj = null;
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (durum.equals("200")) {
                    Snackbar.make(findViewById(R.id.containerProfil), mesaj, Snackbar.LENGTH_LONG).show();
                    SharedPreferences p= PreferenceManager.getDefaultSharedPreferences(ProfilActivity.this);
                    SharedPreferences.Editor editor=p.edit();
                    editor.putBoolean("ProfilChanged",true);
                    editor.commit();
                } else {
                    Snackbar.make(findViewById(R.id.containerProfil), mesaj, Snackbar.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Bitmap dan String e dönüştürülüyor
                String image = getStringImage(bitmap);

                Map<String, String> params = new HashMap<>();

                //parametreler ekleniyor
                params.put("id", id);
                params.put("profil", image);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(ProfilActivity.this);
        requestQueue.add(stringRequest);
    }

    public void click(View view) {
        if (bitmap != null) {
            profiliGuncellemeIstegiGonder();
        } else {
            Snackbar.make(findViewById(R.id.containerProfil), "Lütfen bir resim seçiniz...", Snackbar.LENGTH_LONG).show();
        }
    }


}
