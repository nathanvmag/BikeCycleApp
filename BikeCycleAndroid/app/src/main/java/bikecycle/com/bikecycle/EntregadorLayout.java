package bikecycle.com.bikecycle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class EntregadorLayout extends AppCompatActivity implements  Runnable
{

    private TextView mTextMessage;
    private RelativeLayout mainlayout,displayout,indisplayout;
    private String myid,myfoto,nome;
    BootstrapButton Aceitar;
    private Handler handler;
    boolean inMain=false,inHistory=false;
    int PedidosAtivos=0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mainlayout.removeAllViews();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    inMain=true;
                    inHistory=false;
                    getLayoutInflater().inflate(R.layout.entrega_main,mainlayout,true);
                    indisplayout=findViewById(R.id.indsplay);
                    displayout= findViewById(R.id.display);
                    indisplayout.setVisibility(View.INVISIBLE);
                    displayout.setVisibility(View.INVISIBLE);
                    Aceitar= findViewById(R.id.aceitarent);
                    Aceitar.setEnabled(false);

                    ( (TextView)findViewById(R.id.bnvd)).setText("Bem vindo "+nome);

                    getState((BootstrapButton)findViewById(R.id.imworking));
                    getdispo();

                    ((BootstrapButton)findViewById(R.id.imworking)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BootstrapButton bt = (BootstrapButton)view;
                            setWorkstate(bt.isShowOutline()?1:0,bt);
                        }
                    });
                    Aceitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            RequestParams rp = new RequestParams();
                            rp.add("servID","742");
                            rp.add("id",myid);
                            view.setEnabled(false);
                            if(PedidosAtivos<3){

                            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String resp=new String(responseBody);
                                    utils.log(resp);

                                    view.setEnabled(true);
                                    if (resp.equals("OK"))
                                    {
                                        findViewById(R.id.navigation_dashboard).callOnClick();
                                        utils.log("Sucesso ao receber o pedido, cliquei nele para mais informações");
                                    }
                                    else {
                                        utils.toast(getBaseContext(),"Falha ao aceitar pedido "+resp);

                                    }

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    view.setEnabled(false);
                                    String resp=new String(responseBody);
                                    utils.log(resp);
                                    utils.toast(getBaseContext(),"Falha ao aceitar pedido "+resp);

                                }
                            });
                        }else{
                                utils.toast(view.getContext(),"Você já possui 3 pedidos em andamento, finalize-os primeiro");
                            }
                        }
                    });
                    return true;
                case R.id.navigation_dashboard:
                    inMain=false;
                    inHistory=true;
                    getLayoutInflater().inflate(R.layout.cliente_history,mainlayout,true);
                    final ListView lv= (ListView)findViewById(R.id.entregalist);
                    final List<Entrega> entregas= new ArrayList<>();
                    RequestParams rp= new RequestParams();
                    rp.add("servID","88");
                    rp.add("id",myid);
                    HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String resp = new String(responseBody);
                            utils.log("resposta "+resp);
                            try {
                                String[] modules= resp.split("%");
                                for(int i=0;i<modules.length;i++)
                                {
                                    String[] infs= modules[i].split("!");
                                    Entrega e = new Entrega(infs[1],infs[4],infs[5],Integer.parseInt(infs[3]),Integer.parseInt(infs[0]),1);
                                    utils.log("EAII NOVO "+i);
                                    entregas.add(e);
                                    findViewById(R.id.textView2).setVisibility(View.INVISIBLE);

                                }
                                utils.log("SAIU  LOOP ");

                                entregaAdpter adapter =
                                        new entregaAdpter(entregas, getBaseContext());
                                lv.setAdapter(adapter);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                        RequestParams req =new RequestParams();
                                        view.setEnabled(false);
                                        final Entrega entreg= (Entrega) adapterView.getItemAtPosition(i);
                                        utils.log("vamos ?");
                                            req.add("servID","777");
                                            req.add("entreID",entreg.entregaid+"");
                                            view.setEnabled(false);
                                            HttpUtils.postByUrl(basesite + "application.php", req, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    view.setEnabled(true);
                                                    final String res= new String(responseBody);
                                                    utils.log(res);
                                                    String[] infs= res.split("%",5);

                                                    if(infs.length==5){
                                                        LayoutInflater inflater =getLayoutInflater();
                                                        //Inflate the view from a predefined XML layout
                                                        View layout = inflater.inflate(R.layout.entregador_cliente_info,
                                                                (ViewGroup) findViewById(R.id.mainlayout),false);
                                                        // create a 300px width and 470px height PopupWindow
                                                        final PopupWindow pw =new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                                                                LinearLayout.LayoutParams.MATCH_PARENT, true);
                                                        // display the popup in the center
                                                        pw.showAtLocation(mainlayout, Gravity.CENTER, 0, 0);
                                                        layout.findViewById(R.id.dimissinfo).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                pw.dismiss();
                                                            }
                                                        });
                                                        try{
                                                            infs[1]="("+infs[1].substring(0,2)+") "+infs[1].substring(2,7)+"-"+infs[1].substring(7);
                                                        }
                                                        catch (Exception e){

                                                        }

                                                        ((TextView)layout.findViewById(R.id.infoname)).setText(infs[0]);
                                                        ((TextView)layout.findViewById(R.id.infotel)).setText(infs[1]);
                                                        ((TextView)layout.findViewById(R.id.infodata)).setText("Data: "+ entreg.dataa);
                                                        ((TextView)layout.findViewById(R.id.horater2)).setText("Pedido iniciado às: "+entreg.starthora);
                                                        ((TextView)layout.findViewById(R.id.horaini)).setText("Pedido Finalizado às: " +(infs[3].equals("")?"--:--": infs[3].replace("-",":")));
                                                        ((TextView)layout.findViewById(R.id.infostatus)).setText(entreg.status[entreg.statusid]);
                                                        ((TextView)layout.findViewById(R.id.idview)).setText("#"+entreg.entregaid);

                                                        try{
                                                            infs[4]= infs[4].replace("?","%");
                                                           String[]ends= infs[4].split("%",6);
                                                            ((TextView)layout.findViewById(R.id.inforua)).setText("Rua: "+ends[0]);
                                                            ((TextView)layout.findViewById(R.id.infonum5)).setText("Bairro: "+ends[1]);
                                                            ((TextView)layout.findViewById(R.id.infonum3)).setText("Número: "+ends[2]);
                                                            ((TextView)layout.findViewById(R.id.infonum4)).setText("Complemento: "+ends[3]);
                                                            ((TextView)layout.findViewById(R.id.infonum)).setText("Cep: "+ends[4]);
                                                            ((TextView)layout.findViewById(R.id.infonum2)).setText("Complemento: "+ends[5]);

                                                        }
                                                        catch (Exception e)
                                                        {

                                                        }
                                                        new DownloadImageTask((ImageView) layout.findViewById(R.id.infofoto))
                                                                .execute(loginPage.basesite+infs[2]);
                                                        BootstrapProgressBar progressBar= (BootstrapProgressBar)layout.findViewById(R.id.progbar2);
                                                        progressBar.setProgress(entreg.statusid+1);
                                                        if(entreg.statusid==0)progressBar.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                                                        else if(entreg.statusid<3)progressBar.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                                                        else progressBar.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                        int stID= entreg.statusid;

                                                }}

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    view.setEnabled(true);
                                                    Toast.makeText(getBaseContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                }
                                            });

                                };

                        });}
                        catch (Exception e)
                            {
                                findViewById(R.id.textView2).setVisibility(View.VISIBLE);

                                Toast.makeText(getBaseContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                            utils.log("resposta "+new String(responseBody));

                            Toast.makeText(getBaseContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();

                        }
                    } );


                    return true;
                case R.id.navigation_notifications:
                    inMain=false;
                    inHistory=false;

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
        handler= new Handler();
        handler.postDelayed(this,2000);
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
                        displayout.setVisibility(View.VISIBLE);
                        indisplayout.setVisibility(View.INVISIBLE);
                    }
                    else{
                        bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        bt.setShowOutline(true);
                        bt.setText("Indisponível");
                        displayout.setVisibility(View.INVISIBLE);
                        indisplayout.setVisibility(View.VISIBLE);
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
                        displayout.setVisibility(View.VISIBLE);
                        indisplayout.setVisibility(View.INVISIBLE);
                    }
                    else{
                        bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                        bt.setShowOutline(true);
                        bt.setText("Indisponível");
                        displayout.setVisibility(View.INVISIBLE);
                        indisplayout.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e)
                {
                    Toast.makeText(getBaseContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                    bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    bt.setShowOutline(true);
                    bt.setText("Indisponível");

                    displayout.setVisibility(View.INVISIBLE);
                    indisplayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                bt.setShowOutline(true);
                bt.setText("Indisponível");

                displayout.setVisibility(View.INVISIBLE);
                indisplayout.setVisibility(View.VISIBLE);
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


    @Override
    public void run() {
        if(inMain&&displayout.getVisibility()==View.VISIBLE){
            getdispo();
            handler.postDelayed(this,5000);

        }
        else if (inHistory&&displayout.getVisibility()==View.VISIBLE)
        {
            findViewById(R.id.navigation_dashboard).callOnClick();
            handler.postDelayed(this,90000);

        }
        else{
        handler.postDelayed(this,5000);

    }}
    void getdispo()
    {
        RequestParams rp= new RequestParams();
        rp.add("servID","54");
        rp.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    int a = Integer.parseInt(new String(responseBody));
                    ((TextView)findViewById(R.id.dispotext)).setText("Existem "+a+" pedidos para você neste momento");
                    if(a>0)Aceitar.setEnabled(true);
                    else Aceitar.setEnabled(false);

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
        RequestParams rps= new RequestParams();
        rps.add("servID","775");
        rps.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    PedidosAtivos= Integer.parseInt(new String(responseBody));
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
