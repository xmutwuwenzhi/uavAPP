package com.parrot.ARDrone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import static com.parrot.ARDrone.R.drawable.nuaa;
import static java.lang.Math.sqrt;


@SuppressLint("NewApi")
public class DrawPath<EditView> extends  ImageView implements OnTouchListener
{
//	float points[]={(float) 22.11,(float) 22.11};	
//	Canvas canvas;
/***************   global variable ***********************/		
	static Bitmap    map_bmp;                                           
	ImageView        imgNuaa;
	static Bitmap    begin_bmp;
	static Bitmap 	 ardrone;
	static Bitmap    end_bmp;
	float            COMPONENT_WIDTH  = 0.0f;                              //��ǰView�Ŀ�͸�
	float            COMPONENT_HEIGHT = 0.0f;
	String[] sql=
		{
	"insert into user values(0,0,0,'������',75,2645)",
	"insert into user values(1,0,0,'��һ��·��',131,2655)",
	"insert into user values(3,0,0,'�ڶ���·��',458,2699)",
	"insert into user values(5,0,0,'������·��',870,2754)",
	"insert into user values(7,0,0,'С����',1370,2821)",
	"insert into user values(8,0,0,'ʳ��',1549,2837)",
	//�����һ��·��
	"insert into user values(1,0,0,'��һ��·��',131,2655)",
	"insert into user values(1,0,1,'����������·��',145,2533)",
	"insert into user values(1,0,3,'�����ݱ���·��',182,2220)",
    "insert into user values(1,1,4,'����ѧԺ����·��',199,2077)",
    "insert into user values(1,2,7,'С����·��',265,1600)",
    "insert into user values(1,2,8,'�ݷ��ѧ������·��',298,1300)",
	"insert into user values(1,2,9,'  ',319,1270)",
	"insert into user values(1,2,10,'  ',350,1250)",
	"insert into user values(1,2,11,'  ',390,1231)",
	"insert into user values(1,2,12,'�ݷ��ѧ������·�ڱ���',521,1241)",
	"insert into user values(1,2,13,'�ݷ��ѧ�������ſ�',182,2220)",
	
	//�����һ��Ŀ�ĵ�
	"insert into user values(2,0,1,'������',288,2553)",
	"insert into user values(2,1,4,'����ѧԺ',377,2100)",
	"insert into user values(2,2,7,'��������',430,1623)",

    //����ڶ���·��
	"insert into user values(3,0,0,'�ڶ���·��',458,2699)",				
	"insert into user values(3,0,1,'�����ݶ��Ͻ�·��',471,2576)",
	"insert into user values(3,0,2,'�ۺ�¥���Ͻ�·��',495,2395)",
	"insert into user values(3,0,3,'�����ݶ�����·��',513,2265)",
	"insert into user values(3,1,4,'�ۺ�¥������·��',532,2126)",
    "insert into user values(3,2,7,'��԰������·��',595,1645)",
	"insert into user values(3,2,8,'����ѧԺ',621,1423)",
	"insert into user values(3,2,9,'����¥���Ͻ�',623,1404)",
	"insert into user values(3,3,13,'����¥����·��',643,1252)",

	//����ڶ���Ŀ�ĵ�
	"insert into user values(4,0,2,'�ۺ�¥',708,2342)",
	"insert into user values(4,1,4,'�ۺ�¥',745,2154)",
	"insert into user values(4,2,9,'����¥',812,1421)",
	"insert into user values(4,3,12,'����¥',830,1267)",	
	"insert into user values(4,3,15,'�˺�¥',1057,904)",
	"insert into user values(4,4,20,'����Ժ',1100,339)",	
	
	//���������·��      ��԰·
	"insert into user values(5,0,0,'������·��',870,2721)",	
	"insert into user values(5,0,2,'ͼ������Ͻ�·��',909,2453)",
	"insert into user values(5,1,4,'ͼ���������·��',944,2180)",
	"insert into user values(5,1,5,'�ߺ�¥���Ͻ�·��',970,1986)",
	"insert into user values(5,1,6,'���¥���Ͻ�·��',991,1835)",
    "insert into user values(5,2,7,'ʮ�˺�¥���Ͻ�·��',1009,1700)",
	"insert into user values(5,2,9,'����¥���Ͻ�·��',1043,1438)",
    "insert into user values(5,3,13,'����¥������·��',1056,1287)",
    "insert into user values(5,4,14,'�˺�¥���Ͻ�·��',1093,979)",
    "insert into user values(5,4,15,'�˾ź�¥����',1103,894)",
	"insert into user values(5,4,16,'ʮ��¥�ſ�����·��',1119,724)",
	"insert into user values(5,4,17,'ʮ��¥���Ͻ�·��',856,703)",
	"insert into user values(5,4,18,'ʮ��¥������·��',879,477)",
	"insert into user values(5,4,19,'ʮ��¥����',1137,500)",
	"insert into user values(5,4,20,'��Ժ��Ժ����·��',1152,339)",
	"insert into user values(5,4,21,'����',1167,164)" ,
	//���������Ŀ�ĵ�
	"insert into user values(6,0,2,'ͼ���',1090,2476)",	
	"insert into user values(6,1,5,'�ߺ�¥',1070,2000)",
	"insert into user values(6,1,6,'���¥',1119,1855)",
 	"insert into user values(6,2,7,'ʮ�˺�¥',1143,1716)",
 	"insert into user values(6,3,13,'�����ѧԺ',1203,1316)",
 	"insert into user values(6,4,15,'�ź�¥',1151,895)"
 	
		};
	int              ScreenWidth      = 480;                               //��Ļ�Ŀ�͸�
	int              ScreenHeight     = 320;
	int              giTemp           =  0 ;                               //tempValue  

/*************** variable of paint ***********************/	
	static Path      pathMan      = new Path();
	static Path      path         = new Path();
	
