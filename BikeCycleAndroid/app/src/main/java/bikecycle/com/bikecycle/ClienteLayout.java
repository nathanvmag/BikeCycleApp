package bikecycle.com.bikecycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class ClienteLayout extends AppCompatActivity implements Runnable
{

    private String myid,myfoto,nome;
    RelativeLayout mainlayout;
    Drawable clientlogo;
    Boolean inMain=false;
    private Handler handler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mainlayout.removeAllViews();
            inMain=false;
            switch (item.getItemId()) {
                case R.id.navigation_home2:
                    getLayoutInflater().inflate(R.layout.cliente_main,mainlayout,true);
                    ((TextView)findViewById(R.id.benvindclient)).setText("Bem vindo: "+nome);
                    new DownloadImageTask((ImageView) findViewById(R.id.clientlogo))
                            .execute(loginPage.basesite+myfoto);
                    findViewById(R.id.solicita).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            RequestParams rp = new RequestParams();
                            rp.add("servID","76");
                            rp.add("id",myid);
                            view.setEnabled(false);
                            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    view.setEnabled(true);
                                    String resp =new String(responseBody);
                                    if(resp.equals("OK"))
                                    {
                                        findViewById(R.id.navigation_dashboard2).callOnClick();
                                        Toast.makeText(getBaseContext(),"Sucesso ao realizar pedido, aguarde por entregadores  ",Toast.LENGTH_LONG).show();

                                    }
                                    else{
                                        Toast.makeText(getBaseContext(),"Falha ao solicitar entregador "+resp,Toast.LENGTH_LONG).show();

                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    view.setEnabled(true);
                                    Toast.makeText(getBaseContext(),"Falha ao solicitar entregador" +new String(responseBody),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    inMain=true;

                    //((ImageView)findViewById(R.id.clientlogo)).setImageDrawable(clientlogo==null?getResources().getDrawable(R.drawable.logo):clientlogo);
                    return true;
                case R.id.navigation_dashboard2:
                    getLayoutInflater().inflate(R.layout.cliente_history,mainlayout,true);
                    final ListView lv= (ListView)findViewById(R.id.entregalist);
                    final List<entregas> ents= new ArrayList<>();
                    /*ents.add(new entregas("7","30/02/2018","11:20",0,29));
                    ents.add(new entregas("7","25/02/2018","20:20",1,29));
                    ents.add(new entregas("7","21;02/2018","19:20",2,29));
                    ents.add(new entregas("7","22;02/2018","12:20",3,29));
                    entregaAdpter adapter =
                            new entregaAdpter(ents, getBaseContext());

                    lv.setAdapter(adapter);
                    */
                    RequestParams rp= new RequestParams();
                    rp.add("servID","87");
                    rp.add("id",myid);
                    HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String resp = new String(responseBody);
                            utils.log(resp);
                            try {
                                String[] modules= resp.split("%");
                                for(int i=0;i<modules.length;i++)
                                {
                                    String[] infs= modules[i].split("!");
                                    entregas e = new entregas(infs[2],infs[4],infs[5],Integer.parseInt(infs[3]),Integer.parseInt(infs[0]));
                                    utils.log("EAII NOVO "+i);
                                    ents.add(e);
                                }
                                utils.log("SAIU  LOOP ");

                                entregaAdpter adapter =
                                        new entregaAdpter(ents, getBaseContext());
                                lv.setAdapter(adapter);
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getBaseContext(),"Falha ao obter histórico  "+e.getMessage(),Toast.LENGTH_LONG).show();
                                utils.log(e);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getBaseContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();

                        }
                    });


                    return true;
                case R.id.navigation_notifications2:
                    View v =getLayoutInflater().inflate(R.layout.entrega_configs,mainlayout,true);
                    v.findViewById(R.id.saibot).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
                            SharedPreferences.Editor editor= pm.edit();
                            editor.clear();
                            editor.commit();
                            startActivity(new Intent(getBaseContext(),loginPage.class));
                        }
                    });
                    v.findViewById(R.id.altsenha).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(view.getContext(),altersenha.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","0");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.altcad).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RequestParams rp= new RequestParams();
                            rp.add("servID","11");
                            rp.add("id",myid);
                            rp.add("tipe","0");
                            utils.log("VOu começa");
                            HttpUtils.postByUrl(basesite+"application.php", rp, new AsyncHttpResponseHandler(){


                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String resp= new String(responseBody);
                                    utils.log(resp);
                                    if(resp.length()>10)
                                    {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"register.php?tp=1&id="+myid+"&acess="+resp)));

                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    String resp= new String(responseBody);
                                    Toast.makeText(getBaseContext(),"Falha ao alterar cadastro "+resp,Toast.LENGTH_LONG).show();
                                    utils.log("Falhou "+resp);
                                }
                            });
                            utils.log("VOu termina");

                        }});
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_layout);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainlayout= findViewById(R.id.clientelayoutmain);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nome= extras.getString("nome");
            myid= extras.getString("id");
            myfoto=extras.getString("foto","");
            SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor editor= pm.edit();
            editor.putString("login",extras.getString("login",""));
            editor.putString("pass",extras.getString("pass",""));
            editor.putInt("tipe",0);
            editor.commit();
        }
        findViewById(R.id.navigation_home2).callOnClick();
        handler = new Handler();
        handler.post(this);
    }


    @Override
    public void run() {
        handler.postDelayed(this,10000);
        if(inMain){
            RequestParams rp= new RequestParams();
            rp.add("servID","65");
            rp.add("id",myid);
            utils.log("Comecei");
            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try{
                        int a = Integer.parseInt(new String(responseBody));
                        ((TextView)findViewById(R.id.numsolicita)).setText("Você possui "+a+" pedidos em aberto");

                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getBaseContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getBaseContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}




 class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            utils.log( e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
class entregaAdpter extends BaseAdapter {

    private final List<entregas> entre;
    Context act ;
    public entregaAdpter(List<entregas> cursos, Context act) {
        this.entre = cursos;
        this.act=act;
    }
    @Override
    public int getCount() {
        return entre.size();
    }

    @Override
    public Object getItem(int i) {
        return entre.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v= ((Activity)viewGroup.getContext()) .getLayoutInflater().inflate(R.layout.entrega_history_list,viewGroup,false);
        entregas ent= entre.get(i);
        ((BootstrapLabel)v.findViewById(R.id.statustx)).setText(ent.status[ent.statusid]);
        ((TextView)v.findViewById(R.id.datast)).setText(ent.dataa);
        ((TextView)v.findViewById(R.id.horast)).setText(ent.starthora);
        BootstrapProgressBar progressBar= (BootstrapProgressBar)v.findViewById(R.id.progbar);
        progressBar.setProgress(ent.statusid+1);
        if(ent.statusid==0)progressBar.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
        else if(ent.statusid<3)progressBar.setBootstrapBrand(DefaultBootstrapBrand.INFO);
        else progressBar.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        return v;
    }
}
