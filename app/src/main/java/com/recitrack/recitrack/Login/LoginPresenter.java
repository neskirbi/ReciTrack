package com.recitrack.recitrack.Login;

import android.content.Context;

public class LoginPresenter implements Login.LoginPresenter {
    LoginView loginView;
    Login.LoginInteractor loginInteractor;
    Context context;
    public LoginPresenter(LoginView loginView, Context context) {
        this.context=context;
        this.loginView=loginView;
        loginInteractor=new LoginInteractor(this,context);

    }

    @Override
    public void Validar(String mail, String pass) {
        loginInteractor.Validar(mail,pass);
    }

    @Override
    public void LoginOk() {
        loginView.LoginOK();
    }

    @Override
    public void Error(String msn) {
        loginView.Error(msn);
    }

    @Override
    public void AbreDialogo() {
        loginView.AbreDialogo();
    }

    @Override
    public void CierraDialogo() {
        loginView.CierraDialogo();
    }

    @Override
    public void ValidarLogin() {

    }
}