	Paint            linePaint    = new Paint();                           //���ʣ����ڻ���·��	
	Paint            paint        = new Paint();                           //���ʣ����ڻ�������
	
	float []         oldPts ={-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f};       ;//,495f,2396f,510f,2100f};//the path Points.   
	float []         pts  =  {-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f,-1f};        ;//,165f,2340f,510f,2100f};
	static boolean          firstTime    = true;
	static int       dstX         = 0;
	static int       dstY         = 0;
	static int       CurrentX     = 0;
	static int       CurrentY     = 0;
	
    float            oldpathStartX= 10.0f;
	float            oldpathStartY= 200.0f;
	float            oldpathDstX  = 250.0f;
	float            oldpathDstY  = 200.0f;

	static float     pathStartX   = 10.0f;
	static float     pathStartY   = 200.0f;
	static float     pathDstX     = 250.0f;
	static float     pathDstY     = 200.0f;

/************** variable  of  Multi_touch **************/
	static Matrix    matrix_begin = new Matrix();
	static Matrix    matrix_end   = new Matrix();
	static Matrix    matrix_ardrone =  new Matrix();
	static Matrix    matrix       = new Matrix();     
	float scale        = 1.0f;
	float            oldScale     = 1.0f;
	float            oldOffX      = 0.0f;
    float            oldOffY      = 0.0f;
    float            newOffX      = 0.0f;
    static float     newOffY      = 0.0f;
	Matrix           savedMatrix  = new Matrix();     
    static final int NONE         = 0;     
    static final int DRAG         = 1;     
    static final int ZOOM         = 2;
	private static final Canvas Canvas = null;    
	int              mode         = NONE;     
	PointF           start        = new PointF();     
	PointF           mid          = new PointF();     
	double oldDist      = 1.0f;
	double newDist      = 1.0f;
	float            eventSmallX  = 0.0f;
	float            eventSmallY  = 0.0f;
	boolean          flagZOOM     = false;
	boolean          ACTION_POINTER_UP = false;
	LLB_DBUtil       dbHelper;
	
	int[]            sidz         = new int[2];
	int[]            sidh         = {-1,-1};
	int[]            snumber      =new int[2];
	
	float[]          sX           = new float[2];
	float[]          sY           = new float[2];
	
	float            aX                         ;
	float            aY                         ;
	
	int[]            didz         = new int[2];
	int[]            didh         = {-1,-1};
	int[]            dnumber      = new int[2];
	
	float[]          dX           = new float[2];
	float[]          dY           = new float[2];
	
	/*****************variable  of  socket*******************/

