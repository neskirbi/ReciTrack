package com.recitrack.recitrack.Login;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.recitrack.recitrack.BuildConfig;
import com.recitrack.recitrack.Metodos;
import com.recitrack.recitrack.Principal.PrincipalView;
import com.recitrack.recitrack.R;

import java.security.Principal;

public class LoginView extends AppCompatActivity implements Login.LoginView {
    EditText mail,pass;
    LoginPresenter loginPresenter;
    Context context;
    Metodos metodos;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);
        context=this;
        metodos=new Metodos(context);
        if(metodos.ValidarLogin()){
            startActivity(new Intent(context, PrincipalView.class));
        }
        loginPresenter=new LoginPresenter(this,context);
        mail=findViewById(R.id.mail);
        pass=findViewById(R.id.pass);
        if(BuildConfig.DEBUG){
            mail.setText("arturo.rubinstein@pieldeconcreto.com");
        }
    }

    public void Validar(View view){
        loginPresenter.Validar(mail.getText().toString(),pass.getText().toString());
    }
    @Override
    public void LoginOK() {
        startActivity(new Intent(context, PrincipalView.class));
    }

    @Override
    public void Error(String msn) {

    }

    @Override
    public void AbreDialogo() {
        dialog= ProgressDialog.show(context, "","Descargando la Información...", true);
        dialog.setCancelable(true);
    }

    @Override
    public void CierraDialogo() {

        dialog.dismiss();
    }
}
