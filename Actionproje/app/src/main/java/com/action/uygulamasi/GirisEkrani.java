package com.action.uygulamasi;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
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
import android.widget.CheckBox;
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


public class GirisEkrani extends AppCompatActivity {

    private TextInputLayout tilsifre, tilkullaniciadi;
    private TextInputEditText sifre, kullaniciadi;
    private CheckBox benihatirla;
    private RequestQueue requestQueue;
    private static final String url_login = "http://10.0.2.2/Action/login.php";
    private SharedPreferences preferences;
    private boolean istekGonderildi = false;
    private ObjectAnimator animator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tam ekran için
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_giris_ekrani);

        final ImageView image = (ImageView) findViewById(R.id.image);
        tilsifre = (TextInputLayout) findViewById(R.id.tilsifre);
        tilkullaniciadi = (TextInputLayout) findViewById(R.id.tilkullaniciadi);
        sifre = (TextInputEditText) findViewById(R.id.sifre);
        kullaniciadi = (TextInputEditText) findViewById(R.id.kullaniciadi);
        benihatirla = (CheckBox) findViewById(R.id.benihatirla);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        //beni hatırla kayıtlıysa Ana ekrana geçiş
        if (preferences.getBoolean("benihatirla", false)) {

            Intent intent=new Intent(GirisEkrani.this, Twitter.class);
            intent.putExtra("animasyon",true);
            startActivity(intent);
            GirisEkrani.this.finish();

        }

        if (!internetBaglantiKontrol()) {
            Snackbar.make(findViewById(R.id.rootGirisEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
        }



        WindowManager wm = window.getWindowManager();
        Display ekran = wm.getDefaultDisplay();

        Point point = new Point();
        ekran.getSize(point);

        final int genislik = point.x;
        final int yukseklik = point.y;

        //1.50 en boy oranı
        image.getLayoutParams().width = (int) (yukseklik * 1.50);
        image.getLayoutParams().height = yukseklik;

        animator = ObjectAnimator.ofFloat(image, "x", 0, -(yukseklik * 1.50f - genislik), 0, -(yukseklik * 1.50f - genislik));
        animator.setDuration(210000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();



        }





    public void click(View v) {

        switch (v.getId()) {
            case R.id.button:
                startActivity(new Intent(GirisEkrani.this, KayitEkrani.class));
                break;


            case R.id.fabgiris:

                boolean durumsifre = TextUtils.isEmpty(sifre.getText());
                boolean durumkullaniciadi = TextUtils.isEmpty(kullaniciadi.getText());

                tilkullaniciadi.setError(null);
                tilsifre.setError(null);
                if (durumsifre || durumkullaniciadi) {

                    if (durumsifre)
                        tilsifre.setError("Lütfen şifrenizi giriniz");
                    if (durumkullaniciadi)
                        tilkullaniciadi.setError("Lütfen kullanıcı adınızı giriniz");


                } else {
                    if (!internetBaglantiKontrol()) {
                        Snackbar.make(findViewById(R.id.rootGirisEkrani), "İnternet bağlantınızı kontrol ediniz...", Snackbar.LENGTH_LONG).show();
                    } else {
                        if (!istekGonderildi) {
                            istekGonderildi = true;
                            istekGonder();
                        }
                    }

                    break;
                }
        }
    }

    private void istekGonder() {
        StringRequest request = new StringRequest(Request.Method.POST, url_login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Json verisi", response);
                istekGonderildi = false;

                try {
                   JSONObject jsonObject = new JSONObject(response);
                   String durum = jsonObject.getString("status");
                    String mesaj = jsonObject.getString("mesaj");

                    if (durum.equals("200")) {

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("id", jsonObject.getString("id"));
                       editor.putBoolean("benihatirla", benihatirla.isChecked());
                       editor.commit();
                        //ana ekrana geçiş
                        Intent intent=new Intent(GirisEkrani.this, Twitter.class);
                        intent.putExtra("animasyon",true);
                        startActivity(intent);
                        GirisEkrani.this.finish();

                    } else {
                        Snackbar.make(findViewById(R.id.rootGirisEkrani), mesaj, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();

        if (animator!=null)
        animator.pause();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();

        if (animator.isPaused())
        animator.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (animator!=null)
        animator.cancel();
    }
}