	TextView showJWD;
	public void getPath()
	{
		/*
		 * 1��ȡ��strBds��strDst����Ϣ�����Ʋ��ҵõ���������Ϣ�����ܻ����������ϱ��ţ�
		 * 
		 * 2��ȡ����Ϣ��ͨ��sidh ȡ��ý���һ��
		 * 
		 * 3����ֵoldPts,���䣬���ǵ�һ��λ�ù��䡣���ܹ���͹��䣩
		 * 
		 * 4������ѡ���һ��������һ���㣬���
		 */
//		oldPts={};
		dbHelper = new LLB_DBUtil(getContext(), "myDb");
		SQLiteDatabase db = dbHelper.getReadableDatabase();			
		Cursor cursor;
		  // ��fileIdΪnull��""���ѯ���м�¼
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("showMap.strBds"+showMap.strBds);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		cursor = db.rawQuery("select * from user where name='"+showMap.strBds+"'",null);
		
//		cursor = db.rawQuery("select * from user where name='��������'",null);
		if(cursor !=null)
		{
//			System.out.println("NO NULL");
			if(cursor.moveToFirst())
			{   
				int i=0; 
				do{
                    System.out.println("i:"+i);
                    System.out.println("cs_name");
                    int numColumn=cursor.getColumnIndex("name");
                    String name =  cursor.getString(numColumn);
                    System.out.println("cs_name:"+name);

                    numColumn = cursor.getColumnIndex("idh");
                    String idh = cursor.getString(numColumn);//
                    int iidh=Integer.valueOf(idh);
                    System.out.println("cs_iidh:"+iidh);
                    sidh[i]= iidh;

                    numColumn = cursor.getColumnIndex("idz");
                    String idz = cursor.getString(numColumn);//
                    int iidz=Integer.valueOf(idz);
                    System.out.println("cs_iidz:"+iidz);
                    sidz[i]= iidz;

                    numColumn = cursor.getColumnIndex("number");
                    String num = cursor.getString(numColumn);//
                    int inumber=Integer.valueOf(num);
                    System.out.println("cs_inumber:"+inumber);
                    snumber[i]= inumber;

                    numColumn = cursor.getColumnIndex("X");
                    String X = cursor.getString(numColumn);//
                    float xx=Float.valueOf(X);
                    sX[i]= xx;

                    numColumn=cursor.getColumnIndex("Y");
                    String Y = cursor.getString(numColumn);//
                    float yy=Float.valueOf(Y);
                    sY[i] = yy;
                    i++;
                    if(i>1) break;

			    }while(cursor.moveToNext());
			}
			else
			{
				System.out.println("movetoFirst:null");
			}
			cursor.close();
			cursor =null;
		}
	
		cursor = db.rawQuery("select * from user where name='"+showMap.strDst+"'",null);
		if(cursor !=null)
		{
			if(cursor.moveToFirst())
			{   int i=0; 
				do{
				
				int numColumn=cursor.getColumnIndex("name");
				String name =  cursor.getString(numColumn);
				System.out.println("cd_name"+name);
			
				numColumn = cursor.getColumnIndex("idh");
				String idh = cursor.getString(numColumn);//  
				int iidh=Integer.valueOf(idh);	
				System.out.println("cd_iidh:"+iidh);
				didh[i]= iidh;

				numColumn = cursor.getColumnIndex("idz");
				String idz = cursor.getString(numColumn);//  
				int iidz=Integer.valueOf(idz);	
				System.out.println("cd_iidz:"+iidz);
				didz[i]= iidz;
				
				numColumn = cursor.getColumnIndex("number");
				String num = cursor.getString(numColumn);//  
				int inumber=Integer.valueOf(num);	
				System.out.println("cd_inumber:"+inumber);
				System.out.println("i:"+i);
				dnumber[i]= inumber;
				System.out.println("dnumber["+i+"]:::"+dnumber[0]);
				
				numColumn = cursor.getColumnIndex("X");
				String X = cursor.getString(numColumn);//  
				float xx=Float.valueOf(X);				
				dX[i]= xx;
				
				numColumn=cursor.getColumnIndex("Y");
				String Y = cursor.getString(numColumn);//  
				float yy=Float.valueOf(Y);
				dY[i] = yy;				
				i++;
				if(i>1) break;

			}while(cursor.moveToNext());
				System.out.println("i:::"+i);
			}
			
			cursor.close();
			cursor =null;
		}
        //�����������ţ�����ѡ��,����������ʱ��ʹ�õľ��ǵ�һ����
		if(sidh[1]!=-1)
		{
			System.out.println("sidh.length:"+sidh.length);
			System.out.println("sidh[0]:::"+sidh[0]);
			System.out.println("sidh[1]:::"+sidh[1]);
			if(Math.abs(sidz[0]-didz[0]) > Math.abs(sidz[1]-didz[0]))
			{
				sidh[0]=sidh[1];
				sidz[0]=sidz[1];
				snumber[0]=snumber[1];
				sX[0]=sX[1];
				sY[0]=sY[1];
			}
			//else ��ʹ������[0]�����ݡ�
			
		}

		if(didh[1]!=-1)
		{
			System.out.println("didh!=-1");
			if(Math.abs(didz[0]-sidz[0]) > Math.abs(didz[1]-sidz[0]))
			{
				didh[0]=didh[1];
				didz[0]=didz[1];
				dnumber[0]=dnumber[1];
				dX[0]=dX[1];
				dY[0]=dY[1];
			}
			//else ��ʹ������[0]�����ݡ�
			
		}
		System.out.println("snumber[0]::"+snumber[0]);
		System.out.println("dnumber[0]::"+dnumber[0]);
		int CurrentNumber =0;
		int Currentidh    =0;
		int lastNumber    =0;
		int lastidh       =0;
		String CurrentName= new String();
		String lastName =  new String();
		if(snumber[0]<dnumber[0])                                 //YֵԽ��number ԽС������Դ>Ŀ����numberԴ<Ŀ ��·����ӳ����ػ���Ŀ�ĵء�
		{
			oldPts[0]=sX[0];
			oldPts[1]=sY[0];
			
			CurrentNumber = snumber[0];
			Currentidh    = sidh[0];
			CurrentName   = showMap.strBds;
			lastNumber    = dnumber[0];
			lastidh       = didh[0];
			lastName      = showMap.strDst;
			
		}
		else
		{
			oldPts[0]=dX[0];
			oldPts[1]=dY[0];
			CurrentNumber = dnumber[0];
			Currentidh    = didh[0];
			CurrentName   = showMap.strDst;
			lastNumber    = snumber[0];
			lastidh       = sidh[0];
			lastName      = showMap.strBds; 
		}
		
		System.out.println("sX[0]::"+sX[0]);
		System.out.println("sY[0]::"+sY[0]);
		System.out.println("CurrentNumber:"+CurrentNumber);
		System.out.println("Currentidh:"+Currentidh);
		System.out.println("lastNumber:"+lastNumber);
		System.out.println("lastidh:"+lastidh);
		int i=2;

		if(CurrentNumber!=lastNumber||Currentidh!=lastidh)
		{		
			if(CurrentName!="����ѧԺ")
			if(Currentidh>lastidh)
			{
				Currentidh=Currentidh-1;			
			}
			else
			{
				Currentidh=Currentidh+1;
			}
			if(Currentidh==7)   //����û��7����·/*������չΪʮ�˺�¥��ʳ�õ�С·��
				Currentidh=5;
			for(;CurrentNumber<=lastNumber;CurrentNumber++)
			{
				System.out.println("CurrentNumber:"+CurrentNumber);
				System.out.println("lastNumber:"+lastNumber);
				System.out.println("Currentidh:"+Currentidh);
				System.out.println("lastidh:"+lastidh);
				cursor = db.rawQuery("select * from user where number='"+CurrentNumber+"' and idh='"+Currentidh+"'",null);
				if(cursor.moveToFirst())
				{
					int numColumn = cursor.getColumnIndex("idh");
					String idh = cursor.getString(numColumn);//  
					int iidh=Integer.valueOf(idh);				
					Currentidh= iidh;
				
					numColumn = cursor.getColumnIndex("number");
					String num = cursor.getString(numColumn);//  
					int inumber=Integer.valueOf(num);				
					CurrentNumber= inumber;
				
					numColumn = cursor.getColumnIndex("X");
					String X = cursor.getString(numColumn);//  
					int xx=Integer.valueOf(X);				
					oldPts[i]= xx;
					i++;
					numColumn = cursor.getColumnIndex("Y");
					String Y = cursor.getString(numColumn);//  
					int yy=Integer.valueOf(Y);				
					oldPts[i]= yy;
					i++;
				
				
					if(Math.abs(Currentidh-lastidh)>1)
					{
						if(CurrentNumber==0||CurrentNumber==2||CurrentNumber==4||CurrentNumber==9||CurrentNumber==13)                              //������
						{
							if(Currentidh>lastidh)
							{
								System.out.println("**********************");
								System.out.println("Currentidh:"+Currentidh);
								System.out.println("lastidh:"+lastidh);
								for(Currentidh=Currentidh-2;Currentidh>=lastidh;Currentidh-=2) //�ȼ�
								{
									cursor = db.rawQuery("select * from user where number='"+CurrentNumber+"' and idh='"+Currentidh+"'",null);
									if(cursor.moveToFirst())
									{
										numColumn = cursor.getColumnIndex("idh");
										idh = cursor.getString(numColumn);//  
										iidh=Integer.valueOf(idh);				
										Currentidh= iidh;
									
										numColumn = cursor.getColumnIndex("number");
										num = cursor.getString(numColumn);//  
										inumber=Integer.valueOf(num);				
										CurrentNumber= inumber;
									
										numColumn = cursor.getColumnIndex("X");
										X = cursor.getString(numColumn);//  
								    	xx=Integer.valueOf(X);				
										oldPts[i]= xx;
										i++;
									
										numColumn = cursor.getColumnIndex("Y");
										Y = cursor.getString(numColumn);//  
										yy=Integer.valueOf(Y);				
										oldPts[i]= yy;
										i++;
								
									}
								}
								
								Currentidh+=2;
							}
							else
							{
								for(Currentidh=Currentidh+2;Currentidh<=lastidh;Currentidh+=2)
								{
									System.out.println("######################");
									cursor = db.rawQuery("select * from user where number='"+CurrentNumber+"' and idh='"+Currentidh+"'",null);
									if(cursor.moveToFirst())
									{
										numColumn = cursor.getColumnIndex("idh");
										idh = cursor.getString(numColumn);//  
										iidh=Integer.valueOf(idh);				
										Currentidh= iidh;
								
										numColumn = cursor.getColumnIndex("number");
										num = cursor.getString(numColumn);//  
										inumber=Integer.valueOf(num);				
										CurrentNumber= inumber;
								
										numColumn = cursor.getColumnIndex("X");
										X = cursor.getString(numColumn);//  
										xx=Integer.valueOf(X);				
										oldPts[i]= xx;
										i++;
								
										numColumn = cursor.getColumnIndex("Y");
										Y = cursor.getString(numColumn);//  
										yy=Integer.valueOf(Y);				
										oldPts[i]= yy;
										i++;
							
									}
									
								}								
									
								Currentidh-=2;
							}
							
						
						}
					}
					
				}
				if(Currentidh==3&&CurrentNumber==13&&lastNumber>13)    //������¥�����ǣ��������������ɵ��� 
				{	
			        Currentidh+=2;
			        CurrentNumber--;                    //��Ϊ����ѭ����Ҫ�� 1
				}
			}
			if(Currentidh!=lastidh)
			{
				System.out.println("**********lastidh");
				cursor = db.rawQuery("select * from user where number='"+lastNumber+"' and idh='"+lastidh+"'",null);
				if(cursor.moveToFirst())
				{
					int numColumn = cursor.getColumnIndex("X");
					String X = cursor.getString(numColumn);//  
					int xx=Integer.valueOf(X);				
					oldPts[i]= xx;
					i++;
		
					numColumn = cursor.getColumnIndex("Y");
					String Y = cursor.getString(numColumn);//  
					int yy=Integer.valueOf(Y);				
					oldPts[i]= yy;
					i++;
				}
			}
				
		}
			
		else 
		{
//			for()
		}
		for(int j=0;j<oldPts.length;j++)
			System.out.println("oldPts["+j+"]:"+oldPts[j]);

		matrix.postTranslate(-1*sX[0], -1*sY[0]);
		aX      =  sX[0];
		aY      =  sY[0];
		oldOffX = -1*sX[0];
		oldOffY = -1*sY[0];
		newOffX = oldOffX;
		newOffY = oldOffY;
		for(int giTemp=0; (giTemp<oldPts.length)&&(oldPts[giTemp]!=-1); giTemp++)
			if(giTemp%2 == 0)
       		 pts[giTemp]=oldPts[giTemp] * scale + newOffX; 
       	 else
       		 pts[giTemp]=oldPts[giTemp] * scale + newOffY ;
		System.out.println("sX:"+sX[0]);
		System.out.println("sY:"+sY[0]);
		System.out.println("dX:"+dX[0]);
		System.out.println("dY:"+dY[0]);
	}
		
/**************  variable of TextView  ****************/
//	TextView textViewFillInfo = new TextView(null);
//	testViewFillInfo=R.id.mapInformation;
	@SuppressLint("ResourceType")
	public DrawPath(Context father, AttributeSet as) {
		super(father,as);
		
		Resources res=this.getResources();
		System.out.println("DrawPath");
		// TODO Auto-generated constructor stub
     	BitmapFactory.Options opts = new BitmapFactory.Options();       //
     	opts.inJustDecodeBounds = true;                                 //�������Ƿ�ֹ�ڴ�й¶����ͼƬ̫���Ե��
     	if(firstTime)
     	{
     		System.out.println("fistTime");
     		map_bmp=BitmapFactory.decodeStream(getResources().openRawResource(nuaa));
            // map_bmp=BitmapFactory.decodeStream(getResources().openRawResource(nuaa), null,opts);
     		begin_bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.begin));
    		ardrone   = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.ardrone));
     		end_bmp = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.end));
     		firstTime=false;
     	}

     	
		this.setOnTouchListener(this);                                  //���ô����¼�
		COMPONENT_WIDTH=this.getWidth();                                //��ȡview�Ŀ��
		COMPONENT_HEIGHT=this.getHeight();                              //��ȡview�ĸ߶�

		System.out.println("out of fistTime");
