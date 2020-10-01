package bikecycle.com.bikecycle;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class EntregadorLayout extends AppCompatActivity implements  Runnable
{

   // private TextView mTextMessage;
    private RelativeLayout mainlayout,displayout,indisplayout,loading;
    private String myid,myfoto,nome;
    BootstrapButton Aceitar,aceitaalfabt;
    private Handler handler;
    private int maxPedidos=3;
    int oldwhidt,oldheight;
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
                    //findViewById(R.id.solicitaalfa).setVisibility(View.INVISIBLE);
                    Aceitar= findViewById(R.id.aceitarent);

                    Aceitar.setEnabled(false);
                    aceitaalfabt= findViewById(R.id.aceitaalfa);
                    aceitaalfabt.setEnabled(false);
                    aceitaalfabt.setVisibility(View.INVISIBLE);
                    oldheight= aceitaalfabt.getHeight();
                    oldwhidt= aceitaalfabt.getWidth();
                    ( (TextView)findViewById(R.id.bnvd)).setText("Olá, "+nome+".");
                    new DownloadImageTask2((BootstrapCircleThumbnail)findViewById(R.id.entregafoto)).execute(basesite+myfoto.trim());

                    getState((BootstrapButton)findViewById(R.id.imworking));
                    getdispo();

                    ((BootstrapButton)findViewById(R.id.imworking)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BootstrapButton bt = (BootstrapButton)view;
                            setWorkstate(bt.isShowOutline()?1:0,bt);
                        }
                    });
                    checkTrab();
                    RequestParams ques2= new RequestParams();
                    ques2.add("servID","9734");
                    ques2.add("id",myid);
                    HttpUtils.postByUrl(basesite + "server.php", ques2, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String resp = new String(responseBody);
                            utils.log("meus max pedido "+(resp));
                            checkAceitartermos();

                            maxPedidos= Integer.parseInt((resp));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            utils.noInternetLog(getApplicationContext(),mainlayout);

                            //utils.toast(getApplicationContext(),"Falha ao obter maximos pedidos");
                        }
                    });

                    Aceitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            RequestParams ques= new RequestParams();
                                            ques.add("servID","7740");
                                            ques.add("id",myid);
                                            HttpUtils.postByUrl(basesite + "application.php", ques, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    String resp = new String(responseBody);
                                                    try {
                                                        int rps = Integer.parseInt(resp);
                                                        RequestParams rp = new RequestParams();
                                                        if (rps > 0) {
                                                            rp.add("servID", "743");

                                                        } else {
                                                            rp.add("servID", "742");
                                                        }
                                                            rp.add("id", myid);
                                                            view.setEnabled(false);
                                                            if (PedidosAtivos < maxPedidos) {

                                                                HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                                                    @Override
                                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                        String resp = new String(responseBody);
                                                                        utils.log(resp);

                                                                        view.setEnabled(true);
                                                                        if (resp.equals("OK")) {
                                                                            findViewById(R.id.navigation_dashboard).callOnClick();
                                                                            utils.log("Sucesso ao receber o pedido, cliquei nele para mais informações");
                                                                        } else {
                                                                            utils.toast(getApplicationContext(), "Falha ao aceitar pedido " + resp);

                                                                        }

                                                                    }

                                                                    @Override
                                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                        view.setEnabled(false);
                                                                        String resp = new String(responseBody);
                                                                        utils.log(resp);
                                                                        utils.noInternetLog(getApplicationContext(),view);

                                                                        //utils.toast(getApplicationContext(), "Falha ao aceitar pedido " + resp);

                                                                    }
                                                                });
                                                            } else {
                                                                utils.toast(getApplicationContext(), "Você já possui 3 pedidos em andamento, finalize-os primeiro");
                                                            }

                                                    } catch (Exception e) {
                                                        utils.toast(getApplicationContext(),"Falha ao obter dados do servidor");

                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    utils.noInternetLog(getApplicationContext(),view);

                                                }});

                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Tem certeza que deseja aceitar ?").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).show();


                        }
                    });
                    findViewById(R.id.aceitaalfa).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                                        view.setEnabled(false);
                                                        if (PedidosAtivos < maxPedidos) {
                                                            RequestParams rp = new RequestParams();
                                                            rp.add("id", myid);
                                                            rp.add("servID","744");

                                                            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                    String resp = new String(responseBody);
                                                                    utils.log(resp);

                                                                    view.setEnabled(true);
                                                                    if (resp.equals("OK")) {
                                                                        findViewById(R.id.navigation_dashboard).callOnClick();
                                                                        utils.log("Sucesso ao receber o pedido, cliquei nele para mais informações");
                                                                    } else {
                                                                        utils.toast(getApplicationContext(), "Falha ao aceitar pedido " + resp);

                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                    view.setEnabled(false);
                                                                    String resp = new String(responseBody);
                                                                    utils.log(resp);
                                                                    utils.noInternetLog(getApplicationContext(),view);

                                                                    //utils.toast(getApplicationContext(), "Falha ao aceitar pedido " + resp);

                                                                }
                                                            });
                                                        } else {
                                                            utils.toast(getApplicationContext(), "Você já possui "+maxPedidos+" pedidos em andamento, finalize-os primeiro");
                                                        }


                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setMessage("Tem certeza que deseja aceitar ?").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).show();


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
                    findViewById(R.id.openhistory).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"entregador.html")));

                        }
                    });
                    HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String resp = new String(responseBody);
                            utils.log("resposta "+resp);
                            try {
                                String[] modules= resp.split("%");
                                for(int i=0;i<((modules.length>15)?15:modules.length);i++)
                                {
                                    String[] infs= modules[i].split("!");

                                    Entrega e = new Entrega(infs[1],infs[4],infs[5],Integer.parseInt(infs[3]),Integer.parseInt(infs[0]),1,infs[6]);
                                    if(e.statusid==4)continue;
                                    entregas.add(e);
                                    findViewById(R.id.textView2).setVisibility(View.INVISIBLE);

                                }

                                entregaAdpter adapter =
                                        new entregaAdpter(entregas, getApplicationContext());
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
                                                            chegoentrega.setEnabled(false);
                                                        }
                                                        if(entreg.statusid==3)
                                                        {
                                                            finalizaPed.setText("Pedido Finalizado");
                                                            finalizaPed.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                            finalizaPed.setShowOutline(true);
                                                            finalizaPed.setEnabled(false);
                                                        }
                                                        chegoentrega.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                               // changest(2,entreg.entregaid,entreg.empresaID,entreg.statusid,2);
                                                               // pw.dismiss();

                                                                findViewById(R.id.navigation_dashboard).callOnClick();
                                                            }
                                                        });
                                                        finalizaPed.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                //changest(3,entreg.entregaid,entreg.empresaID,entreg.statusid,3);
                                                                //pw.dismiss();

                                                                //findViewById(R.id.navigation_dashboard).callOnClick();

                                                            }
                                                        });
                                                        if(entreg.statusid==1||entreg.statusid==2)
                                                        {
                                                            layout.findViewById(R.id.reporterror).setVisibility(View.VISIBLE);
                                                            layout.findViewById(R.id.reporterror).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(final View view) {
                                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                                                    LayoutInflater lt = getLayoutInflater();
                                                                    final View ratingalert=lt.inflate(R.layout.reporterror, mainlayout,false);
                                                                    builder.setView(ratingalert);

                                                                    builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                            RequestParams rps = new RequestParams();
                                                                            rps.add("servID","887");
                                                                            rps.add("id",myid);
                                                                            rps.add("title","Reportar um problema -"+((BootstrapEditText)ratingalert.findViewById(R.id.reclamatitle)).getText().toString());
                                                                            rps.add("mess",((BootstrapEditText)ratingalert.findViewById(R.id.reclamabody)).getText().toString());
                                                                            rps.add("from","0");
                                                                            HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
                                                                                @Override
                                                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                    String res= new String(responseBody);
                                                                                    if (res.equals("OK"))
                                                                                    {
                                                                                        utils.toast(getApplicationContext(),"Sucesso ao reportar o problema ");

                                                                                    }
                                                                                    else{
                                                                                        utils.toast(getApplicationContext(),"Falha ao reportar o problema "+ new String(responseBody));

                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                                    utils.noInternetLog(getApplicationContext(),view);

                                                                                    //utils.toast(getApplicationContext(),"Falha ao reportar o problema "+ new String(responseBody));
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                        }
                                                                    });
                                                                    builder.setTitle("Reportar um problema");
                                                                    builder.create().show();

                                                                }
                                                            });
                                                        }
                                                        new DownloadImageTask2((BootstrapCircleThumbnail) layout.findViewById(R.id.infofoto))
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
                                                    utils.noInternetLog(getApplicationContext(),view);

                                                    // Toast.makeText(getApplicationContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                }
                                            });

                                };

                        });}
                        catch (Exception e)
                            {
                                findViewById(R.id.textView2).setVisibility(View.VISIBLE);

                                Toast.makeText(getApplicationContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                       try{     findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                            utils.log("resposta "+new String(responseBody));
                           utils.noInternetLog(getApplicationContext(),mainlayout);

                            //Toast.makeText(getApplicationContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();

                        }catch (Exception e){}}

                    } );


                    return true;
                case R.id.navigation_notifications:
                    inMain=false;
                    inHistory=false;

                    getLayoutInflater().inflate(R.layout.entrega_configs,mainlayout,true);
                    findViewById(R.id.saibot).setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                utils.log("sair");
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                sendRegistrationToServer("");

                                                SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
                                                SharedPreferences.Editor editor= pm.edit();
                                                editor.clear();
                                                editor.commit();
                                                startActivity(new Intent(getApplicationContext(),loginPage.class));

                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }}};

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Você tem certeza que deseja sair? Você não receberá mais notificações").setPositiveButton("Sim", dialogClickListener)
                                        .setNegativeButton("Não", dialogClickListener).create().show();

                            }

                        });

                    findViewById(R.id.altsenha).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),altersenha.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","1");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.contatSup).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),contatarSuporte.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","0");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.avaliaapp).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),AvaliarApp.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","0");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.ajuda).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"ajuda.html")));

                        }
                    });
                    findViewById(R.id.sobre).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(),about.class ));

                        }
                    });
                    findViewById(R.id.altcad).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
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
                                    utils.noInternetLog(getApplicationContext(),view);

                                    // Toast.makeText(getApplicationContext(),"Falha ao alterar cadastro "+resp,Toast.LENGTH_LONG).show();
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
    void changest(int st ,int entrid,String clientid,int oldst,int nst)
    {
        RequestParams rp= new RequestParams();
        rp.add("servID","574");
        rp.add("entregaID",entrid+"");
        rp.add("clientid",clientid+"");
        rp.add("oldst",oldst+"");
        rp.add("nst",nst+"");
        rp.add("entregadorid",myid+"");

        rp.add("state",st+"");
        HttpUtils.postByUrl(basesite+"application.php", rp, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                utils.toast(getApplicationContext(),"Sucesso ao mudar status da entrega, aguarde o cliente confirmar.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregador_layout);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)

            {
                utils.toast(getApplicationContext(),"deu erro "+ e);
            }
        });
        loading= findViewById(R.id.loadinglogin);
        handler= new Handler();
        handler.postDelayed(this,2000);
        //mTextMessage = (TextView) findViewById(R.id.message);
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
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String deviceToken = instanceIdResult.getToken();
                    // Do whatever you want with your token now
                    // i.e. store it on SharedPreferences or DB
                    // or directly send it to server
                    sendRegistrationToServer(deviceToken);
                }
            });
        }
        findViewById(R.id.navigation_home).callOnClick();
    }
    void loadingpage(boolean t)
    {
        loading.setVisibility(t?View.VISIBLE:View.INVISIBLE);
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
                    Toast.makeText(getApplicationContext(),"Falha ao alterar estado"+new String(responseBody),Toast.LENGTH_LONG).show();
                else {
                    if(bt.isShowOutline())
                    {
                        bt.setBootstrapBrand(DefaultBootstrapBrand.INFO);
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
                utils.noInternetLog(getApplicationContext(),mainlayout);

                //  Toast.makeText(getApplicationContext(),"Falha ao alterar estado"+new String(responseBody),Toast.LENGTH_LONG).show();
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
                        bt.setBootstrapBrand(DefaultBootstrapBrand.INFO);
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
                    Toast.makeText(getApplicationContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                    bt.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
                    bt.setShowOutline(true);
                    bt.setText("Indisponível");

                    displayout.setVisibility(View.INVISIBLE);
                    indisplayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Toast.makeText(getApplicationContext(),"Falha ao status "+new String(responseBody),Toast.LENGTH_LONG).show();
                utils.noInternetLog(getApplicationContext(),mainlayout);

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
        RequestParams ques= new RequestParams();
        ques.add("servID","7740");
        ques.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", ques, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String resp = new String(responseBody);

                try{
                    int rps= Integer.parseInt(resp);
                    if(rps>0)
                    {
                        RequestParams rp= new RequestParams();
                        rp.add("servID","55");
                        rp.add("id",myid);
                      //  loadingpage(true);

                        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try{
                                    int a = Integer.parseInt(new String(responseBody));
                                    ((TextView)findViewById(R.id.dispotext)).setText(a+"");
                                    if(a>0)Aceitar.setEnabled(true);
                                    else Aceitar.setEnabled(false);

                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                utils.noInternetLog(getApplicationContext(),mainlayout);

                                //Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else{
                        RequestParams rp= new RequestParams();
                        rp.add("servID","54");
                        rp.add("id",myid);
                        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try{
                                    int a = Integer.parseInt(new String(responseBody));
                                    Log.d("entre", a+"  entregas dispo");
                                    ((TextView)findViewById(R.id.dispotext)).setText(a+"");
                                    if(a>0)Aceitar.setEnabled(true);
                                    else Aceitar.setEnabled(false);

                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                utils.noInternetLog(getApplicationContext(),mainlayout);

                                //   Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
                            }
                        });
                        rp= new RequestParams();
                        rp.add("servID","776");
                        rp.add("id",myid);
                       // utils.log(findViewById(R.id.aceitaalfa).getVisibility());

                        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try{
                                    int a = Integer.parseInt(new String(responseBody));
                                    utils.log("Valor de a "+a);

                                    utils.log(findViewById(R.id.aceitaalfa).getVisibility());
                                 //   findViewById(R.id.aceitaalfa).setVisibility(View.GONE);
                                 // findViewById(R.id.aceitaalfa).setVisibility(a==1? View.VISIBLE:View.INVISIBLE);
                                if(a==1){
                                    aceitaalfabt.setVisibility(View.VISIBLE);
                                    RequestParams rp= new RequestParams();
                                    rp.add("servID","56");
                                    rp.add("id",myid);
                                    HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            try{
                                                int a = Integer.parseInt(new String(responseBody));

                                                findViewById(R.id.aceitaalfa).setEnabled(a>0);
                                                //findViewById(R.id.aceitaalfa).setVisibility(a>0?View.VISIBLE:View.INVISIBLE);

                                                ((BootstrapButton)findViewById(R.id.aceitaalfa)).setText(a+ " entregas alfas disponíveis,\n Aceitar?");

                                            }
                                            catch (Exception e)
                                            {
                                                Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            utils.noInternetLog(getApplicationContext(),mainlayout);

                                            //   Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
                                        }
                                    });

                                }
                                else{

                                    aceitaalfabt.setVisibility(View.INVISIBLE);
                                   // aceitaalfabt.setWidth(0);
                                    //aceitaalfabt.setHeight(0);
                                    utils.log("veio aqui");
                                }
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                utils.noInternetLog(getApplicationContext(),mainlayout);

                                //   Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
                            }
                        });



                    }
                }
                catch(Exception e)
                {
                    utils.toast(getApplicationContext(),"Falha ao obter dados do servidor");

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                // utils.toast(getApplicationContext(),"Falha ao obter dados do servidor");
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
                utils.noInternetLog(getApplicationContext(),mainlayout);

            }
        });
    }
    void checkTrab()
    {
        RequestParams rp = new RequestParams();
        rp.add("servID","749");
        rp.add("id",myid);

        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                utils.log("Resultado do check trab "+new String(responseBody));
                Integer resu= Integer.parseInt(new String(responseBody));
                if(resu==0)
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                   // utils.log("Ok positivo");
                                   serv(1);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    serv(0);
                                    BootstrapButton bt= ((BootstrapButton)findViewById(R.id.imworking));
                                    if(!bt.isShowOutline())bt.callOnClick();

                                    break;
                                default:
                                    BootstrapButton bt2= ((BootstrapButton)findViewById(R.id.imworking));

                                    if(!bt2.isShowOutline())bt2.callOnClick();
                                    break;
                            }
                        }
                    };
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
                    Date data = Calendar.getInstance().getTime();
                    String str = fmt.format(data);
                    AlertDialog.Builder builder = new AlertDialog.Builder(EntregadorLayout.this);
                    builder.setMessage("Você confirma a sua presença hoje, dia: "+str +" Caso sua resposta seja não outro entregador será convidado.").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).setCancelable(false).
                            show();
                }

                else utils.log("Confirmou trabalho");


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                // utils.toast(getApplicationContext(),"Erro ao obter confirmação "+new String(responseBody));
                utils.log("Erro ao obter confirmação  pelo erro "+new String(responseBody));

            }
        });

    }
    void serv(int id)
    {
        RequestParams rps= new RequestParams();
        rps.add("servID","669");
        rps.add("id",myid);
        rps.add("serv",id+"");
        HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                utils.toast(getApplicationContext(),"Sucesso ao confirmar ");
                utils.log("confirmar "+new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                // utils.toast(getApplicationContext(),"Falha ao confirmar trabalho "+ new String(responseBody));
            }
        });
    }



    private void sendRegistrationToServer(String token) {
        utils.log("minha toke "+token);
        RequestParams rp = new RequestParams();
        rp.add("servID","333");
        rp.add("token",token);
        rp.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                utils.log("Sucesso com a token nova "+new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                //utils.log("Falha com a token nova"+new String(responseBody));
            }
        });
    }
    void checkAceitartermos()
    { SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
        final SharedPreferences.Editor editor= pm.edit();
        if(!pm.contains("aceitatermos")){
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.aceita_termos, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.MATCH_PARENT;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window tolken
            popupWindow.showAtLocation(mainlayout, Gravity.CENTER, 0, 0);
            popupView.findViewById(R.id.aceitatermostodos).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    popupWindow.dismiss();
                                    editor.putString("aceitatermos","ah");
                                    editor.commit();
                                    editor.apply();

                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }}};

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Ao aceitar você garante que leu e está ciente dos termos de uso de política de privacidade.").setPositiveButton("Continuar", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).create().show();

                }
            });
            popupView.findViewById(R.id.bnvd7).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"politicaprivacidade.pdf")));

                }
            });
            popupView.findViewById(R.id.bnvd6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"termosusuario.pdf")));

                }
            });
        }
    }
}
