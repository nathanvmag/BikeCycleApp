package bikecycle.com.bikecycle;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

import static bikecycle.com.bikecycle.loginPage.basesite;

public class contatarSuporte extends AppCompatActivity {
    String myid,tipe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatar_suporte);
        if (getIntent().getExtras()!=null)
        {
            Bundle extras = getIntent().getExtras();
            myid= extras.getString("id");
            tipe= extras.getString("tipe");
            findViewById(R.id.envia).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestParams rps = new RequestParams();
                    rps.add("servID","887");
                    rps.add("id",myid+"");
                    rps.add("title","Contato via APP");
                    rps.add("mess",((BootstrapEditText)findViewById(R.id.reclamabody)).getText().toString());
                    rps.add("from",tipe+"");
                    utils.log("HEy "+myid+" meu tipo "+tipe);
                    HttpUtils.postByUrl(basesite + "application.php", rps, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res= new String(responseBody);
                            if (res.equals("OK"))
                            {
                                utils.toast(getBaseContext(),"Sucesso ao Enviar mensagem ");

                            }
                            else{
                                utils.toast(getBaseContext(),"Falha ao Enviar mensagem "+ new String(responseBody));

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            utils.toast(getBaseContext(),"Falha ao Enviar mensagem "+ new String(responseBody));
                        }
                    });
                }
            });
            findViewById(R.id.wpp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=5521999622725&text=Digite%20sua%20Mensagem")));

                }
            });
            findViewById(R.id.fb).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/bikescycle")));

                }
            });




        }
    }
}