//		int picWidth=map_bmp.getWidth();                                //ͼƬ�Ŀ��
        //int picWidth=1792;
//		int picHeight=map_bmp.getHeight();                              //ͼƬ�ĸ߶�
        //int picHeight=3072;
//		System.out.println("picWidth:"+ picWidth);
//		float startX=(COMPONENT_WIDTH-picWidth)/2+CurrentX;             //��ʼ��ͼ�����x����
//		float startY=(COMPONENT_HEIGHT-picHeight)/2+CurrentY;           //��ʼ��ͼ������y����
	    
		showJWD = (TextView) findViewById(R.id.showJWD);
		
		paint.setColor(Color.RED);                                      //���û���
		paint.setAlpha(255);
		linePaint.setColor(Color.BLUE);
		linePaint.setAlpha(255);
		linePaint.setStrokeWidth(2.0f);
		
//       matrix.postTranslate(40, -2550); 
		System.out.println("myDb");
		dbHelper = new LLB_DBUtil(getContext(), "myDb");
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		System.out.println("myDb");
		for(String s:sql)
		{
			db.execSQL(s);
		}
		System.out.println("after execSQL");
		Cursor cursor;
		cursor = db.rawQuery("select * from user", null);
		if(cursor !=null)
		{
			if(cursor.moveToFirst())
			{
				do{			
					int numColumn=cursor.getColumnIndex("name");
					String name =  cursor.getString(numColumn);
				}while(cursor.moveToNext());
			}
			cursor.close();
			cursor =null;
		}
		db.close();
		
		System.out.println("before getpath");
		getPath();
		
		System.out.println("after getpath");
		matrix_begin.setScale(0.1f*scale, 0.1f*scale);
		matrix_ardrone.setScale(0.1f*scale, 0.1f*scale);
 		matrix_end.setScale(0.1f*scale, 0.1f*scale);
		matrix_begin.postTranslate(newOffX+(sX[0]-16)*scale, (float) (newOffY+(sY[0]-46.6)*scale));	
		matrix_ardrone.postTranslate(newOffX+(aX-6)*scale, (float) (newOffY+(aY-6.6)*scale));
		matrix_end.postTranslate(newOffX+(dX[0]-16)*scale, (float) (newOffY+(dY[0]-46.6)*scale));
