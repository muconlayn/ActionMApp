package com.action.uygulamasi.Fragments;



import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;




import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

// paket içerisinde kullandığımız için import etmek zorundayız
import com.action.uygulamasi.Adapter;
import com.action.uygulamasi.R;
import com.action.uygulamasi.TweetModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnasayfaFragment extends Fragment {

    private String id;
    private Context context;
    private List<TweetModel> modelList;
    //private RecyclerView recyclerView;
    private ListView listView;
    private TextView textView;
    private SwipeRefreshLayout refreshLayout;


    private String url = "http://10.0.2.2/Action/getTweetler.php";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.anasayfa_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //recyclerView = (RecyclerView) ((AppCompatActivity) context).findViewById(R.id.listTweet);
        listView = (ListView) ((AppCompatActivity) context).findViewById(R.id.listview);
        textView = (TextView) ((AppCompatActivity) context).findViewById(R.id.textView3);
        refreshLayout = (SwipeRefreshLayout) ((AppCompatActivity) context).findViewById(R.id.refresh);

        // refreshLayout a 3 tane renk değeri veriyoruz. İşlem uzadıkçe sırayla verilen renk değerlerini alacak
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent),Color.BLUE,Color.GREEN);

        modelList = new ArrayList<>();
        id = getArguments().getString("id");

        //recyclerView.setHasFixedSize(true);
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setItemAnimator(new DefaultItemAnimator());

        istekGonder();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                modelList.clear();
                istekGonderRefresh();
            }
        });

        Log.d("Volley işlemleri testi", ".............................................");


    }

    private void istekGonder() {
        //final ProgressDialog loading = ProgressDialog.show(context, "Tweetler yükleniyor...", "Lütfen bekleyiniz...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //loading.dismiss();
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
                            textView.setText("Hiçbir tweet bulunamadı...");
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

                            setAdapter();
                        }



                    }

                else {
                    //request başarısız ise
                    Snackbar.make(listView, mesaj, Snackbar.LENGTH_LONG).show();
                }
                Log.d("Volley işlemleri testi", "request işlemler tamamlandı.........................");


                /*if (modelList.size() == 0) {
                   textView.setText("Hiçbir tweet bulunamadı...");
                } else {
                    setAdapter();
                }
*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }//istekGonder metodu sonu

    private void setAdapter() {

        //RecyclerView.Adapter mAdapter = new MyAdapter(modelList,context);
        //recyclerView.setAdapter(mAdapter);
        Adapter adapter = new Adapter(context, modelList,true);
        listView.setAdapter(adapter);


    }

    private void istekGonderRefresh() {

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refreshLayout.setRefreshing(false);
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
                        textView.setText("Hiçbir tweet bulunamadı...");
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
                        setAdapter();
                    }

                } else {
                    //request başarısız ise
                    Snackbar.make(listView, mesaj, Snackbar.LENGTH_LONG).show();
                }
                Log.d("Volley işlemleri testi", "request işlemler tamamlandı.........................");

               /* if (modelList.size() == 0) {
                    textView.setText("Hiçbir tweet bulunamadı...");
                    setAdapter();
                } else {
                    setAdapter();
                }
*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


}
