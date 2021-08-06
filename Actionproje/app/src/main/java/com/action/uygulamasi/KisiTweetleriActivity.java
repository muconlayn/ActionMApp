package com.action.uygulamasi;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class KisiTweetleriActivity extends AppCompatActivity {

    private TextView adsoyad,kullaniciadi,mail;
    private ListView listView;
    private String id;
    private RequestQueue requestQueue;
    private CircleImageView profilFoto;
    private List<TweetModel> modelList;
    private String url = "http://10.0.2.2/Action/getTweetler.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kisi_tweetleri);


        this.adsoyad= (TextView) findViewById(R.id.kisiAdsoyad);
        this.kullaniciadi= (TextView) findViewById(R.id.kisiKullaniciadi);
        this.mail= (TextView) findViewById(R.id.kisiMail);
        this.profilFoto= (CircleImageView) findViewById(R.id.profile_image_kisi);
        this.listView= (ListView) findViewById(R.id.kisitweetlerilist);
        this.requestQueue = Volley.newRequestQueue(getApplicationContext());
        modelList=new ArrayList<>();

        Bundle arguments=getIntent().getExtras();
        String path=arguments.getString("path","");
        String adsoyad=arguments.getString("adsoyad","");
        String kullaniciadi=arguments.getString("kullaniciadi","");
        String mail=arguments.getString("mail","");
        this.id=arguments.getString("id","-1");

        this.adsoyad.setText(adsoyad);
        this.kullaniciadi.setText(kullaniciadi);
        this.mail.setText(mail);
        this.adsoyad.setText(adsoyad);

        if (!path.equals(""))
            //Picasso.with(getApplicationContext()).load(path).into(profilFoto);
            Picasso.get().load(path).into(profilFoto);

        istekGonder();

    }

    private void istekGonder() {
        final ProgressDialog loading = ProgressDialog.show(KisiTweetleriActivity.this, "Tweetler yükleniyor...", "Lütfen bekleyiniz...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.d("Json bilgisi Tweetler: ", response);

                String durum = null, mesaj = null;
                JSONArray tweetler = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                    tweetler = jsonObject.getJSONArray("tweetler");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //herşey yolundaysa
                if (durum.equals("200")) {

                    if (tweetler.length()==0) {
                        Snackbar.make(listView,"Hicbir tweet bulunamadi..." , Snackbar.LENGTH_LONG).show();
                    }else {

                        for (int i = 0; i < tweetler.length(); i++) {
                            JSONObject tweet;
                            TweetModel model = new TweetModel();
                            try {
                                tweet = tweetler.getJSONObject(i);
                                model.setAdSoyad(tweet.getString("adsoyad"));
                                model.setKullaniciAdi(tweet.getString("kullaniciadi"));
                                model.setProfilPath(tweet.getString("avatar"));
                                model.setResimPath(tweet.getString("path"));
                                model.setTweetText(tweet.getString("text"));
                                model.setTarih(tweet.getString("date"));
                                model.setUuid(tweet.getString("uuid"));
                            } catch (JSONException e) {
                                Log.e("json parse hatası", e.getLocalizedMessage());
                            }

                            modelList.add(model);

                        }

                        Adapter adapter=new Adapter(KisiTweetleriActivity.this,modelList,false);
                        listView.setAdapter(adapter);


                    }



                }

                else {
                    //request başarısız ise
                    Snackbar.make(listView, mesaj, Snackbar.LENGTH_LONG).show();
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
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };


        requestQueue.add(request);
    }//istekGonder metodu sonu
}
