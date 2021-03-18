package com.balvarezazocar.ciensonetosdeamor.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AdminSQLiteOpenHelper (
    context: Context,
    name:String,
    factory: SQLiteDatabase.CursorFactory?,
    version:Int
        ): SQLiteOpenHelper (context, name, factory,version){

    override fun onCreate(db:SQLiteDatabase){
        db.execSQL("create table sonetos (codigo text primary key,descripcion text, liked Int)")
    }
    override fun onUpgrade(db: SQLiteDatabase, olVersion:Int, newVersion: Int){

    }
}