package com.action.uygulamasi;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterArama extends BaseAdapter {

    private List<TweetModel> modelList;
    private Context context;

    public AdapterArama(List<TweetModel> modelList,Context context) {
        this.modelList=modelList;
        this.context=context;
    }

    @Override
    public int getCount() {
        if (modelList.size()==0)
            return 0;

        return modelList.size();
    }

    @Override
    public Object getItem(int position) {
        if (modelList.size()==0)
            return null;
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (modelList.size() == 0)
            return null;

        LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.kisi_listesi_item, parent, false);
        final TextView adsoyad= (TextView) layout.findViewById(R.id.aramaAdsoyad);
        final TextView kullaniciadi= (TextView) layout.findViewById(R.id.aramaKullaniciadi);
        TextView mail= (TextView) layout.findViewById(R.id.aramaMail);
        final CircleImageView profilImage= (CircleImageView) layout.findViewById(R.id.profile_image_arama);

        final TweetModel kisi=modelList.get(position);

        adsoyad.setText(kisi.getAdSoyad());
        kullaniciadi.setText(kisi.getKullaniciAdi());
        mail.setText(kisi.getMail());

        if (!kisi.getProfilPath().equals(""))
        {
            //Picasso.with(context).load(kisi.getProfilPath()).into(profilImage);
            Picasso.get()
                    .load(kisi.getProfilPath())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(60, 60)
                    .centerCrop()
                    .into(profilImage);
        }


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(context,KisiTweetleriActivity.class);
                intent.putExtra("id",kisi.getId());
                intent.putExtra("path",kisi.getProfilPath());
                intent.putExtra("kullaniciadi",kisi.getKullaniciAdi());
                intent.putExtra("adsoyad",kisi.getAdSoyad());
                intent.putExtra("mail",kisi.getMail());

                // Lolipop öncesi sürümlerde bu animasyon çalışmaz
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    View profil = profilImage;
                    View adSoyad = adsoyad;
                    View kullaniciAdi = kullaniciadi;
                    Pair<View, String> pairProfilFoto = Pair.create(profil, "profilImage");
                    Pair<View, String> pairAdsoyad = Pair.create(adSoyad, "adsoyad");
                    Pair<View, String> pairKullaniciadi = Pair.create(kullaniciAdi, "kullaniciadi");

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((AppCompatActivity) context, pairProfilFoto, pairAdsoyad, pairKullaniciadi);
                    context.startActivity(intent, optionsCompat.toBundle());

                }else{
                    context.startActivity(intent);
                }

            }
        });

        return layout;
    }
}
