package com.parrot.ARDrone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class showData extends Activity
{
	Intent intent=new Intent();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{	
			intent.setClass(showData.this,showMap.class);
			startActivity(intent);
		}			
		else if(keyCode==KeyEvent.KEYCODE_HOME)
		{
			intent.setClass(showData.this, NavigationActivity.class);
			startActivity(intent);
		}
		
		return true;
									
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0,"首页");
		menu.add(0, 1, 0,"显示地图");
		menu.add(0, 2, 0,"显示飞行视角");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
		case 0:
			intent.setClass(showData.this,NavigationActivity.class);
			startActivity(intent);
			break;
		case 1:
			intent.setClass(showData.this,showMap.class);
			startActivity(intent);
			break;
		case 2:
			intent.setClass(showData.this,showSight.class);
			startActivity(intent);
			break;
		
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	     if (keyCode == KeyEvent.KEYCODE_MENU) {
	     super.openOptionsMenu();
	     }
	     return true;
	     }
	
	
}