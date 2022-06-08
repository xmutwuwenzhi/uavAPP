package com.parrot.ARDrone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.opengl.GLSurfaceView;
import android.text.Layout;
import android.util.Log;
import android.view.ViewGroup;


public class showMap  extends Activity  implements SensorEventListener
{
	static String strDst;
	static String strBds;
	
   
	Intent intentback=new Intent();
	Intent intent=new Intent();
	Button button_information;
	Button button_halt;
	
	private static final  String LOG_TAG = "ARDrone Activity"; 
	private SensorManager mSensorManager;
    private DemoGLSurfaceView mGLView;
	private Sensor mSensor;
	private Button mMoveUpButton;
	private Button mMoveDownButton;
	private Button mTakeOffButton;
	private Button mEmergLandButton;
	private ViewGroup mLayoutView;
	// private GLSurfaceView mLayoutView;
	
	// Used for toggle the takeoff/landing button text
	public showMap()
	{		
		System.out.println("showMap"); 
	}	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("before R.layout.map");
		mGLView= new DemoGLSurfaceView(this);
		mGLView =  (DemoGLSurfaceView) findViewById(R.id.glsurfaceview);
		try {
			mGLView.initialize(this);
		}catch(Exception e)
		{

		}
		System.out.println("after R.layout.map");
		Intent intentR=getIntent(); 
		if(intentR.getStringExtra("Dst")!=null)
		{
		strDst = intentR.getStringExtra("Dst");
		
	    strBds = intentR.getStringExtra("Bds");
		}
	    System.out.println("strDst:"+strDst);
	    System.out.println("strBds:"+strBds);
	
  		setContentView(R.layout.map);
		button_information= (Button) findViewById(R.id.information);
		button_halt = (Button) findViewById(R.id.Button_halt);
/*		
		button_information.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v) {
				// TODO Auto-generated method stub
				DemoActivity.nativeCommand(1,0,0,0,0,0);
				System.out.println("Takeoff");
//				intent.setClass(showMap.this, NavigationActivity.class);System.out.println("onClick");
//				intent.setClass(showMap.this, DemoActivity.class);System.out.println("onClick");
//				startActivity(intent);
				
			}
			
			
		});
		
		
		
		button_halt.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v){
				//Emergency halt!!!!!!!!!!!!!!!!!!!!!
				DemoActivity.nativeCommand(2,0,0,0,0,0);
			}
		});
*/
		button_information.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v) {
				// TODO Auto-generated method stub
				intentback.setClass(showMap.this, NavigationActivity.class);
				startActivity(intentback);
			}
			
		});
		
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
       WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


// Get Sensor Manager
      mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
      mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "驾驶员视角");
		menu.add(0, 1, 0,"飞行控制信息");   
		return super.onCreateOptionsMenu(menu);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		System.out.println("item.getItemId:"+item.getItemId());
		switch(item.getItemId())
		{
		case 0:           //驾驶员视角图片。。。
			System.out.println("Menu.First+1");
			intent.setClass(showMap.this,showSight.class);
			startActivity(intent);
		break;
		case 1:           //飞行控制信息数据。。。经纬度。。。距离。。速度。。。
			intent.setClass(showMap.this, showData.class);
			startActivity(intent);
		break;
		}
		
		return super.onOptionsItemSelected(item);
	}
    // Set flags to keep screen from dimming


@Override
protected void onPause() {
    super.onPause();
    mGLView.onPause();
	mSensorManager.unregisterListener(this);
}

@Override
protected void onResume() {
    super.onResume();
//    mGLView.onResume();
	// update every 200 ms (NORMAL), 60 ms (UI) or 20 ms (GAME)
    mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
}

@Override
protected void onStop() {
    super.onStop();
    nativeStop();
}

public void onAccuracyChanged(Sensor sensor, int accuracy) {
	/* do nothing ? */
}

public void onSensorChanged(final SensorEvent ev) {
	
	//Log.d(LOG_TAG, "azimuth= " + ev.values[0] +  " pitch= " + ev.values[1] +  "roll= " + ev.values[2] );
//www	nativeSensorEvent(ev.values[0],ev.values[1],ev.values[2]);
}

static {
//    System.loadLibrary("ardrone");

}
    

/**
 * Method for pass enumerated commands to native layer
 * 
 * @param commandId
 * @param iparam1
 * @param fparam1
 * @param fparam2
 * @param fparam3
 * @param fparam4
 */
private static native void nativeCommand(int commandId, int iparam1, float fparam1, float fparam2, float fparam3, float fparam4);

private static native void nativeStop();
private static native void nativeSensorEvent(float x, float y, float z);
	
	
 }

	
	
	
