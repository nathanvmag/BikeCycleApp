package bikecycle.com.bikecycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.concurrent.CountDownLatch;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class EntregadorLayout extends AppCompatActivity {

    private TextView mTextMessage;
    private RelativeLayout mainlayout;
    private String myid,myfoto,nome;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mainlayout.removeAllViews();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    getLayoutInflater().inflate(R.layout.entrega_main,mainlayout,true);
                    ( (TextView)findViewById(R.id.bnvd)).setText("Bem vindo "+nome);
                    getState((BootstrapButton)findViewById(R.id.imworking));

                    ((BootstrapButton)findViewById(R.id.imworking)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BootstrapButton bt = (BootstrapButton)view;
                            setWorkstate(bt.isShowOutline()?1:0,bt);
                        }
                    });
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    getLayoutInflater().inflate(R.layout.entrega_configs,mainlayout,true);
                    findViewById(R.id.saibot).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
                            SharedPreferences.Editor editor= pm.edit();
                            editor.clear();
                            editor.commit();
                            startActivity(new Intent(getBaseContext(),loginPage.class));
                        }
                    });
                    findViewById(R.id.altsenha).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(view.getContext(),altersenha.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","1");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.altcad).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RequestParams rp= new RequestParams();
                            rp.add("servID","11");
                            rp.add("id",myid);
                            rp.add("tipe","1");
                            utils.log("VOu começa");
                            HttpUtils.postByUrl(basesite+"application.php", rp, new AsyncHttpResponseHandler(){


                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String resp= new String(responseBody);
                                    utils.log(resp);
                                    if(resp.length()>10)
                                    {
                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"register.php?tp=0&id="+myid+"&acess="+resp)));

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

        }};
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregador_layout);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mainlayout= findViewById(R.id.mainlayout);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nome= extras.getString("nome");
            myid= extras.getString("id");
            myfoto=extras.getString("foto","");
            SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
            SharedPreferences.Editor editor= pm.edit();
            editor.putString("login",extras.getString("login",""));
            editor.putString("pass",extras.getString("pass",""));
            editor.putInt("tipe",1);
            editor.commit();
        }
        findViewById(R.id.navigation_home).callOnClick();
    }
    void setWorkstate(int state, final BootstrapButton bt)
    {
        RequestParams rp= new RequestParams();
        rp.add("servID","16");
        rp.add("id",myid);
        rp.add("work",state+"/");
        AsyncHttpResponseHandler atp= new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               // utils.log(new String(responseBody));
                if(!new String(responseBody).equals("OK"))
                    Toast.makeText(getBaseContext(),"Falha ao alterar estado"+new String(responseBody),Toast.LENGTH_LONG).show();
                else {
                    if(bt.isShowOutline())
                    {
                        bt.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                        bt.setShowOutline(false);
                        bt.setText("Disponível");
                    }
                    else{
                        bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        bt.setShowOutline(true);
                        bt.setText("Indisponível");
                    }
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(),"Falha ao alterar estado"+new String(responseBody),Toast.LENGTH_LONG).show();
            }
        };

        HttpUtils.postByUrl(basesite+"application.php", rp,atp);
    }
    void getState(final BootstrapButton bt)
    {
        RequestParams rp= new RequestParams();
        rp.add("servID","15");
        rp.add("id",myid);
        AsyncHttpResponseHandler atp= new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s= new String(responseBody);
                //utils.log(s);
                try{
                    Integer value= Integer.parseInt(s);
                    if(value==1)
                    {
                        bt.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                        bt.setShowOutline(false);
                        bt.setText("Disponível");
                    }
                    else{
                        bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        bt.setShowOutline(true);
                        bt.setText("Indisponível");
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getBaseContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                    bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    bt.setShowOutline(true);
                    bt.setText("Indisponível");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                bt.setShowOutline(true);
                bt.setText("Indisponível");
               // utils.log(new String(responseBody));
            }
        };
        HttpUtils.postByUrl(basesite+"application.php",rp,atp);
    }
    public static Boolean Ask(Context ctx,String Pergunta)
    {
        final boolean[] resu = new boolean[1];
        final boolean[] repeat = {true};
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        resu[0] =true;
                        repeat[0] =false;
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        resu[0]=false;
                        repeat[0] =false;
                        break;
                        default:
                            repeat[0] =false;
                            break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(Pergunta).setPositiveButton("Sim", dialogClickListener)
                .setNegativeButton("Não", dialogClickListener).show();

          return resu[0];
    }


}
