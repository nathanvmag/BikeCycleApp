package bikecycle.com.bikecycle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class loginPage extends AppCompatActivity {
    BootstrapButton cliente,entregador,entrar;
    RelativeLayout loading,login;
    EditText loginTX,passTX;
    //1 entregador 0 cliente
    public int LoginType=1;
    public static final String basesite="http://bikescycle.com/app/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pm = getSharedPreferences("pref",MODE_PRIVATE);
        setContentView(R.layout.activity_login_page);
        loading= findViewById(R.id.loadinglogin);
        login=findViewById(R.id.loginlay);
        loading.setVisibility(View.INVISIBLE);
        login.setVisibility(View.INVISIBLE);

        if(pm.contains("login")&&pm.contains("pass"))
        {        login.setVisibility(View.INVISIBLE);

            loading.setVisibility(View.VISIBLE);
            earlyLogin(pm.getString("login",""),pm.getString("pass",""),pm.getInt("tipe",0));
        }else{
        loading.setVisibility(View.INVISIBLE);
        login.setVisibility(View.VISIBLE);}

        TypefaceProvider.registerDefaultIconSets();
        cliente= (BootstrapButton)findViewById(R.id.Cliente);
        entregador=findViewById(R.id.entregabutton);
        loginTX=findViewById(R.id.emailadess);
        passTX=findViewById(R.id.pass);
        findViewById(R.id.ver_senha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BootstrapEditText)passTX).setInputType(((BootstrapEditText)passTX).getInputType()== InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD? InputType.TYPE_CLASS_TEXT:InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
            }
        });




        /// Handle de eventos
        findViewById(R.id.primeoacess).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"register.php")));
            }
        });
        findViewById(R.id.esquepass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(),esquepass.class));
            }
        });
        findViewById(R.id.ajuda).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(basesite+"ajuda.html")));

            }
        });
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entregador.setShowOutline(true);
                cliente.setShowOutline(false);
                LoginType=0;
            }
        });
        entregador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entregador.setShowOutline(false);
                cliente.setShowOutline(true);
                LoginType=1;

            }
        });
        findViewById(R.id.entrarbt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                doLogin(view);
            }
        });
        loginTX.requestFocus();

    }
    @Override
    //impede que o app volte para Activity anterior quando o botão voltar é pressionado
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
    void doLogin(final View view) {
        loading.setVisibility(View.VISIBLE);
        login.setVisibility(View.INVISIBLE);
        RequestParams rp = new RequestParams();
        rp.add("servID", "90");
        rp.add("login", loginTX.getText().toString());
        rp.add("pass", passTX.getText().toString());
        rp.add("tipe", LoginType + "");
        view.setEnabled(false);
        HttpUtils.postByUrl(basesite + "application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                view.setEnabled(true);

                loading.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);

                String resp = new String(responseBody);

                if (resp.equals("W")) {
                    Toast.makeText(getApplicationContext(), "Login ou Senha incorretos, Por favor tente novamente", Toast.LENGTH_LONG).show();

                } else if (resp.equals("NF")) {
                    Toast.makeText(getApplicationContext(), "Sua conta ainda não foi confirmada, aguarde mais um tempo, caso demore, contate o suporte", Toast.LENGTH_LONG).show();

                } else if (resp.split("!").length == 3) {
                    String[] ids = resp.split("!");
                    int tipe= LoginType;
                    Intent intent;
                    if(tipe==1) {
                        intent = new Intent(getApplicationContext(), EntregadorLayout.class);

                    }
                    else{
                        intent= new Intent(getApplicationContext(),ClienteLayout.class);
                    }
                    intent.putExtra("id", ids[0]);
                    intent.putExtra("nome", ids[1]);
                    intent.putExtra("foto", ids[2]);
                    intent.putExtra("login", loginTX.getText().toString());
                    intent.putExtra("pass", passTX.getText().toString());
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "Erro ao Logar " + new String(responseBody), Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                view.setEnabled(true);

                loading.setVisibility(View.INVISIBLE);
                login.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Erro ao Logar " + new String( responseBody), Toast.LENGTH_LONG).show();
            }
        });

    }void earlyLogin(final String user, final String pass, final int tipe)
    { RequestParams rp= new RequestParams();
        rp.add("servID","90");
        rp.add("login",user);
        rp.add("pass",pass);
        rp.add("tipe",tipe+"");
        HttpUtils.postByUrl(basesite+"application.php", rp, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String resp= new String(responseBody);

                if(resp.equals("W"))
                {
                    login.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(),"Login ou Senha incorretos, Por favor tente novamente",Toast.LENGTH_LONG).show();

                }
                else if(resp.equals("NF"))
                {
                    login.setVisibility(View.VISIBLE);

                    Toast.makeText(getApplicationContext(),"Sua conta ainda não foi confirmada, aguarde mais um tempo, caso demore, contate o suporte",Toast.LENGTH_LONG).show();

                }
                else if (resp.split("!").length==3)
                {
                    String[] ids = resp.split("!");
                    Intent intent;
                    if(tipe==1) {
                        intent = new Intent(getApplicationContext(), EntregadorLayout.class);

                    }
                    else{
                        intent= new Intent(getApplicationContext(),ClienteLayout.class);
                    }
                    intent.putExtra("id", ids[0]);
                    intent.putExtra("nome", ids[1]);
                    intent.putExtra("foto", ids[2]);
                    intent.putExtra("login", user);
                    intent.putExtra("pass", pass);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Erro ao Logar "+new String(responseBody),Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getApplicationContext(),"Erro ao Logar "+new String(responseBody),Toast.LENGTH_LONG).show();
            }
        });}
    
}
