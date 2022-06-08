package com.parrot.ARDrone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.ImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class sight extends ImageView implements OnTouchListener
{
	static String strTime;
	static String strDistant;
	static int    intTime;
	static int    intDistant=1000;
    static int    length;	
	static Bitmap height_bmp;
	static Bitmap airspeed_bmp;
	static Bitmap line_bmp;
	static Bitmap direction_bmp;
	static Bitmap background_bmp;
	static Bitmap background1_bmp;
	static Bitmap triangular_bmp;
	
	static Matrix matrix_height = new Matrix();
	static Matrix matrix_airspeed = new Matrix();
	static Matrix matrix_line = new Matrix();
	static Matrix matrix_direction = new Matrix();
	static Matrix matrix_background = new Matrix();
	static Matrix matrix_background1 = new Matrix();
	static Matrix matrix_triangular = new Matrix();
	Paint paint = new Paint();
	
	public sight(Context context,AttributeSet as) {
		super(context,as);
		// TODO Auto-generated constructor stub
		height_bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.height));
		airspeed_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.airspeed));
		line_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.line));
		direction_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.direction));
		background_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.background));
		background1_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.background1));
		triangular_bmp=BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.triangular));
		
		paint.setColor(Color.GREEN);
		matrix_direction.setScale(1.12f,1.12f);
		matrix_direction.postTranslate(0f, 310f);
		matrix_line.setScale(3f, 3f);
		matrix_line.postTranslate(9f, 122f);
		matrix_height.setScale(1f, 1f);
		matrix_height.postTranslate(30f, 60f);
		matrix_background.setScale(1f, 1f);
		matrix_background1.setScale(1f, 1f);
		matrix_background1.postTranslate(0f, 285f);
		matrix_triangular.postTranslate(130f, 285f);
		
		new Thread(){
			public void run(){
				while(!Thread.currentThread().isInterrupted())
				{
					postInvalidate();

					try{
						sleep(1000);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}										
				}
			}	
		}.start();
	}
	public void getData()
	{
		intTime++;
		intDistant--;
		strDistant = intDistant+"m";
		strTime = intTime+"s";
	}
	
	public void onDraw(Canvas canvas)
	{	
		getData();
		canvas.drawBitmap(background_bmp, matrix_background, paint);		
		canvas.drawBitmap(height_bmp, matrix_height, paint);
		
		canvas.drawBitmap(background1_bmp, matrix_background1, paint);
		canvas.drawBitmap(direction_bmp, matrix_direction, paint);
		canvas.drawBitmap(triangular_bmp, matrix_triangular, paint);
		canvas.drawBitmap(line_bmp,matrix_line,paint);
		
		canvas.drawText("·ÉÐÐÊ±¼ä", 30,20,paint);
		canvas.drawText("´ý·É¾àÀë",200,20,paint);
		length=strTime.length();
		canvas.drawText(strTime,62-length,40,paint);
		length=strDistant.length();
		canvas.drawText(strDistant,221-length,40,paint);
	}
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
}