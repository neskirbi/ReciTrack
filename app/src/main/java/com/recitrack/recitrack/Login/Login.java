package com.recitrack.recitrack.Login;

public interface Login {

    interface LoginView{
        void LoginOK();
        void Error(String msn);
        void AbreDialogo();
        void CierraDialogo();
    }

    interface LoginPresenter{
        void Validar(String mail,String pass);
        void LoginOk();
        void Error(String msn);
        void AbreDialogo();
        void CierraDialogo();
        void ValidarLogin();
    }

    interface LoginInteractor{
        void Validar(String mail,String pass);
    }
}
