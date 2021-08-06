package com.action.uygulamasi;


import android.animation.ObjectAnimator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;

import android.net.ConnectivityManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class KayitEkrani extends AppCompatActivity {

    private TextInputLayout tiladsoyad, tilmail, tilsifre, tilkullaniciadi;
    private TextInputEditText adsoyad, mail, sifre, kullaniciadi;
    private RequestQueue requestQueue;
    private static final String url_kayit = "http://10.0.2.2/Action/register.php";
    private boolean istekGonderildi = false;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tam ekran için
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kayit_ekrani);

        ImageView image = (ImageView) findViewById(R.id.imagekayit);
        tiladsoyad = (TextInputLayout) findViewById(R.id.tiladsoyadkayit);
        tilmail = (TextInputLayout) findViewById(R.id.tilmailkayit);
        tilsifre = (TextInputLayout) findViewById(R.id.tilsifrekayit);
        tilkullaniciadi = (TextInputLayout) findViewById(R.id.tilkullaniciadikayit);
        adsoyad = (TextInputEditText) findViewById(R.id.adsoyadkayit);
        mail = (TextInputEditText) findViewById(R.id.mailkayit);
        sifre = (TextInputEditText) findViewById(R.id.sifrekayit);
        kullaniciadi = (TextInputEditText) findViewById(R.id.kullaniciadikayit);
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        if (!internetBaglantiKontrol()) {
            Snackbar.make(findViewById(R.id.rootKayitEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
        }


        WindowManager wm = window.getWindowManager();
        Display ekran = wm.getDefaultDisplay();

        Point point = new Point();
        ekran.getSize(point);

        int genislik = point.x;
        int yukseklik = point.y;

        //1.50 en boy oranı
        image.getLayoutParams().width = (int) (yukseklik * 1.50);
        image.getLayoutParams().height = yukseklik;

        animator = ObjectAnimator.ofFloat(image, "x", 0, -(yukseklik * 1.50f - genislik), 0, -(yukseklik * 1.50f - genislik));
        animator.setDuration(210000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        findViewById(R.id.zatenhesabimvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KayitEkrani.this, GirisEkrani.class);
                NavUtils.navigateUpTo(KayitEkrani.this, intent);
            }
        });

        findViewById(R.id.zatenhesabimvar).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    ((TextView) v).setTextColor(Color.parseColor("#DD999999"));

                if (event.getAction() == MotionEvent.ACTION_UP)
                    ((TextView) v).setTextColor(Color.WHITE);

                return false;
            }
        });

        findViewById(R.id.fabkayit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean durumadsoyad = TextUtils.isEmpty(adsoyad.getText());
                boolean durummail = TextUtils.isEmpty(mail.getText());
                boolean durumsifre = TextUtils.isEmpty(sifre.getText());
                boolean durumkullaniciadi = TextUtils.isEmpty(kullaniciadi.getText());

                tilkullaniciadi.setError(null);
                tilsifre.setError(null);
                tiladsoyad.setError(null);
                tilmail.setError(null);

                if (durumadsoyad || durummail || durumsifre || durumkullaniciadi || !mail.getText().toString().contains("@")) {

                    if (durumadsoyad)
                        tiladsoyad.setError("Lütfen ad ve soyadınızı giriniz");
                    if (durumsifre)
                        tilsifre.setError("Lütfen şifrenizi giriniz");
                    if (durumkullaniciadi)
                        tilkullaniciadi.setError("Lütfen kullanıcı adınızı giriniz");

                    if (durummail)
                        tilmail.setError("Lütfen mail adresinizi giriniz");
                    else if (!mail.getText().toString().contains("@"))
                        tilmail.setError("Lütfen geçerli bir mail adresi giriniz");

                } else {
                    //kayıt isteği gönderilecek
                    if (!internetBaglantiKontrol()) {
                        Snackbar.make(findViewById(R.id.rootKayitEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!istekGonderildi) {
                            istekGonderildi = true;
                            istekGonder();
                        }
                    }

                }
            }
        });


    }

    private void istekGonder() {
        StringRequest request = new StringRequest(Request.Method.POST, url_kayit, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json verisi", response);

                try {
                    JSONObject json = new JSONObject(response);
                    //String id = json.getString("id");
                    String durum = json.getString("status");

                    if (durum.equals("404")) {
                        Snackbar.make(findViewById(R.id.rootKayitEkrani), "Sunucu ile bağlantı kurulamadı...", Snackbar.LENGTH_LONG).show();
                        istekGonderildi = false;
                    } else if (durum.equals("400")) {
                        Snackbar.make(findViewById(R.id.rootKayitEkrani), "Verilen bilgilerle kayıt yapılamadı. Kullanıcı adı daha önce kullanılmış.", Snackbar.LENGTH_LONG).show();
                        istekGonderildi = false;
                    } else if (durum.equals("200")) {
                        Intent intent=new Intent(KayitEkrani.this, GirisEkrani.class);
                        intent.putExtra("animasyon",true);
                        startActivity(intent);
                        KayitEkrani.this.finish();
                        Snackbar.make(findViewById(R.id.rootKayitEkrani), "Kayit islemi basarili bir sekilde yapildi. Lutfen mail adresinizi kontrol ediniz.", Snackbar.LENGTH_LONG).show();
                        //sunucu ile mail gönderme işlemi şimdilik offline
                        /*new AlertDialog.Builder(KayitEkrani.this)
                                .setMessage("Kayit islemi basarili bir sekilde yapildi. Lutfen mail adresinizi kontrol ediniz.")
                                .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).show();*/

                    }

                } catch (JSONException e) {
                    Log.e("parse hatası", e.getLocalizedMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> degerler = new HashMap<>();
                degerler.put("kullaniciadi", kullaniciadi.getText().toString());
                degerler.put("sifre", sifre.getText().toString());
                degerler.put("adsoyad", adsoyad.getText().toString());
                degerler.put("mail", mail.getText().toString());
                return degerler;
            }
        };

        requestQueue.add(request);
    }


    boolean internetBaglantiKontrol() {

        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (baglantiYonetici.getActiveNetworkInfo().isAvailable() &&
                baglantiYonetici.getActiveNetworkInfo().isConnected() &&
                baglantiYonetici.getActiveNetworkInfo() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (animator != null)
            animator.pause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (animator.isPaused())
            animator.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (animator != null)
            animator.cancel();
    }
}
