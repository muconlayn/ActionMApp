package com.action.uygulamasi;


import android.animation.Animator;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.view.ViewPager;

import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
import com.action.uygulamasi.Fragments.AnasayfaFragment;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Twitter extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener

{
    private static final String url_profil_bilgileri = "http://10.0.2.2/Action/profilBilgileri.php";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView adsoyad, mail;
    private CircleImageView profilFoto;
    private SharedPreferences preferences;
    private String id;
    private int[] tabIcons = {
            R.drawable.ic_person_gri_24dp,

    };

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences.getBoolean("ProfilChanged", false)) {
            setProfilBilgileri(preferences.getString("id", "-1"));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("ProfilChanged", false);
            editor.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter);


        //giriş ekranından gelip gelmediğini kontrol ediyoruz
        boolean girisekranindangeldim = getIntent().getBooleanExtra("animasyon", false);
        if (girisekranindangeldim) {
            //--------------giriş animasyonumuzu oluşturuyoruz--------------------------
            final LinearLayout view = new LinearLayout(Twitter.this);
            ImageView icon = new ImageView(Twitter.this);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            icon.setImageResource(R.drawable.acilis_iconu);

            //iconun genişlik ve yükseklik özelliğini  250 olarak veriyoruz ve view nesnesine ekliyoruz
            view.addView(icon, 250, 250);
            getWindow().addContentView(view, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));


            Animation scaleAnim = AnimationUtils.loadAnimation(Twitter.this, R.anim.giris_animasyonu);
            icon.clearAnimation();
            icon.startAnimation(scaleAnim);

            scaleAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.0f);
                    animator.setDuration(300);
                    animator.setInterpolator(new LinearInterpolator());
                    animator.start();
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //animasyon bittikten sonra görünürlük özelliği GONE olacak yani hiç orada yokmuş gibi olacak
                            view.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            //-----------Animasyon işlemlerinin sonu-----------------
        }



        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Twitter.this, TweetGonder.class));

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        LinearLayout layout = (LinearLayout) navigationView.getHeaderView(0);
        adsoyad = (TextView) layout.findViewById(R.id.adsoyad);
        mail = (TextView) layout.findViewById(R.id.mail);
        profilFoto = (CircleImageView) layout.findViewById(R.id.profile_image);
        preferences = PreferenceManager.getDefaultSharedPreferences(Twitter.this);
        id = preferences.getString("id", "-1");
        setProfilBilgileri(id);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                toolbar.setTitle(tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void setProfilBilgileri(final String id) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_profil_bilgileri, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Json verisi", response);

                String durum = "", mesaj = "", adsoyad = "", avatar = "", mail = "";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    durum = jsonObject.getString("status");
                    mesaj = jsonObject.getString("mesaj");
                    avatar = jsonObject.getString("avatar");
                    adsoyad = jsonObject.getString("adsoyad");
                    mail = jsonObject.getString("mail");

                } catch (JSONException e) {
                    Log.e("Json parse hatası", e.getLocalizedMessage());
                }

                if (durum.equals("200")) {
                    setProfil(adsoyad, mail, avatar);
                } else {
                    Snackbar.make(findViewById(R.id.fab), mesaj, Snackbar.LENGTH_LONG).show();
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
                degerler.put("id", id);
                return degerler;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void setProfil(String adsoyad, String mail, String avatar) {
        Log.d("AVATAR", avatar);



        //profil fotoğrafının olup olmadığını kontrol ediyoruz
        if (!avatar.equals("")){
            Picasso.get()
                    .load(avatar)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(70, 70)
                    .centerCrop()
                    .into(profilFoto);
        }

        this.adsoyad.setText(adsoyad);
        this.mail.setText(mail);

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);


    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AnasayfaFragment(), "Actionlarım");


        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.twitter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            startActivity(new Intent(Twitter.this, AramaActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profil_menu) {
            // Profil activity e geçiş
            startActivity(new Intent(Twitter.this, ProfilActivity.class));
        }else if (id == R.id.cikis) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Twitter.this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("benihatirla", false);
            editor.putString("id", "-1");
            editor.commit();
            startActivity(new Intent(Twitter.this, GirisEkrani.class));
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                Fragment fragment = mFragmentList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", id);
                fragment.setArguments(bundle);
                return fragment;
            }
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
