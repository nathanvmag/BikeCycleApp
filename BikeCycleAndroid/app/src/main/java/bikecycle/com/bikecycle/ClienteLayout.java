package bikecycle.com.bikecycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.icu.util.DateInterval;
import android.icu.util.TimeUnit;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.common.api.Api;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;
import static java.lang.Integer.parseInt;

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
                    ((TextView)findViewById(R.id.benvindclient)).setText("Bem-vindo, "+nome);
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
                                                            Toast.makeText(getApplicationContext(),"Sucesso ao realizar pedido, aguarde por entregadores alocados ",Toast.LENGTH_LONG).show();
                                                        else {
                                                            utils.toast(getApplicationContext(),"No momento não há entregadores disponíveis, aguarde um momento que seu pedido será aceito");
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


                                                                    utils.noInternetLog(getApplicationContext(),view);
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador alocado "+resp,Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    view.setEnabled(true);
                                                    utils.noInternetLog(getApplicationContext(),view);

                                                    // Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador" +new String(responseBody),Toast.LENGTH_LONG).show();
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
                                                    utils.log(resp);
                                                    if(resp.equals("OK"))
                                                    {
                                                        findViewById(R.id.navigation_dashboard2).callOnClick();
                                                        if(entregadoresDisponiveis>0)
                                                            Toast.makeText(getApplicationContext(),"Sucesso ao realizar pedido, aguarde por entregadores  ",Toast.LENGTH_LONG).show();
                                                        else {
                                                            utils.toast(getApplicationContext(),"No momento não há entregadores disponíveis, aguarde um momento que seu pedido será aceito");
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

                                                                    utils.noInternetLog(getApplicationContext(),view);

                                                                    //utils.log("Resultado do error  "+new String(responseBody)+" "+error);
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador "+resp,Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    view.setEnabled(true);
                                                    utils.noInternetLog(getApplicationContext(),view);

                                                    // Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador" +new String(responseBody),Toast.LENGTH_LONG).show();
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
                    findViewById(R.id.solicitaalfa).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            RequestParams rp = new RequestParams();
                                            rp.add("servID","78");
                                            rp.add("id",myid);
                                            view.setEnabled(false);
                                            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    view.setEnabled(true);
                                                    String resp =new String(responseBody);
                                                    utils.log(resp);
                                                    if(resp.equals("OK"))
                                                    {
                                                        findViewById(R.id.navigation_dashboard2).callOnClick();
                                                        if(entregadoresDisponiveis>0)
                                                            Toast.makeText(getApplicationContext(),"Sucesso ao realizar pedido, aguarde por entregadores  ",Toast.LENGTH_LONG).show();
                                                        else {
                                                            utils.toast(getApplicationContext(),"No momento não há entregadores disponíveis, aguarde um momento que seu pedido será aceito");
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

                                                                    utils.noInternetLog(getApplicationContext(),view);

                                                                    //utils.log("Resultado do error  "+new String(responseBody)+" "+error);
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador "+resp,Toast.LENGTH_LONG).show();

                                                    }
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                    view.setEnabled(true);
                                                    utils.noInternetLog(getApplicationContext(),view);

                                                    // Toast.makeText(getApplicationContext(),"Falha ao solicitar entregador" +new String(responseBody),Toast.LENGTH_LONG).show();
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

                    //check30min(mainlayout);
                    //checksolicitacoes();

                    //((ImageView)findViewById(R.id.clientlogo)).setImageDrawable(clientlogo==null?getResources().getDrawable(R.drawable.logo):clientlogo);
                    return true;
                case R.id.navigation_dashboard2:

                    getLayoutInflater().inflate(R.layout.cliente_history,mainlayout,true);
                    final ListView lv= (ListView)findViewById(R.id.entregalist);
                    final List<Entrega> ents= new ArrayList<>();
                    inHistory=true;
                    //checksolicitacoes();


                            RequestParams rp= new RequestParams();
                    rp.add("servID","87");
                    rp.add("id",myid);
                    findViewById(R.id.openhistory).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"historico.html")));

                        }
                    });
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
                                    Entrega e = new Entrega(infs[2],infs[4],infs[5], parseInt(infs[3]), parseInt(infs[0]),0,infs[6]);
                                    e.clienteID= parseInt( infs[7]);
                                    ents.add(e);
                                    tx.setVisibility(View.INVISIBLE);

                                }
                                utils.log("SAIU  LOOP ");

                                entregaAdpter adapter =
                                        new entregaAdpter(ents, getApplicationContext());
                                lv.setAdapter(adapter);

                                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                    @Override
                                    public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                                        final Entrega entrega= (Entrega)adapterView.getItemAtPosition(i);
                                        if(entrega.statusid==0||entrega.statusid==1)
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:
                                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                                                            builder2.setTitle("Por favor escolha o motivo do cancelamento");

                                                            final View pickerview = getLayoutInflater().inflate( R.layout.cancelamentopicker, null);
                                                            builder2.setView(pickerview);
                                                            ((RadioButton)pickerview.findViewById(R.id.radioButton5)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                    pickerview.findViewById(R.id.outromotivobox).setVisibility(isChecked?View.VISIBLE:View.INVISIBLE);
                                                                }
                                                            });

                                                            builder2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    String motivo="";
                                                                    if(((RadioButton) pickerview.findViewById(R.id.radioButton5)).isChecked())
                                                                    {
                                                                        motivo= ((EditText) pickerview.findViewById(R.id.outromotivobox)).getText().toString();
                                                                    }
                                                                    else
                                                                    {
                                                                        int radioButtonId = ((RadioGroup) pickerview.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
                                                                        motivo = ((RadioButton) pickerview.findViewById(radioButtonId)).getText().toString();

                                                                    }
                                                                     //String motivo ="input.getText().toString();";
                                                                     if(motivo.length()>=10)
                                                                     {
                                                                         RequestParams rp = new RequestParams();
                                                                         rp.add("servID","445");
                                                                         rp.add("entid",entrega.entregaid+"");
                                                                         rp.add("id",myid);
                                                                         rp.add("motivo",motivo);
                                                                         HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                                                                             @Override
                                                                             public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                                 String resp= new String(responseBody);
                                                                                 if(resp.equals("Cancelar pedido"))
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
                                                                     }else {
                                                                         utils.toast(view.getContext(),"Por favor digite ao menos 25 caracteres");
                                                                     }
                                                                }
                                                            });
                                                            builder2.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            builder2.show();



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
                                        else  if(entrega.statusid==2)
                                        {
                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which){
                                                        case DialogInterface.BUTTON_POSITIVE:

                                                            AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                                                            builder2.setTitle("Por favor escolha o motivo do cancelamento");

                                                            final View pickerview = getLayoutInflater().inflate( R.layout.cancelamentopicker, null);
                                                            builder2.setView(pickerview);
                                                            ((RadioButton)pickerview.findViewById(R.id.radioButton5)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                                @Override
                                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                    pickerview.findViewById(R.id.outromotivobox).setVisibility(isChecked?View.VISIBLE:View.INVISIBLE);
                                                                }
                                                            });


                                                            builder2.setPositiveButton("Cancelar pedido", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    String motivo="";
                                                                    if(((RadioButton) pickerview.findViewById(R.id.radioButton5)).isChecked())
                                                                    {
                                                                        motivo= ((EditText) pickerview.findViewById(R.id.outromotivobox)).getText().toString();
                                                                    }
                                                                    else
                                                                    {
                                                                        int radioButtonId = ((RadioGroup) pickerview.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
                                                                        motivo = ((RadioButton) pickerview.findViewById(radioButtonId)).getText().toString();

                                                                    }
                                                                    cancelaPedido(motivo,entrega.entregaid,view);

                                                                }
                                                            });
                                                            builder2.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });

                                                            builder2.show();



                                                            break;

                                                        case DialogInterface.BUTTON_NEGATIVE:
                                                            break;
                                                    }
                                                }
                                            };

                                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                            builder.setMessage("Você tem certeza que deseja cancelar esse pedido ? Este cancelamento será cobrado.").setPositiveButton("Sim", dialogClickListener)
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
                                        if(entreg.statusid==4)return;
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
                                                    ((TextView)layout.findViewById(R.id.infotel)).setText("Suporte WhatsApp:\n21 99962-2725");
                                                    ((TextView)layout.findViewById(R.id.infotel)).setVisibility(View.INVISIBLE);
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
                                                        public void onClick(final View view) {
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
                                                                    else  Toast.makeText(getApplicationContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                }

                                                                @Override
                                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                                                                    Toast.makeText(getApplicationContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                                    utils.noInternetLog(getApplicationContext(),view);

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
                                                                    requestParams.add("nota",((RatingBar)ratingalert.findViewById(R.id.rtbar)).getRating()+"");
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
                                                                            else  Toast.makeText(getApplicationContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                                        }

                                                                        @Override
                                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                           // Toast.makeText(getApplicationContext(),"Falha ao alterar status "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                                            utils.noInternetLog(getApplicationContext(),view);

                                                                        }
                                                                    });
                                                                }
                                                            });
                                                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    utils.toast(getApplicationContext(),"Por favor preencha a avaliação para finalizar o pedido");
                                                                }
                                                            });
                                                            builder.create().show();


                                                        }}
                                                    });
                                                    layout.findViewById(R.id.infclientefinal).setVisibility(entreg.clienteID==0?View.GONE:View.VISIBLE);
                                                    if(entreg.clienteID!=0){
                                                        layout.findViewById(R.id.infclientefinal).setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                utils.log("Logica cliente especial");
                                                                RequestParams req= new RequestParams();
                                                                utils.log("vamos ?");
                                                                req.add("servID", "89");
                                                                req.add("entreID", entreg.clienteID + "");
                                                                view.setEnabled(false);
                                                                HttpUtils.postByUrl(basesite + "application.php", req, new AsyncHttpResponseHandler() {
                                                                    @Override
                                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                                        view.setEnabled(true);
                                                                        final String res = new String(responseBody);
                                                                        utils.log("Resposta json" + res);
                                                                        try {
                                                                            JSONObject jsonObject = new JSONObject(res);
                                                                            LayoutInflater inflater = getLayoutInflater();
                                                                            //Inflate the view from a predefined XML layout
                                                                            View layout = inflater.inflate(R.layout.entregador_clientefinal_info,
                                                                                    (ViewGroup) findViewById(R.id.mainlayout), false);
                                                                            // create a 300px width and 470px height PopupWindow
                                                                            final PopupWindow pw = new PopupWindow(layout, LinearLayout.LayoutParams.MATCH_PARENT,
                                                                                    LinearLayout.LayoutParams.MATCH_PARENT, true);
                                                                            // display the popup in the center
                                                                            pw.showAtLocation(mainlayout, Gravity.CENTER, 0, 0);
                                                                            layout.findViewById(R.id.dimissinfo).setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {
                                                                                    pw.dismiss();
                                                                                }
                                                                            });

                                                                            Iterator<String> keys = jsonObject.keys();
                                                                            LinearLayout linearLayout= layout.findViewById(R.id.llayout);
                                                                            while(keys.hasNext()) {

                                                                                String key = keys.next();
                                                                                String value= jsonObject.getString(key);
                                                                                try {
                                                                                    TextView tx = linearLayout.findViewWithTag(key);
                                                                                    if(!value.isEmpty()) {

                                                                                        String myString = key.split("_")[1];
                                                                                        String upperString = myString.substring(0, 1).toUpperCase() + myString.substring(1).toLowerCase();
                                                                                        tx.setText(upperString + ": " + value);
                                                                                        utils.log(value);
                                                                                    }
                                                                                    else{
                                                                                        tx.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                                catch(Exception ex){
                                                                                    utils.log("Falhou tag "+key);
                                                                                }
                                                                            }
                                                                            layout.findViewById(R.id.reporterror).setVisibility(View.INVISIBLE);

                                                                            ((TextView) layout.findViewById(R.id.idview)).setText("#" + entreg.entregaid);
                                                                            ((TextView) layout.findViewById(R.id.infostatus)).setText(entreg.status[entreg.statusid]);

                                                                            BootstrapProgressBar progressBar = (BootstrapProgressBar) layout.findViewById(R.id.progbar2);
                                                                            progressBar.setProgress(entreg.statusid + 1);
                                                                            if (entreg.statusid == 0)
                                                                                progressBar.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                                                                            else if (entreg.statusid < 3)
                                                                                progressBar.setBootstrapBrand(DefaultBootstrapBrand.INFO);
                                                                            else
                                                                                progressBar.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
                                                                        } catch (JSONException err) {
                                                                            view.setEnabled(true);
                                                                            utils.noInternetLog(getApplicationContext(), view);
                                                                        }
                                                                    }



                                                                    @Override
                                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                                        view.setEnabled(true);
                                                                        utils.noInternetLog(getApplicationContext(), view);

                                                                        // Toast.makeText(getApplicationContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                                    }
                                                                });


                                                            }



                                                        });

                                                    }

                                                }

                                            else{
                                                    Toast.makeText(getApplicationContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();

                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            //    Toast.makeText(getApplicationContext(),"Falha ao abrir informações "+new String(responseBody),Toast.LENGTH_LONG).show();
                                                utils.noInternetLog(getApplicationContext(),view);

                                            }
                                        });




                                    }
                                        else{
                                            Toast.makeText(getApplicationContext(),"Este pedido não possui entregador ainda",Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                            }
                            catch (Exception e)
                            {
                                try{
                                findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(),"Falha ao obter histórico  "+e.getMessage(),Toast.LENGTH_LONG).show();
                                utils.log(e);
                            }catch (Exception xe){

                            }
                        }}

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                           // Toast.makeText(getApplicationContext(),"Falha ao obter histórico  "+new String(responseBody),Toast.LENGTH_LONG).show();
                            utils.noInternetLog(getApplicationContext(),mainlayout);

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
                    inHistory=false;
                    inMain=false;

                    v.findViewById(R.id.altsenha).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),altersenha.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","0");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.contatSup).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),contatarSuporte.class);
                            intent.putExtra("id",myid);
                            intent.putExtra("tipe","1");
                            startActivity(intent);
                        }
                    });
                    findViewById(R.id.avaliaapp).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent= new Intent(getApplicationContext(),AvaliarApp.class);
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
                                  //  Toast.makeText(getApplicationContext(),"Falha ao alterar cadastro "+resp,Toast.LENGTH_LONG).show();
                                    utils.noInternetLog(getApplicationContext(),view);

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
    protected void onResume() {
        super.onResume();

    }
    void cancelaPedido(String motivo, int entregaid, final View view)
    {

        if(motivo.length()>20)
        {
            RequestParams rp = new RequestParams();
            rp.add("servID","445");
            rp.add("entid",entregaid+"");
            rp.add("id",myid);
            rp.add("motivo",motivo);
            rp.add("cobrada","1");
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
        }else {
            utils.toast(view.getContext(),"Por favor digite ao menos 20 caracteres");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_layout);
        mainlayout= findViewById(R.id.clientelayoutmain);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

       /* Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)

            {
                utils.toast(getApplicationContext(),"deu erro "+ e);
                utils.log(e);
            }
        });*/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation2);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
    Boolean solicitando=false;
    void checksolicitacoes()
    {
        final RequestParams requestParams= new RequestParams();
        requestParams.add("servID","573");
        requestParams.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String resp= new String(responseBody);
                utils.log("LOG DO solicita "+resp);
                if(!resp.equals("")){
                    try{
                        if(!solicitando) {
                            final String[] info = resp.split("%")[0].split("!");
                            solicitando=true;
                            utils.log("AQUI TEMMM");
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            RequestParams rps= new RequestParams();
                                            rps.add("servID","993");
                                            rps.add("entreid",info[1]);
                                            rps.add("state",info[5]);
                                            rps.add("nota",5+"");
                                            solicitando=false;
                                            HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                    RequestParams rps= new RequestParams();
                                                    rps.add("servID","447");
                                                    rps.add("id",info[0]);
                                                    solicitando=false;

                                                    HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {

                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            utils.toast(getBaseContext(),"Sucesso ao alterar status de entrega");
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                }
                                            });

                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            rps= new RequestParams();
                                            rps.add("servID","447");
                                            rps.add("id",info[0]);
                                            solicitando=false;

                                            HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {

                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                }

                                                @Override
                                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                                }
                                            });
                                            break;
                                            default:
                                                solicitando=false;

                                                break;
                                    }}};

                            AlertDialog.Builder builder = new AlertDialog.Builder(ClienteLayout.this);

                            String[] histStatus= new String[]{"Procurando Entregador","Aceito, aguarde o entregador","Enviado para entrega","Pedido finalizado"};

                            builder.setMessage("O entregador "+ info[6] +" solicitou a troca de status da entrega nº "+ info[1]+ " \nDe: "+histStatus[parseInt(info[4])]+" \nPara: "+ histStatus[parseInt(info[5])]+"\nVocê confirma ?").setPositiveButton("Sim", dialogClickListener)
                                    .setNegativeButton("Não", dialogClickListener).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    solicitando=false;
                                    utils.log("Cancelado");

                                }
                            }).create().show();

                        }
                }catch (Exception ex){}
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                // utils.toast(getApplicationContext(),"Falha ao obter dados do servidor");
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }


    void getdispo()
    {
       // checksolicitacoes();

        RequestParams requestParams= new RequestParams();
        requestParams.add("servID","7783");
        requestParams.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
               //


                String resp= new String(responseBody);
                try{
                    int response = parseInt(resp);
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
                    checkAceitartermos();

                }
                catch (Exception e)
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

        RequestParams rp= new RequestParams();
        rp.add("servID","65");
        rp.add("id",myid);

        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    int a = parseInt(new String(responseBody));
                    ((TextView)findViewById(R.id.numsolicita)).setText(""+a);

                }
                catch (Exception e)
                {
                    //Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                // Toast.makeText(getApplicationContext(),"Falha ao obter pedidos ativos "+new String(responseBody),Toast.LENGTH_LONG).show();
            }
        });

        RequestParams req= new RequestParams();
        req.add("servID","19");
        req.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    entregadoresDisponiveis = parseInt(new String(responseBody));
                    ((TextView)findViewById(R.id.numsolicita2)).setText(entregadoresDisponiveis+"");

                }
                catch (Exception e)
                {
                    //Toast.makeText(getApplicationContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                //Toast.makeText(getApplicationContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();
            }
        });
        req= new RequestParams();
        req.add("servID","20");
        req.add("id",myid);
        HttpUtils.postByUrl(basesite + "application.php", req, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try{
                    int v = parseInt(new String(responseBody));
                    findViewById(R.id.solicitaalfa).setVisibility(v==0?View.INVISIBLE:View.VISIBLE);

                }
                catch (Exception e)
                {
                    //Toast.makeText(getApplicationContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                utils.noInternetLog(getApplicationContext(),mainlayout);

                //Toast.makeText(getApplicationContext(),"Falha ao obter entregadores disponíveis",Toast.LENGTH_LONG).show();
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
                popupWindow.dismiss();
                editor.putString("aceitatermos","ah");
                editor.commit();
                editor.apply();
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
        void check30min(final View v )
        {
            RequestParams rp= new RequestParams();
            rp.add("servID","87");
            rp.add("id",myid);
            HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String resp = new String(responseBody);
                    utils.log(resp);
                    final TextView tx=(TextView)findViewById(R.id.textView2);
                    //utils.noInternetLog(getApplicationContext(),mainlayout);

                    try {

                        String[] modules= resp.split("%");
                        for(int i=0;i<modules.length;i++)
                        {
                            String[] infs= modules[i].split("!");
                            final Entrega e = new Entrega(infs[2],infs[4],infs[5], parseInt(infs[3]), parseInt(infs[0]),0,infs[6]);
                            utils.log("Hora de inicio "+ e.starthora);
                            if(e.statusid!=3&&e.statusid!=0&&e.statusid!=4)
                            {
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                                Date date = sdf.parse(e.starthora);
                                Date date2 = new Date();
                                String now= sdf.format(date2);
                                utils.log("hora atual "+now);
                                int atualminutes = parseInt( now.split(":")[0])*60+ parseInt( now.split(":")[1]);
                                int iniciominutes= parseInt( e.starthora.split(":")[0])*60+ parseInt( e.starthora.split(":")[1]);
                                int result = atualminutes-iniciominutes;
                                utils.log("Minutos resultantes "+result);
                                if(result>=30)
                                {
                                    utils.log("Entrea aqui");

                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    utils.log("Finaliza");
                                                    RequestParams rp= new RequestParams();
                                                    rp.add("servID","9732");
                                                    rp.add("id",e.entregaid+"");
                                                    HttpUtils.postByUrl(basesite + "server.php", rp, new AsyncHttpResponseHandler() {
                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                            String resp = new String(responseBody);
                                                            utils.log(resp);
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                            utils.noInternetLog(getApplicationContext(),mainlayout);

                                                            // utils.toast(getApplicationContext(),"Falha ao finalizar pedido");
                                                        }
                                                    });
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }}};

                                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                    builder.setMessage("Há mais de 30 minutos que a entrega número: "+e.entregaid +" foi realizada. \nDeseja finalizar esta entrega?").setPositiveButton("Finalizar", dialogClickListener)
                                            .setNegativeButton("Não", dialogClickListener).create().show();
                                }
                            }
                        }}catch (Exception e){}
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    utils.noInternetLog(getApplicationContext(),mainlayout);

                }
            });
        }

    }






