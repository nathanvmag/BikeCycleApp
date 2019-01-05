package bikecycle.com.bikecycle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
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
    Boolean inMain=false,inHistory= false
            ;

    Integer entregadoresDisponiveis=0;
    private Handler handler;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            mainlayout.removeAllViews();
            inMain=false;
            inHistory=false;
            switch (item.getItemId()) {
                case R.id.navigation_home2:

                    inHistory=false;
                    getLayoutInflater().inflate(R.layout.cliente_main,mainlayout,true);
                    ((TextView)findViewById(R.id.benvindclient)).setText("Bem vindo: "+nome);
                    new DownloadImageTask2((BootstrapCircleThumbnail) findViewById(R.id.clientlogo))
                            .execute(loginPage.basesite+myfoto);
                    getdispo();
                    ((CheckBox)findViewById(R.id.permitavulso)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            ((BootstrapButton)findViewById(R.id.solicita)).setEnabled(b);

                        }
                    });
                    findViewById(R.id.solicitaalocado).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            RequestParams rp = new RequestParams();
                                            rp.add("servID","77");
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
                                                        if(entregadoresDisponiveis>0)
                                                            Toast.makeText(getBaseContext(),"Sucesso ao realizar pedido, aguarde por entregadores alocados ",Toast.LENGTH_LONG).show();
                                                        else {
                                                            utils.toast(view.getContext(),"No momento não há entregadores disponíveis, aguarde um momento que seu pedido será aceito");
                                                            RequestParams rps= new RequestParams();
                                                            rps.add("servID","887");
                                                            rps.add("title","Falta de entregadores ");
                                                            rps.add("mess","Foi solicitado entregadores alocados para meus pedidos e no momento não tive nenhum entregador disponível");
                                                            rps.add("from","1");
                                                            rps.add("id",myid);
                                                            HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    utils.log("Resultado do pedido "+new String(responseBody));
                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {


                                                                    utils.log("Resultado do error  "+new String(responseBody)+" "+error);
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(getBaseContext(),"Falha ao solicitar entregador alocado "+resp,Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    view.setEnabled(true);
                                                    Toast.makeText(getBaseContext(),"Falha ao solicitar entregador" +new String(responseBody),Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Você tem certeza que deseja solicitar um entregador alocado?").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).create().show();


                        }
                    });
                    findViewById(R.id.solicita).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
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
                                                        if(entregadoresDisponiveis>0)
                                                            Toast.makeText(getBaseContext(),"Sucesso ao realizar pedido, aguarde por entregadores  ",Toast.LENGTH_LONG).show();
                                                        else {
                                                            utils.toast(view.getContext(),"No momento não há entregadores disponíveis, aguarde um momento que seu pedido será aceito");
                                                            RequestParams rps= new RequestParams();
                                                            rps.add("servID","887");
                                                            rps.add("title","Falta de entregadores ");
                                                            rps.add("mess","Foi solicitado entregadores para meus pedidos e no momento não tive nenhum entregador disponível");
                                                            rps.add("from","1");
                                                            rps.add("id",myid);
                                                            HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    utils.log("Resultado do pedido "+new String(responseBody));
                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {


                                                                    utils.log("Resultado do error  "+new String(responseBody)+" "+error);
                                                                }
                                                            });
                                                        }
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
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Você tem certeza que deseja solicitar um entregador ?").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).create().show();


                        }
                    });
                    inMain=true;

                    //((ImageView)findViewById(R.id.clientlogo)).setImageDrawable(clientlogo==null?getResources().getDrawable(R.drawable.logo):clientlogo);
                    return true;
                case R.id.navigation_dashboard2:

                    getLayoutInflater().inflate(R.layout.cliente_history,mainlayout,true);
                    final ListView lv= (ListView)findViewById(R.id.entregalist);
                    final List<Entrega> ents= new ArrayList<>();
                    inHistory=true;


                            RequestParams rp= new RequestParams();
                    rp.add("servID","87");
                    rp.add("id",myid);
                    HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String resp = new String(responseBody);
                            utils.log(resp);
                            final TextView tx=(TextView)findViewById(R.id.textView2);

                            try {

                                String[] modules= resp.split("%");
                                for(int i=0;i<modules.length;i++)
                                {
                                    String[] infs= modules[i].split("!");
                                    Entrega e = new Entrega(infs[2],infs[4],infs[5],Integer.parseInt(infs[3]),Integer.parseInt(infs[0]),0,infs[6]);
                                    ents.add(e);
                                    tx.setVisibility(View.INVISIBLE);

                                }
                                utils.log("SAIU  LOOP ");

                                entregaAdpter adapter =
                                        new entregaAdpter(ents, getBaseContext());
                                lv.setAdapter(adapter);

                                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                        final Entrega entrega= (Entrega)adapterView.getItemAtPosition(i);
                                        if(entrega.statusid==0)
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            RequestParams rp = new RequestParams();
                                                            rp.add("servID","445");
                                                            rp.add("entid",entrega.entregaid+"");
                                                            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    String resp= new String(responseBody);
                                                                    if(resp.equals("OK"))
                                                                    {
                                                                        findViewById(R.id.navigation_dashboard2).callOnClick();
                                                                        utils.toast(view.getContext(),"Sucesso ao cancelar o pedido ");

                                                                    }
                                                                    else {
                                                                        utils.toast(view.getContext(),"Falha ao cancelar o pedido "+resp);
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    utils.toast(view.getContext(),"Falha ao cancelar o pedido "+new String(responseBody));
                                                                }
                                                            });

                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setMessage("Você tem certeza que deseja cancelar esse pedido ?").setPositiveButton("Sim", dialogClickListener)
                                                    .setNegativeButton("Não", dialogClickListener).show();
                                        }
                                        return false;
                                    }
                                });
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                        RequestParams rp =new RequestParams();
                                        final Entrega entreg= (Entrega) adapterView.getItemAtPosition(i);
                                        if(entreg.statusid!=0){
                                        rp.add("servID","773");
                                        rp.add("entreID",entreg.entregaid+"");
                                        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                final String res= new String(responseBody);
                                                String[] infs= res.split("%",4);
                                                utils.log("Minha end hora "+infs[3] +"  "+res);
                                                try{
                                                    infs[1]="("+infs[1].substring(0,2)+") "+infs[1].substring(2,7)+"-"+infs[1].substring(7);
                                                }
                                                catch (Exception e){

                                                }
                                                if(infs.length==4){
                                                LayoutInflater inflater =getLayoutInflater();
                                                //Inflate the view from a predefined XML layout
                                                final View layout = inflater.inflate(R.layout.cliente_entregador_info,
                                                        (ViewGroup) findViewById(R.id.mainlayout));
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
                                                    ((TextView)layout.findViewById(R.id.infoname)).setText(infs[0]);
                                                    ((TextView)layout.findViewById(R.id.infotel)).setText(infs[1]);
                                                    ((TextView)layout.findViewById(R.id.infodata)).setText("Data: "+ entreg.dataa);
                                                    ((TextView)layout.findViewById(R.id.horater2)).setText("Pedido iniciado às: "+entreg.starthora);
                                                    ((TextView)layout.findViewById(R.id.horaini)).setText("Pedido Finalizado às: " +(infs[3].equals("")?"--:--": infs[3].replace("-",":")));
                                                    ((TextView)layout.findViewById(R.id.infostatus)).setText(entreg.status[entreg.statusid]);
                                                    ((TextView)layout.findViewById(R.id.idview)).setText("#"+entreg.entregaid);
                                                    new DownloadImageTask2((BootstrapCircleThumbnail) layout.findViewById(R.id.infofoto))
                                                            .execute(loginPage.basesite+infs[2]);

                                                    BootstrapProgressBar progressBar= (BootstrapProgressBar)layout.findViewById(R.id.progbar2);
                                                    progressBar.setProgress(entreg.statusid+1);
                                                    if(entreg.statusid==0)progressBar.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                                                    else if(entreg.statusid<3)progressBar.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                                                    else progressBar.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                    int stID= entreg.statusid;
                                                    final BootstrapButton chegoentrega= layout.findViewById(R.id.saiuent);
                                                    final BootstrapButton finalizaPed= layout.findViewById(R.id.finalizaped);
                                                    if(entreg.statusid==1)
                                                    {
                                                        chegoentrega.setShowOutline(false);
                                                    }
                                                    else if(entreg.statusid>=2)
                                                    {
                                                        chegoentrega.setShowOutline(true);
                                                        chegoentrega.setText("Pedido Enviado");
                                                    }
                                                    if(entreg.statusid==3)
                                                    {
                                                        finalizaPed.setText("Pedido Finalizado");
                                                        finalizaPed.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                        finalizaPed.setShowOutline(true);
                                                    }
                                                    chegoentrega.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            if(!chegoentrega.isShowOutline()){
                                                            int netxstate= chegoentrega.isShowOutline()?1:2;
                                                            RequestParams requestParams= new RequestParams();
                                                            requestParams.add("servID","993");
                                                            requestParams.add("entreid",entreg.entregaid+"");
                                                            requestParams.add("state",netxstate+"");
                                                            utils.log("EAIII");
                                                            HttpUtils.postByUrl(basesite + "application.php", requestParams, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    String resp= new String(responseBody);
                                                                    if(resp.equals("OK"))
                                                                    {
                                                                        pw.dismiss();
                                                                        findViewById(R.id.navigation_dashboard2).callOnClick();
                                                                    }
                                                                    else  Toast.makeText(getBaseContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    Toast.makeText(getBaseContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                }
                                                            });
                                                        }}
                                                    });
                                                    finalizaPed.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(final View view) {
                                                            if(!finalizaPed.isShowOutline()){
                                                            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                            LayoutInflater lt = getLayoutInflater();
                                                            final View ratingalert=lt.inflate(R.layout.avaliesuacorrida, mainlayout,false);
                                                            builder.setView(ratingalert);

                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    RequestParams requestParams= new RequestParams();
                                                                    requestParams.add("servID","993");
                                                                    requestParams.add("entreid",entreg.entregaid+"");
                                                                    requestParams.add("nota",((RatingBar)ratingalert.findViewById(R.id.ratingBar)).getRating()+"");
                                                                    requestParams.add("state",((BootstrapButton)view).isShowOutline()?2+"": 3+"");
                                                                    utils.log("EAIII");
                                                                    HttpUtils.postByUrl(basesite + "application.php", requestParams, new AsyncHttpResponseHandler() {
                                                                        @Override
                                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                            String resp= new String(responseBody);
                                                                            if(resp.equals("OK"))
                                                                            {
                                                                                pw.dismiss();
                                                                                findViewById(R.id.navigation_dashboard2).callOnClick();
                                                                            }
                                                                            else  Toast.makeText(getBaseContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                        }

                                                                        @Override
                                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                            Toast.makeText(getBaseContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    utils.toast(view.getContext(),"Por favor preencha a avaliação para finalizar o pedido");
                                                                }
                                                            });
                                                            builder.create().show();


                                                        }}
                                                    });
                                                }
                                            else{
                                                    Toast.makeText(getBaseContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                Toast.makeText(view.getContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();
                                            }
                                        });




                                    }
                                        else{
                                            Toast.makeText(view.getContext(),"Este pedido não possui entregador ainda",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                            }
                            catch (Exception e)
                            {
                                try{
                                findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                                Toast.makeText(getBaseContext(),"Falha ao obter histórico  "+e.getMessage(),Toast.LENGTH_LONG).show();
                                utils.log(e);
                            }catch (Exception xe){

                            }
                        }}

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
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
                                            SharedPreferences.Editor editor= pm.edit();
                                            editor.clear();
                                            editor.commit();
                                            startActivity(new Intent(getBaseContext(),loginPage.class));

                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }}};

                                   AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Você tem certeza que deseja sair? Você não receberá mais notificações").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).create().show();

                             }
                    });
                    inHistory=false;
                    inMain=false;

                    v.findViewById(R.id.altsenha).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(view.getContext(),altersenha.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","0");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.contatSup).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(view.getContext(),contatarSuporte.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","1");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.avaliaapp).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(view.getContext(),AvaliarApp.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","1");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.ajuda).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"ajuda.html")));

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
        if(inMain){
            getdispo();
            handler.postDelayed(this,10000);

        }
        else if(inHistory){
            findViewById(R.id.navigation_dashboard2).callOnClick();
            handler.postDelayed(this,60000);


        }
        else{
            handler.postDelayed(this,10000);

        }
    }


    void getdispo()
    {
        RequestParams requestParams= new RequestParams();
        requestParams.add("servID","7783");
        requestParams.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp= new String(responseBody);
                try{
                    int response = Integer.parseInt(resp);
                    if(response>0) {
                        findViewById(R.id.solicitaalocado).setVisibility(View.VISIBLE);
                        findViewById(R.id.permitavulso).setVisibility(View.VISIBLE);
                        ((BootstrapButton)findViewById(R.id.solicita)).setEnabled(false);

                    }
                    else {
                        findViewById(R.id.solicitaalocado).setVisibility(View.INVISIBLE);
                        findViewById(R.id.permitavulso).setVisibility(View.INVISIBLE);
                        ((BootstrapButton)findViewById(R.id.solicita)).setEnabled(true);
                    }

                }
                catch (Exception e)
                {
                    utils.toast(getBaseContext(),"Falha ao obter dados do servidor");

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.toast(getBaseContext(),"Falha ao obter dados do servidor");
            }
        });

        RequestParams rp= new RequestParams();
        rp.add("servID","65");
        rp.add("id",myid);

        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    int a = Integer.parseInt(new String(responseBody));
                    ((TextView)findViewById(R.id.numsolicita)).setText(""+a);

                }
                catch (Exception e)
                {
                    //Toast.makeText(getBaseContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
            }
        });

        RequestParams req= new RequestParams();
        req.add("servID","19");
        req.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    entregadoresDisponiveis = Integer.parseInt(new String(responseBody));
                    ((TextView)findViewById(R.id.numsolicita2)).setText(entregadoresDisponiveis+"");

                }
                catch (Exception e)
                {
                    //Toast.makeText(getBaseContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getBaseContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();
            }
        });
    }

}


