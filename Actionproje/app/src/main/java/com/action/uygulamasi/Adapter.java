package com.action.uygulamasi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class Adapter extends BaseAdapter {

    private Context context;
    private List<TweetModel> modelList;
    private boolean silmeIslemi;


    public Adapter(Context context, List<TweetModel> modelList,boolean silmeIslemi) {
        this.modelList = modelList;
        this.context = context;
        this.silmeIslemi=silmeIslemi;
    }

    @Override
    public int getCount() {
        if (modelList == null)
            return 0;

        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (modelList == null)
            return null;

        final LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.tweet_list_item, parent, false);

        TextView adSoyad = (TextView) layout.findViewById(R.id.textView7);
        TextView kullaniciAdi = (TextView) layout.findViewById(R.id.textView6);
        TextView tarihTv = (TextView) layout.findViewById(R.id.textView5);
        TextView textTv = (TextView) layout.findViewById(R.id.text);
        CircleImageView circleImageView = (CircleImageView) layout.findViewById(R.id.profile_image_tweet);
        ImageView imImageView = (ImageView) layout.findViewById(R.id.imageView);

        final TweetModel tweet = modelList.get(position);

        adSoyad.setText(tweet.getAdSoyad());
        textTv.setText(tweet.getTweetText());
        kullaniciAdi.setText(tweet.getKullaniciAdi());

        if (!tweet.getProfilPath().equals(""))
             //Picasso.with(context).load(tweet.getProfilPath()).into(circleImageView);
        {
            Picasso.get()
                    .load(tweet.getProfilPath())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(60, 60)
                    .centerCrop()
                    .into(circleImageView);
        }

        if (!tweet.getResimPath().equals(""))
            //Picasso.with(context).load(tweet.getResimPath()).into(imImageView);
            Picasso.get().load(tweet.getResimPath()).into(imImageView);



        Date simdi = new Date();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tarih = null;
        try {
            tarih = df.parse(tweet.getTarih());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int fark = (int) (simdi.getTime() - tarih.getTime());

        int gun = fark / (1000 * 60 * 60 * 24);
        int saat = fark / (1000 * 60 * 60);
        int dakika = fark / (1000 * 60);
        int saniye = fark / (1000);

        if (saniye == 0)
            tarihTv.setText("şimdi");

        if (saniye > 0 && dakika == 0)
            tarihTv.setText(saniye + "s");

        if (dakika > 0 && saat == 0)
            tarihTv.setText(dakika + "dk");

        if (saat > 0 && gun == 0)
            tarihTv.setText(saat + "sa");

        if (gun > 0)
            tarihTv.setText(gun + "gün");

        if (silmeIslemi){
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    layout.setAlpha(.5f);
                    new AlertDialog.Builder(context)
                            .setTitle("Tweet Sil")
                            .setMessage("Tweeti silmek istediğinizden emin misiniz?")
                            .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    istekGonderSil(position, tweet.getUuid(), layout);
                                }
                            })
                            .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    layout.setAlpha(1);
                                }
                            }).show();
                    return true;
                }
            });
        }


        return layout;
    }

    private void istekGonderSil(final int position, final String uuid, final View layout) {
        final ProgressDialog loading = ProgressDialog.show(context, "Tweet siliniyor...", "Lütfen bekleyiniz...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, "http://10.0.2.2/Action/tweetSil.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Log.d("Json bilgisi: ", response);

                String durum = null, mesaj = null;

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //herşey yolundaysa
                if (durum.equals("200")) {
                    Snackbar.make(((AppCompatActivity) context).findViewById(R.id.listview), mesaj, Snackbar.LENGTH_LONG).show();
                    modelList.remove(position);
                    notifyDataSetChanged();

                } else {
                    //request başarısız ise
                    Snackbar.make(((AppCompatActivity) context).findViewById(R.id.listview), mesaj, Snackbar.LENGTH_LONG).show();
                    layout.setAlpha(1);
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
                params.put("uuid", uuid);

                if (!modelList.get(position).getResimPath().equals(""))
                    params.put("path", modelList.get(position).getResimPath());

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }


}
