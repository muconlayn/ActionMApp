package com.action.uygulamasi;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<TweetModel> modelList;
    private Context context;

    public MyAdapter(List<TweetModel> modelList,Context c) {
        //yapıcı metoda (Constructor) gelen context ve tweet bilgilerini sınıf değişkenlerine değer olarak atıyoruz
        this.modelList = modelList;
        this.context=c;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //LayoutInflater ile xml ile oluşturduğumuz layoutumuzu rootView isimli View nesnesine dönüştürüyoruz.
        LinearLayout rootView = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_list_item, parent, false);

        //rootView i ViewHolder sınıfının yapıcı metoduna (Constructor) parametre olarak gönderiyoruz
        ViewHolder vh = new ViewHolder(rootView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        TweetModel tweet = modelList.get(position);
        //holder üzerinden arayüz elemanlarına erişip gerekli bilgileri set ediyoruz.
        holder.adSoyad.setText(tweet.getAdSoyad());
        holder.textTv.setText(tweet.getTweetText());
        holder.kullaniciAdi.setText(tweet.getKullaniciAdi());

        if (!tweet.getProfilPath().equals(""))
        {
            //Picasso.with(context).load(tweet.getProfilPath()).into(holder.circleImageView);
            Picasso.get()
                    .load(tweet.getProfilPath())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(60, 60)
                    .centerCrop()
                    .into(holder.circleImageView);
        }


        if (!tweet.getResimPath().equals("")) {
            //Picasso kullanarak
            //Picasso.with(context).load(tweet.getResimPath()).into(holder.imImageView);
            Picasso.get().load(tweet.getResimPath()).into(holder.imageView);


            //Volley kullanarak
            ImageLoader.ImageCache imageCache = new BitmapLruCache ();
            ImageLoader imageLoader = new ImageLoader (Volley.newRequestQueue(context),imageCache);
            holder.imageView.setImageUrl (tweet.getResimPath(),imageLoader);
            holder.imageView.setDefaultImageResId (R.mipmap.icon);
            holder.imageView.setErrorImageResId (R.mipmap.icon);
        }



        Date simdi=new Date();

        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tarih=null;
        try {
           tarih=df.parse(tweet.getTarih());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int fark= (int) (simdi.getTime()-tarih.getTime());

        int gun=fark/(1000*60*60*24);
        int saat=fark/(1000*60*60);
        int dakika=fark/(1000*60);
        int saniye=fark/(1000);

        if (saniye==0)
            holder.tarihTv.setText("şimdi");

        if (saniye>0 && dakika==0)
            holder.tarihTv.setText(saniye+"s");

        if (dakika>0 && saat==0)
            holder.tarihTv.setText(dakika+"dk");

        if (saat>0 && gun==0)
            holder.tarihTv.setText(saat+"sa");

        if (gun>0)
            holder.tarihTv.setText(gun+"gün");


        Log.d("pozisyon:::::::: ",String.valueOf(position));


    }

    @Override
    public int getItemCount() {

        //Listedeki item ların sayısını belirtiyoruz
        if (modelList == null)
            return 0;
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //public olarak View nesnelerini tanımlıyoruz
        public TextView adSoyad, kullaniciAdi, tarihTv, textTv;
        public CircleImageView circleImageView;
        public ImageView imImageView;
        public NetworkImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            //itemView üzerinden arayüz elemanlarını tanımlıyoruz
            adSoyad = (TextView) itemView.findViewById(R.id.textView7);
            kullaniciAdi = (TextView) itemView.findViewById(R.id.textView6);
            tarihTv = (TextView) itemView.findViewById(R.id.textView5);
            textTv = (TextView) itemView.findViewById(R.id.text);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.profile_image_tweet);
            //imImageView = (ImageView) itemView.findViewById(R.id.imageView);
            //imageView = (NetworkImageView)itemView.findViewById(R.id.img);
        }
    }
}
