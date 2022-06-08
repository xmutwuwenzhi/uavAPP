package com.parrot.ARDrone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView 
	implements SurfaceHolder.Callback{
	
	int i=0;
	NavigationActivity activity;
	Paint      paint;                                   //画笔
	int        currentAlpha=0;                          //不透明值
	int        screenWidth =320;
	int        screenHeight=480;                        //屏幕大小
	int        sleepSpan   =50;                         //动画的时延
	
	static Bitmap[]   logos = new Bitmap[4];                   //
	Bitmap     currentLogo;
	int        currentX;
	int        currentY;
	public MySurfaceView(NavigationActivity activity){
		super(activity);
		this.activity=activity;              
		this.getHolder().addCallback(this);             ///设置生命周期回调接口的实现者
		paint = new Paint();
		paint.setAntiAlias(true);                       //打开抗锯齿
		logos[0]=BitmapFactory.decodeResource(getResources(),R.drawable.dukea);
		logos[1]=BitmapFactory.decodeResource(getResources(), R.drawable.dukeb);
		logos[2]=BitmapFactory.decodeResource(getResources(),R.drawable.dukec);
		logos[3]=BitmapFactory.decodeResource(getResources(), R.drawable.duked);
/*		logos[4]=BitmapFactory.decodeResource(getResources(), R.drawable.welcome2);                         		
		logos[5]=BitmapFactory.decodeResource(getResources(), R.drawable.welcome3);
		logos[2]=BitmapFactory.decodeResource(getResources(), R.drawable.welcome4);                         		
*/
	}
	public void Ondraw(Canvas canvas)
	{
		paint.setColor(Color.BLACK);
		paint.setAlpha(255); 
		
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		if(currentLogo==null)
		{
			return ;
		}
		
		
		paint.setAlpha(currentAlpha);
		canvas.drawBitmap(currentLogo,currentX, currentY, paint);
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		new Thread()
		{
			public void run()
			{
				for(Bitmap bm:logos)
				{	
					currentLogo=bm;
					currentX=(screenWidth-bm.getWidth())/2;
					currentY=(screenHeight-bm.getHeight())/2;
					
					for(int i=255;i>0;i-=10)
					{
						currentAlpha=i;
						if(currentAlpha<0)
						{
							currentAlpha=0;
						}
						SurfaceHolder myHolder = MySurfaceView.this.getHolder();
						Canvas canvas = myHolder.lockCanvas();
						
						try{
							synchronized(myHolder){
								Ondraw(canvas);
							}
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							if(canvas!=null){
								myHolder.unlockCanvasAndPost(canvas);
							}
						}

						try{
							if(i==255)
							{
								Thread.sleep(1000);        //新图片，停一会儿。
							}
						}catch(Exception e){
							e.printStackTrace();
						}								
					}//end of for(i=255,I>-10;i-=10)		
				}//end of for(Bitmap bm:Logos)
				activity.hd.sendEmptyMessage(0);
				
			}
		}.start();
	
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		
	}

	
}