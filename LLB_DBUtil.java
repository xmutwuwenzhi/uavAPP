package com.parrot.ARDrone;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.EditText;
 
public class LLB_DBUtil extends SQLiteOpenHelper
{
	
		private static final CursorFactory CursorFactory = null;

		public LLB_DBUtil(Context context, String name, CursorFactory factory,int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
		public LLB_DBUtil(Context context, String name) {
			super(context, name, CursorFactory, 1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("create table if not exists user("
			+ "idh int,"
			+ "idz int,"
			+ "number int,"
			+ "name varchar(20),"
			+ "X    float,"
			+ "Y    float)");
			}
		    

			//当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}

			
}


	    	  