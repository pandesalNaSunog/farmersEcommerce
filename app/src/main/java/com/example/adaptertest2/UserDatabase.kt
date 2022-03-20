package com.example.adaptertest2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class UserDatabase(context: Context): SQLiteOpenHelper(context, databaseName, null, databaseVersion) {
    companion object{
        private const val databaseName = "user"
        private const val databaseVersion = 2
        private const val tableName = "user_tbl"

        private const val id = "id"
        private const val name = "name"
        private const val storeName = "storeName"
        private const val email = "email"
        private const val phone = "phone"
        private const val address = "address"
        private const val farmerId = "farmerId"
        private const val coordinates = "coordinates"
        private const val onlineId = "onlineId"
        private const val type = "type"
        private const val token = "token"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query = "CREATE TABLE $tableName($id INTEGER PRIMARY KEY, $name TEXT, $storeName TEXT, $email TEXT, $phone TEXT, $address TEXT, $farmerId TEXT, $coordinates TEXT, $onlineId INTEGER, $type TEXT, $token TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val query = "DROP TABLE IF EXISTS $tableName"
        db?.execSQL(query)
        onCreate(db)
    }

    fun addItem(user: User, stoken: String){
        val db = this.writableDatabase
        val content = ContentValues()

        content.put(name,user.name)
        content.put(storeName,user.store_name)
        content.put(email, user.email)
        content.put(phone, user.phone)
        content.put(address, user.address)
        content.put(farmerId, user.farmers_cooperative_id)
        content.put(coordinates, user.coordinates)
        content.put(onlineId, user.id)
        content.put(type, user.type)
        content.put(token, stoken)

        db.insert(tableName, null,content)
        db.close()
    }

    @SuppressLint("Range")
    fun getAll(): User?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tableName"

        val cursor: Cursor
        var user: User? = null
        try{
            cursor = db.rawQuery(query,null)
        }catch(e: SQLiteException){
            db.execSQL(query)
            return user
        }

        if(cursor.moveToFirst()){
            val stype = cursor.getString(cursor.getColumnIndex(type))
            val sname: String? = cursor.getString(cursor.getColumnIndex(name))
            val semail: String? = cursor.getString(cursor.getColumnIndex(email))
            val sstoreName: String? = cursor.getString(cursor.getColumnIndex(storeName))
            val sphone: String? = cursor.getString(cursor.getColumnIndex(phone))
            val saddress: String? = cursor.getString(cursor.getColumnIndex(address))
            val sfarmerId: String? = cursor.getString(cursor.getColumnIndex(farmerId))
            val scoordinates: String? = cursor.getString(cursor.getColumnIndex(coordinates))
            val sonlineId: Int? = cursor.getInt(cursor.getColumnIndex(onlineId))

            user = User(saddress,scoordinates,null, semail,null,sfarmerId,sonlineId,sname,sphone,null,sstoreName,stype,null)
        }
        return user
    }
    @SuppressLint("Range")
    fun getSize(): Int{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tableName"

        val cursor: Cursor
        var user: User?
        val list = ArrayList<User>()
        try{
            cursor = db.rawQuery(query,null)
        }catch(e: SQLiteException){
            db.execSQL(query)
            return list.size
        }

        if(cursor.moveToFirst()){
            val stype = cursor.getString(cursor.getColumnIndex(type))
            val sname: String? = cursor.getString(cursor.getColumnIndex(name))
            val semail: String? = cursor.getString(cursor.getColumnIndex(email))
            val sstoreName: String? = cursor.getString(cursor.getColumnIndex(storeName))
            val sphone: String? = cursor.getString(cursor.getColumnIndex(phone))
            val saddress: String? = cursor.getString(cursor.getColumnIndex(address))
            val sfarmerId: String? = cursor.getString(cursor.getColumnIndex(farmerId))
            val scoordinates: String? = cursor.getString(cursor.getColumnIndex(coordinates))
            val sonlineId: Int? = cursor.getInt(cursor.getColumnIndex(onlineId))

            user = User(saddress,scoordinates,null, semail,null,sfarmerId,sonlineId,sname,sphone,null,sstoreName,
                stype,null)
            list.add(user)
        }
        return list.size
    }
    @SuppressLint("Range")
    fun getToken(): String{
        val db = this.readableDatabase
        val query = "SELECT * FROM $tableName"

        val cursor: Cursor
        var stoken = ""
        try{
            cursor = db.rawQuery(query,null)
        }catch(e: SQLiteException){
            db.execSQL(query)
            return stoken
        }

        if(cursor.moveToFirst()){
            stoken = cursor.getString(cursor.getColumnIndex(token))
        }
        return stoken
    }

    fun deleteAll(){
        val db = this.writableDatabase

        db.delete(tableName,null,null)
        db.close()
    }
}