package bikecycle.com.bikecycle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class esquepass extends AppCompatActivity {
    BootstrapButton cliente,entregador,entrar;
    int recoveType=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esquepass);
        try{getSupportActionBar().hide();}catch(Exception e) {}
        cliente= (BootstrapButton)findViewById(R.id.Cliente);
        entregador=findViewById(R.id.entregabutton);
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entregador.setShowOutline(true);
                cliente.setShowOutline(false);
                recoveType=0;
            }
        });
        entregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entregador.setShowOutline(false);
                cliente.setShowOutline(true);
                recoveType=1;

            }
        });

        findViewById(R.id.sendforgot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String emailforgor= ((EditText) findViewById(R.id.emailadessforg)).getText().toString();
               if(!emailforgor.equals("")) {


                   RequestParams rp = new RequestParams();
                   rp.add("servID","21");
                   rp.add("email",emailforgor);
                   rp.add("tipe",recoveType+"");

                   HttpUtils.postByUrl(loginPage.basesite+"application.php", rp, new JsonHttpResponseHandler() {
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                           view.setEnabled(true);
                           Log.d("eai", "onSuccess: EAIIIIIIIIII");
                           String Result = response.toString();
                           Log.d("asd", " A resposta é  : " + Result +"  "+Result.equals("OK"));
                           if(Result.equals("OK"))
                           {
                               Log.d("asd", "onSuccess: SUCESOO");
                           }
                           else{
                               Log.d("asd", "DEU ERROOOOOW : " + response);

                               Toast.makeText(getBaseContext(),"Esse email não pertence a nenhuma conta registrada",Toast.LENGTH_LONG).show();
                           }

                       }



                       @Override
                       public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                           view.setEnabled(true);
                           Log.d("eai", "onSuccess: EAIIIIIIIIII");
                           String Result = responseString.toString();
                           Log.d("asd", " A resposta é  : " + Result +"  "+Result.equals("OK"));
                           if(Result.equals("OK"))
                           {
                               Toast.makeText(getBaseContext(),"Sucesso ao enviar o email, olhe sua caixa de mensagem",Toast.LENGTH_LONG).show();
                           }
                           else{
                               Log.d("asd", "DEU ERROOOOOW : " + responseString);

                               Toast.makeText(getBaseContext(),"Esse email não pertence a nenhuma conta registrada",Toast.LENGTH_LONG).show();
                           }

                       }
                   });
                   view.setEnabled(false);

               }
               else Toast.makeText(view.getContext(),"Por favor preencha o email corretamente",Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.voltar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),loginPage.class));
            }
        });
    }




}
