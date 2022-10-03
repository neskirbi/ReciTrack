package com.recitrack.recitrack.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB extends SQLiteOpenHelper {
    private static String Cliente = "",Coordenadas = "",Citas = "",Remision="";
    private static  String DB_NAME = "Recitrack.sqlite";
    private static  int DB_VERSION = 1;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Cliente = "CREATE TABLE IF NOT EXISTS  clientes(" +
                "id text DEFAULT ''," +
                "nombres text DEFAULT ''," +
                "apellidos text DEFAULT ''," +
                "mail text DEFAULT '')";

        sqLiteDatabase.execSQL(Cliente);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