/**/
		new Thread(){
			public void run()
			{
				while(!Thread.currentThread().isInterrupted())
				{
					postInvalidate();
//					socket_client_receive();
//					showJWD.setText("jingweidu");
					try{
						sleep(10000);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}										
				}
			}	
		}.start();
		
	}
    /************************************************************************
     * @function:  draw the desired image , first draw the map ,then the path,
     *            at last ,the position of the owner.
     *            transform to the desired condition.
     * @input   :  none.
     * @return  :  none. but refresh the view.
     * @version :  2012.4.13 
     * @author  :  Andy
     **********************************************************************/
	public void onDraw(Canvas canvas)
	{
		/*
		 * aX aY �ļ��㡣��
		 * aX = 
		 */
		if(!( (aX>sX[0] && aX>dX[0])  ||  (aX<sX[0] && aX<dX[0])))
 		{
			aX = (float) (aX + (dX[0]-sX[0])*0.1);
			aY = (float) (aY + (dY[0]-sY[0])*0.1);
 		}
		else
		{
			aX = sX[0];
			aY = sY[0];
		}
//		matrix_ardrone.setScale(0.1f*scale,0.1f*scale);
//		matrix_ardrone.postTranslate(newOffX+(aX)*scale, (float) (newOffY+(aY)*scale));
		String result = null;
		System.out.println("aX = "+aX+"aY = "+aY);
		System.out.println("sX[0] = "+sX[0]+"sY[0] = "+sY[0]);
		System.out.println("dX[0]"+dX[0]+"dY[0] ="+dY[0]);
		System.out.println("onDraw");
//		matrix.postTranslate(-100, -100);
        // TODO Auto-generated method stub              
		//����DBHelper�Ķ��� 
		canvas.drawBitmap(map_bmp, matrix, paint);
		//�滭��ʼͼ��
		canvas.drawBitmap(begin_bmp, matrix_begin, paint);
		//�滭����ͼ��
		canvas.drawBitmap(end_bmp, matrix_end, paint);
		canvas.drawBitmap(ardrone, matrix_ardrone, paint);
		pathMan.moveTo(10, 330); 		
		pathMan.lineTo(30,330); 
		pathMan.lineTo(20,310); 
//		path.addCircle(10, 300, 60, Direction.CCW);
		pathMan.close(); 
		canvas.drawPath(pathMan, paint);
		linePaint.setStrokeWidth(2.0f*scale);
		drawLines(pts,canvas);
//		canvas.drawLine(pathStartX, pathStartY, pathDstX, pathDstY, linePaint);
		
//		canvas.drawBitmap(map_bmp, null, paint);	
	}


    /***********************************************************************
     * @function:  Multi_touch Event ,give the values of
     *            scale and OffX and OffY. Then the image would be
     *            transform to the desired condition.
     * @input   :  fingers touch the screen.
     * @return  :  none. but change the static value of scale and offX/Y.
     * @version :  2012.4.13 
     * @author  :  Andy
     **********************************************************************/
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		 ImageView view = (ImageView) v; 		
         switch (event.getAction() & MotionEvent.ACTION_MASK) {     
         case MotionEvent.ACTION_DOWN:     
        	 	 System.out.println("Action_down");
//                 matrix.set(view.getImageMatrix()); 
        	 	 System.out.println("OffX:"+oldOffX);
        	 	 System.out.println("OffY:"+oldOffY);
                 savedMatrix.set(matrix);     
                 start.set(event.getX(), event.getY());     
                 mode = DRAG;                         
                 break;     
         case MotionEvent.ACTION_POINTER_DOWN:  
        	     System.out.println("ACTION_POINTER_DOWN");
                 oldDist = spacing(event);     
                 if (oldDist > 10f) {     
                         savedMatrix.set(matrix);     
                         midPoint(mid, event);     
                         mode = ZOOM;     
                 }     
                 break;     
         case MotionEvent.ACTION_UP: 
        	     postInvalidate();
                 mode = NONE; 
                 break;  
                 
         case MotionEvent.ACTION_POINTER_UP: 
        	 	 ACTION_POINTER_UP = true;
        	 	 newDist = spacing(event);
        	 	 scale = (float) (newDist / oldDist);
                 if(scale>2.0f)                                       //������ֵ  0.1 -- 5
                	 scale=2.0f;
                 else if(scale<0.1f)
                	 scale=0.3f; 
                 matrix.setScale(scale, scale);
//                 newOffX = (newOffX-mid.x*scale) *scale/oldScale ;
//                 newOffY = (newOffY-mid.y*scale) *scale/oldScale;
//                 System.out.println("oldScale:"+oldScale);
//                 System.out.println("scale:"+scale);
                 newOffX = mid.x - (mid.x-oldOffX)*(scale/oldScale);
                 newOffY = mid.y - (mid.y-oldOffY)*(scale/oldScale); 
                 matrix.postTranslate(newOffX, newOffY);
        		 matrix_begin.setScale(0.1f*scale, 0.1f*scale);
        		 matrix_ardrone.setScale(0.1f*scale, 0.1f*scale);
         		 matrix_end.setScale(0.1f*scale, 0.1f*scale);
        		 matrix_begin.postTranslate(newOffX+(sX[0]-16)*scale, (float) (newOffY+(sY[0]-46.6)*scale));
        		 matrix_ardrone.postTranslate(newOffX+(aX-6)*scale, (float) (newOffY+(aY-6.6)*scale));
        		 matrix_end.postTranslate(newOffX+(dX[0]-16)*scale, (float) (newOffY+(dY[0]-46.6)*scale));

        	 	 postInvalidate();
        	 	 mode = NONE; 
        	 	 oldScale = scale;
        	 	 oldOffX = newOffX;
        	 	 oldOffY = newOffY;
        	 	 break;   
                
         case MotionEvent.ACTION_MOVE:     
                 if (mode == DRAG) {   
 //                        matrix.set(savedMatrix);
/*                  if(scale<=0.8)
                  {
                         if(( (  event.getX() - start.x)*0.3f + oldOffX <  200f) 
                          && ((  event.getX() - start.x )*0.3f + oldOffX> -1000f))
                         newOffX =(  event.getX() - start.x )*0.3f + oldOffX;           //̫�����ˣ����Ըĳ�0.3.��ԭ�л���������
                         else if((  event.getX() - start.x)*0.3f + oldOffX>200f)
                        	 newOffX = 200f;
                         else if((  event.getX() - start.x)*0.3f + oldOffX < -1000f)
                        	 newOffX = -1000f;
                         
                         if((( event.getY()  - start.y )*0.3f + oldOffY <  200f) 
                                 && ((event.getY()  - start.y )*0.3f + oldOffY> -2000f))
                        	 	newOffY =( event.getY() - start.y )*0.3f + oldOffY;     //̫�����ˣ����Ըĳ�0.3
                         else if(( event.getY()  - start.y )*0.3f + oldOffY>200f)
                               	 newOffY = 20f;
                         else if((event.getY()  - start.y )*0.3f + oldOffY < -2000f)
                               	 newOffY = -2000f; 
                  }
                  else 
                  {
*/                	 
                      newOffX =(  event.getX() -  start.x )*0.3f + oldOffX;           //̫�����ˣ����Ըĳ�0.3.��ԭ�л���������
                	  newOffY =(  event.getY() -  start.y )*0.3f + oldOffY;     //̫�����ˣ����Ըĳ�0.3
//                  }
                	     System.out.println ("newOffX:"+newOffX) ;
                	     System.out.println ("newOffY:"+newOffY) ;
                	     System.out.println ("Scale:"+scale);
                         matrix.setScale(scale, scale);
                         matrix.postTranslate(newOffX, newOffY);  
                 		 matrix_begin.setScale(0.1f*scale, 0.1f*scale);
                 		 matrix_ardrone.setScale(0.1f*scale, 0.1f*scale);
                 		 matrix_end.setScale(0.1f*scale, 0.1f*scale);
                		 matrix_begin.postTranslate(newOffX+(sX[0]-16)*scale, (float) (newOffY+(sY[0]-46.6)*scale));
                		 matrix_ardrone.postTranslate(newOffX+(aX-6)*scale, (float) (newOffY+(aY-6.6)*scale));
                		 matrix_end.postTranslate(newOffX+(dX[0]-16)*scale, (float) (newOffY+(dY[0]-46.6)*scale));
                         oldOffX = newOffX;
                         oldOffY = newOffY; 
                         
                 } 
                 else if (mode == ZOOM) 
                 {     
 /*                        newDist = spacing(event);  
                         if(event.getX(0)>event.getX(1))
                        	 eventSmallX= event.getX(1);
                         else 
                        	 eventSmallX= event.getX(0);

                         if(event.getY(0)>event.getY(1))
                        	 eventSmallY= event.getY(1);
                         else 
                        	 eventSmallY= event.getY(0);                        
                         if (newDist > 10f)
                         { 
                        	 	 System.out.println("mode = ZOOM");
                                 matrix.set(savedMatrix);     
                                 scale = (newDist / oldDist) * oldScale;                                                               
                                 if(scale>5.0f)                                       //������ֵ  0.1 -- 5
                                	 scale=3.0f;
                                 else if(scale<0.1f)
                                	 scale=0.3f; 
                                 matrix.setScale(scale, scale);
                                 oldScale = scale;   

                         }     
    */               }                  
               break;               
         }                                                              //�������
         for(giTemp=0; (giTemp<pts.length)&&(oldPts[giTemp]!=-1); giTemp++)
        	 if(giTemp%2 == 0)
        		 pts[giTemp]=oldPts[giTemp] * scale + newOffX; 
        	 else
        		 pts[giTemp]=oldPts[giTemp] * scale + newOffY ;
