package com.parrot.ARDrone;
import com.parrot.ARDrone.NavigationActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GGView extends View 
{
	NavigationActivity activity;  //Activity
	int COMPONENT_WIDTH;           //View的宽度
	int COMPONENT_HEIGHT;          //View的高度
	boolean initflag=false;	
	static Bitmap[] bma;	               //图片数组
	Paint paint;
	int[] drawablesId;
	int currIndex=0;                 //当前索引
	boolean workFlag=true;
	static boolean firstTimeGGview=true;
	public GGView(Context father,AttributeSet as) //有参构造器
	{ 
		super(father,as);	        //调用父类
		this.drawablesId=new int[]  //图片数组
		{
			R.drawable.adv1,	
			R.drawable.adv2,					
			R.drawable.adv3,				 
		};
		bma=new Bitmap[drawablesId.length];		
		initBitmaps();            //加载图片的方法		
		paint=new Paint();      //画笔
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);//消除锯齿		
		
		new Thread()           //线程
		{
			public void run()   //重写方法
			{
				while(workFlag)
				{
					currIndex=(currIndex+1)%drawablesId.length;
					GGView.this.postInvalidate(); //重画
					try 
					{
						Thread.sleep(3000);    //线程休息
					} catch (InterruptedException e) 
					{						
						e.printStackTrace();   //打印异常信息
					}
				}
			}
		}.start();
	}
	
	public void initBitmaps()   //加载图片的方法
	{
		Resources res=this.getResources(); //得到资源
		for(int i=0;i<drawablesId.length;i++)
		{
			bma[i]=BitmapFactory.decodeResource(res, drawablesId[i]);
		}
	}
	
	public void onDraw(Canvas canvas)
	{
		if(!initflag)
		{
			COMPONENT_WIDTH=this.getWidth();//获取view的宽度
			COMPONENT_HEIGHT=this.getHeight();//获取view的高度
			initflag=true;
		}
		
		int picWidth=bma[currIndex].getWidth();   //图片的宽度
		int picHeight=bma[currIndex].getHeight();  //图片的高度
		
		int startX=(COMPONENT_WIDTH-picWidth)/2;   //开始贴图的起点x坐标
		int startY=(COMPONENT_HEIGHT-picHeight)/2; //开始贴图的起点的y坐标
		
		//绘制背景色
		canvas.drawARGB(255, 255,204, 128);		
		canvas.drawBitmap(bma[currIndex], startX,startY, paint); //正式绘制图片
		
	}
}
