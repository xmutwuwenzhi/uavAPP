package com.parrot.ARDrone;

import static com.parrot.ARDrone.LLB_DBUtil.*;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class NavigationActivity extends Activity
{
    /** Called when the activity is first created. */
	
	static final int DDLB_DIALOG=0;
	static final int NEAR_DISTANCE=1;
	Dialog ddlbDialog; //地点列表对话框的引用
	Dialog nearDialog;
	
    String currCity;
    String currCSMC;
    String currDM; 
    String currDZ;
    
    TextView tv;
    SeekBar sb;
    int i=0;
    
    int state=0;//0-从列表选择  1-自己填写地址
    ArrayList<AddressInfo> addlist=new ArrayList<AddressInfo>();
    ArrayList<AddressInfo> nearlist=new ArrayList<AddressInfo>();

	int COMPONENT_WIDTH ;
	int COMPONENT_HEIGHT;
/********************************************
 * variable of ImageView
 *******************************************/
	static Path path = new Path();
	static boolean firstTime =false;// true;
	Canvas canvas;
	Paint paint = new Paint();
	Bitmap map_bmp;
	ImageView nuaa_view;
//	int CurrentAlpha=0;
	static	int ScreenWidth = 320;
	static	int ScreenHeight = 480;
	static String Bds;
	static String Dst;

	static int BdsId= 0;
	static int DstId= 0;
	static int dstX = 0;
	static int dstY = 0;
	
	static int CurrentX=0;
	static int CurrentY=0;


/********************************************
 *                Handler                   *
 ********************************************/
	Handler hd = new Handler(){
		@Override
		public void handleMessage(Message msg){
			
			switch(msg.what)
			{
				case 0:
				gotomainView();
				break;
				
			}
		}

	};
/********************************************
 *                 欢迎界面                                               *
 ********************************************/    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        //设置为横屏
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //这两句设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        ScreenWidth = metric.widthPixels;               
        ScreenHeight = metric.heightPixels;
        if(firstTime==true) {
			System.out.println(">>>>>>>>R.drawable.nuaa");
			gotoSurfaceView();
		}
        else
        gotomainView();
		
    }
/*******************************************
 *                 主函数                                                 *
 *******************************************/  
    public void gotoSurfaceView(){
    	MySurfaceView mView = new MySurfaceView(this);  //新建一个MySurfaceView对象,调用onCreate（）函数生成一个画图线程
    	setContentView(mView);
    	firstTime = false;
    }
    
    public void gotomainView(){
    /************************************************
     * main view   	
     ***********************************************/
     	setContentView(R.layout.main);
    	System.out.println(">>>>>>>>R.drawable.nuaa");
        final LinearLayout lineOne=(LinearLayout)findViewById(R.id.linear001);
        final LinearLayout lineTwo=(LinearLayout)findViewById(R.id.linear002);
        RadioButton rb1 = (RadioButton) findViewById(R.id.clbxz);                 //列表选择
        RadioButton rb2 = (RadioButton) findViewById(R.id.zjtx);                 //自己填写
//        RadioButton rb3 = (RadioButton) findViewById(R.id.zjcx);                 //最近查询
        ImageButton run = (ImageButton) findViewById(R.id.ImageButton01);
        Button      Bshow = (Button) findViewById(R.id.Button_show);
        final Spinner spB      = (Spinner) findViewById(R.id.Spinner01);
        final Spinner spD      = (Spinner) findViewById(R.id.Spinner02);
        final Bundle  bmessage= new Bundle();     
        ArrayAdapter<String> adapterSpB,adapterSpD;
        
        final String []alItem = {
        "西大门",
        "食堂",
        "体育馆",
        "航天学院",
        "导航中心",
        "机电学院",
        "综合楼",
        "智能楼",
        "八号楼",
        "九号楼",
        "经管院",
        "图书馆",
        "七号楼",
        "五号楼",
        "十八号楼",
        "航空宇航学院",
        "北门"               
        };
        adapterSpB = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,alItem);
        adapterSpB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterSpD = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,alItem);
        adapterSpD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spB.setAdapter(adapterSpB);
        spD.setAdapter(adapterSpD);
            
        spB.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Bds = alItem[arg2].toString();				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        
        	
        }
		);
        spD.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				Dst = alItem[arg2].toString();
//				if(Dst==Bds)
//					Toast.makeText(this, "Mistake.Please change to another!", Toast.LENGTH_LONG);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        
        	
        }
		);
        
        run.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
/*				// TODO Auto-generated method stub
				if(state==0)
				{
					bmessage.putString("Bds", Bds);
					bmessage.putString("Dst", Dst);
					System.out.println(">>>>>>>>>>>>>");
					System.out.println("Bds:"+Bds);
					System.out.println("Dst:"+Dst);
					System.out.println(">>>>>>>>>>>>>");
				}
				else if (state==1)
				{
					
				}
					
				Intent intent = new Intent();
				intent.putExtras(bmessage);
				intent.setClass(NavigationActivity.this, DemoActivity.class);
				startActivity(intent);
				NavigationActivity.this.finish();
  */ 			}
     	
        });
        Bshow.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(state==0)
				{
					System.out.println(">>>>>state=0>>>>>>>>");
					bmessage.putString("Bds", Bds);
					bmessage.putString("Dst", Dst);
					System.out.println(">>>>>>>>>>>>>");
					System.out.println("Bds:"+Bds);
					System.out.println("Dst:"+Dst);
					System.out.println(">>>>>>>>>>>>>");
				}
				else if (state==1)
				{
					
				}
					
				Intent intent = new Intent();
				intent.putExtras(bmessage);
				intent.setClass(NavigationActivity.this, showMap.class);
				startActivity(intent);
				NavigationActivity.this.finish();
			}
        	
        });
        rb1.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				lineOne.setVisibility(1);
				lineTwo.setVisibility(-1);				
			}
		});
        rb2.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				lineOne.setVisibility(-1);
				lineTwo.setVisibility(1);	

			}
	    });
		
        }
        
 //  	initData();
        
//		List<String> cityList = search_dstPot();
//    	currCity=cityList.get(0);    //初始化当前的城市

//    	System.out.println("R.layout.map");
//    	nuaa_view = (ImageView)findViewById(R.id.ImageView1);
//    	nuaa_view.setOnTouchListener(this);
//   	new Thread((Runnable) new  drawthread()).start();
//    	DrawPath drawpath = new DrawPath(this);
//     	drawpath.onDraw();
   
    
	 
}