/*         pathStartX = oldpathStartX * scale  +  newOffX + eventSmallX;                // scale    0        newOffX |
         pathStartY = oldpathStartY * scale  +  newOffY + eventSmallY;                // 0       scale     newOffY |
         pathDstX   = oldpathDstX   * scale  +  newOffX + eventSmallX;                // 0        0         1      | 
         pathDstY   = oldpathDstY   * scale  +  newOffY + eventSmallY;                
*/         
         return true;     
	}
	private double spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);     
		float y = event.getY(0) - event.getY(1);     
		return  sqrt(x * x + y * y);
	}     

	private void midPoint(PointF point, MotionEvent event) {     
		float x = event.getX(0) + event.getX(1);     
		float y = event.getY(0) + event.getY(1);     
		point.set(x / 2, y / 2);     
	} 
    /*
	/**************************************************************
     *  function: draw the lines that the following line'head connect the line before.
     *            that is draw lines by the pts[] 1234 3456 5678 (12)_____(34)
     *            transform to the desired condition.                     |
     * @input   :  fingers touch   the screen        .                    (78)______|(56)
     * @return  :  none.                                          
     * @version :  2012.4.13 
     * @author  :  Andy
     **********************************************************************/
	public void drawLines(float [] pts,Canvas canvas)
	{
		int temp = 0;
		for(temp=0; (temp<pts.length-3)&&(pts[temp+2]!=-1);temp=temp+2)
			canvas.drawLine(pts[temp], pts[temp+1], pts[temp+2], pts[temp+3], linePaint);
	}
}
	
