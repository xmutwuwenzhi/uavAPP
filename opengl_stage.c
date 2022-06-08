/*
 *  opengl_stage.c
 *  Test
 *
 *  Created by Karl Leplat on 22/02/10.
 *  Copyright 2010 Parrot SA. All rights reserved.
 *
 */
#include "opengl_stage.h"
#include "app.h"
#include <android/log.h>

#include <cv.h>
#include <highgui.h>
C_RESULT my_rgb24(vp_api_picture_t *in,uint8_t *dst);
C_RESULT my_rgb565(vp_api_picture_t *picture,uint8_t *dst);

PIPELINE_HANDLE pipeline_handle;  //¹ÜÏßŸä±ú
static uint8_t  pixbuf_data[2500000] ;	//Ã¿žöSTAGEµÄÊä³öÊýŸÝ¶ŒŽæŽ¢ÔÚÕâÀï£¬ÈçœâÂëºó£¬ŽæŽ¢Ò»Ö¡YUVÍŒÏñ£¬×ª»»žñÊœºóÎªÒ»Ö¡RGBÍŒÏñ
static uint8_t  pixbuf_data565[250000];
static vp_os_mutex_t  video_update_lock; //Ïß³Ì»¥³âÁ¿
int b_H_land;
int32_t channel;

static	CvMemStorage* h_storage_standard;
static	CvSeq* h_contour_standard;

CvPoint pic_position;
CvPoint bod_position;
//#define VP_STAGES_YUV2ARGB_LIMIT(x)  ( (x) > 0xffff ? 0xff : ( (x) <= 0xff ? 0 : ( (x) >> 8 ) ) )
#define VP_STAGES_YUV2ARGB_LIMIT(dst, x) \
  dst = x; \
  dst = (dst > 0 ? ( ((dst) >> 8) > 0xff ? 0xff : ((dst) >> 8) ) : 0);

/**
 *  @def      VP_STAGES_YUV2RGB_SAT5U(a)
 *  @brief    5 bits saturation
 */
#define VP_STAGES_YUV2RGB_SAT5U(a)  \
  if((a) < 0) (a) = 0;              \
  else if((a) > 0x1F) (a) = 0x1F;

/**
 *  @def      VP_STAGES_YUV2RGB_SAT6U(a)
 *  @brief    6 bits saturation
 */
#define VP_STAGES_YUV2RGB_SAT6U(a)  \
  if((a) < 0) (a) = 0;              \
  else if((a) > 0x3F) (a) = 0x3F;
CvSeq* get_standard_H_contour(void)
{
	CvSeq *h_seq_standard, *h_seq_tmp_standard;
	/********************
			   Ô€ŽŠÀí
	*********************/
	IplImage* pImg_standard;// ÍíÉÏÒÔ8Îª±ê×Œ, °×ÌìÒÔ15Îª±ê×Œ
	IplImage* pGray_standard;
	IplImage* pGray_edge_standard;
	CvSeq* circles_standard;
	CvMemStorage* storage_standard;
	int i = 0, j = 0, max_index_standard= 0;;

	pImg_standard = cvLoadImage("/sdcard/28.jpg", 1);
	pGray_standard = cvCreateImage(cvGetSize(pImg_standard), IPL_DEPTH_8U, 1);
	pGray_edge_standard = cvCreateImage(cvGetSize(pGray_standard), IPL_DEPTH_8U, 1);
	h_storage_standard = cvCreateMemStorage(0);
	storage_standard = cvCreateMemStorage(0);
	cvCvtColor( pImg_standard, pGray_standard, CV_BGR2GRAY );
	cvSmooth( pGray_standard, pGray_standard, CV_GAUSSIAN, 5, 5, 0, 0);  //¿ÉÒÔÅÅ³ýžÉÈÅ, 5x5Ž°¿ÚµÄžßË¹ÂË²š

	//¿ÉÒÔ¿ŒÂÇÈ¡ÏûÍâÈŠµÄŒì²â, µœÊ±ºò±ÈœÏÁœÖÖ·œ·šµÄËÙÂÊ...  1.ÍâÈŠËõÐ¡·¶Î§, ÔÚŒì²âH   2.Ö±œÓŒì²âH, ÃâÈ¥Œì²âÍâÈŠµÄŒÆËã
	circles_standard = cvHoughCircles( pGray_standard, storage_standard, CV_HOUGH_GRADIENT, 1, pGray_standard->height/2, 200, 100, 0, 0 );

    for( i = 0; i < circles_standard->total; i++ )
    {
		float* p_standard;

		double area_standard = 0;

		//ŒÆËãÖÐÐÄŸØÐÎÇøÓò
		CvPoint pa_standard;
		CvPoint pb_standard;
		CvRect rect_roi_standard;

		p_standard = (float*)cvGetSeqElem( circles_standard, i );

		pa_standard = cvPoint(cvRound(p_standard[0]-(4.5/7.0)*p_standard[2]), cvRound(p_standard[1]-(4.5/7.0)*p_standard[2]));
		pb_standard = cvPoint(cvRound(p_standard[0]+(4.5/7.0)*p_standard[2]), cvRound(p_standard[1]+(4.5/7.0)*p_standard[2]));
		rect_roi_standard = cvRect(pa_standard.x, pa_standard.y, cvRound((9.0/7.0)*p_standard[2]), cvRound((9.0/7.0)*p_standard[2]));

		//ÉèÖÃROIÎªÖÐÐÄŸØÐÎ
		cvSetImageROI(pGray_standard, rect_roi_standard);
		//œ«ROIÇøÓò¶þÖµ»¯
		cvAdaptiveThreshold(pGray_standard, pGray_standard, 255,
			CV_ADAPTIVE_THRESH_MEAN_C,
			CV_THRESH_BINARY_INV, 5, 5);//×ÔÊÊÓŠãÐÖµ, ÆœŸùŒÓÈš, 5*5Ž°¿Ú
		//œ«HÐÍÁ¬œÓÆðÀŽ
		cvDilate(pGray_standard, pGray_standard, 0, 1);

		cvZero(pGray_edge_standard);
		cvSetImageROI(pGray_edge_standard, rect_roi_standard);

		cvResetImageROI(pGray_standard);

		cvSetImageROI(pGray_standard, rect_roi_standard);

		//ÔÚROIÇøÓòÖÐÕÒ³öµÄÂÖÀª
		cvFindContours(pGray_standard, h_storage_standard, &h_seq_standard, sizeof(CvContour),
			CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));//»ñÈ¡ROIÇøÓòÖÐµÄÍâÂÖÀª...

		/*Ãæ»ýÉžÑ¡(¿ŒÂÇÌážßŽúÂëÐ§ÂÊ)*/
		if(h_seq_standard)
		{
			h_seq_tmp_standard = h_seq_standard;
			//»ñÈ¡×îŽóÇøÓòµÄË÷Òý
			while( h_seq_tmp_standard )
			{
				double temp_standard;
				temp_standard = cvContourArea(h_seq_tmp_standard, cvSlice(0,1073741823), 0);
				if(temp_standard>=area_standard)
				{
					area_standard = temp_standard;
					max_index_standard = j;
				}
				h_seq_tmp_standard = h_seq_tmp_standard->h_next;
				j++;
			}

			//»æÖÆ×îŽóÃæ»ýµÄÂÖÀª
			for(j=0; j<max_index_standard; j++)
				h_seq_standard = h_seq_standard->h_next;
			cvDrawContours(pGray_edge_standard, h_seq_standard, CV_RGB(255, 255, 255),
				CV_RGB(127, 127, 127), -1, CV_FILLED, 8, cvPoint(0,0));
		}
		else
		{
			cvResetImageROI(pGray_standard);
			cvResetImageROI(pGray_edge_standard);

			cvReleaseImage(&pImg_standard);
			cvReleaseImage(&pGray_standard);
			cvReleaseImage(&pGray_edge_standard);

			return NULL; //Ã»ÓÐÕÒµœH
		}
		cvResetImageROI(pGray_standard);
		cvResetImageROI(pGray_edge_standard);
    }

	cvReleaseMemStorage(&storage_standard);

	cvReleaseImage(&pImg_standard);
	cvReleaseImage(&pGray_standard);
	cvReleaseImage(&pGray_edge_standard);

	return h_seq_standard;
}

/*****************************************************************************/
/*
\±ÈœÏÊÓÆµÖ¡Óë±ê×ŒHÂÖÀª, ÌáÈ¡Ö¡ÖÐµÄHÂÖÀªÎ»ÖÃ.
*/
CvPoint get_H_position(CvSeq* standard_contour, IplImage*  img)
{
	CvSeq *h_seq_detect, *h_seq_tmp_detect;
	int i = 0, nearest_index;
	double least_diffrence = 1000;

	IplImage* pImg_detect;
	IplImage* pGray_detect;
	IplImage* pGray_edge_detect;
	CvMemStorage* h_storage_detect;
	CvMemStorage* storage_center;
	CvPoint pt;
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "Enter in get_H_position\n" );
	pImg_detect = img;
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "before pGray_detect in get_H_position\n" );
	pGray_detect = cvCreateImage(cvGetSize(pImg_detect), IPL_DEPTH_8U, 1);
	pGray_edge_detect = cvCreateImage(cvGetSize(pGray_detect), IPL_DEPTH_8U, 1);
	 h_storage_detect = cvCreateMemStorage(0);
//		__android_log_print( ANDROID_LOG_INFO, "ARDrone", "before cvCvtColor in get_H_position\n" );
	cvCvtColor( pImg_detect, pGray_detect, CV_BGR2GRAY );
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "before cvSmooth in get_H_position\n" );
	cvSmooth( pGray_detect, pGray_detect, CV_GAUSSIAN, 5, 5, 0, 0);  //¿ÉÒÔÅÅ³ýžÉÈÅ, 5x5Ž°¿ÚµÄžßË¹ÂË²š
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "before cvAdaptiveThreshold in get_H_position\n" );
	//¶þÖµ»¯
	cvAdaptiveThreshold(pGray_detect, pGray_detect, 255,
		CV_ADAPTIVE_THRESH_MEAN_C,
		CV_THRESH_BINARY_INV, 5, 5);//×ÔÊÊÓŠãÐÖµ, ÆœŸùŒÓÈš, 5*5Ž°¿Ú
	//œ«HÐÍÁ¬œÓÆðÀŽ
	cvDilate(pGray_detect, pGray_detect, 0, 1);

	//ÕÒ³öµÄÂÖÀª
	cvFindContours(pGray_detect, h_storage_detect, &h_seq_detect, sizeof(CvContour),
		CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));//»ñÈ¡ROIÇøÓòÖÐµÄÍâÂÖÀª...
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "before if in get_H_position\n" );
	/*Ïà·û¶ÈÉžÑ¡(¿ŒÂÇÌážßŽúÂëÐ§ÂÊ)*/
	if(h_seq_detect)
	{
		h_seq_tmp_detect = h_seq_detect;
		while( h_seq_tmp_detect )
		{
			double n_tmp_detect = cvMatchShapes(h_seq_tmp_detect, h_contour_standard, CV_CONTOURS_MATCH_I1, 0);
			if((n_tmp_detect<least_diffrence) && (n_tmp_detect>0.02))
			{
				least_diffrence = n_tmp_detect;
				nearest_index = i;
//				 __android_log_print( ANDROID_LOG_INFO, "ARDrone", "least_difference=%f,nearest_index=%d",least_diffrence,nearest_index);
			}
			h_seq_tmp_detect = h_seq_tmp_detect->h_next;
			i++;
		}

		if(least_diffrence == 1000)
		{
//			__android_log_print(ANDROID_LOG_INFO,"ARDrone","Cannot detect any H in this frame!\n");
			//printf("Cannot detect any H in this frame!\n");
			cvReleaseMemStorage(&h_storage_detect);

			cvReleaseImage(&pGray_detect);
			cvReleaseImage(&pGray_edge_detect);
			return cvPoint(0,0);
		}
		else if(least_diffrence>0.35)
		{
//			__android_log_print(ANDROID_LOG_INFO,"ARDrone","cannot detect any valid H in this frame!\n");
			//printf("Cannot detect any valid H in this frame!\n");
			cvReleaseMemStorage(&h_storage_detect);

			cvReleaseImage(&pGray_detect);
			cvReleaseImage(&pGray_edge_detect);
			return cvPoint(0,0);
		}
		else
		{
			double m00, m01, m10, inv_m00;
			int xc, yc;
			CvMat* region;
			CvMoments moments;

			//»æÖÆÓëH×îœÓœüµÄÂÖÀª, ²¢ŒÆËãÐÎÐÄ
			for(i=0; i<nearest_index; i++)
				h_seq_detect = h_seq_detect->h_next;

			//printf("H detected in 'H d edge!'\n");

			storage_center = cvCreateMemStorage(0);
			h_seq_detect = cvApproxPoly( h_seq_detect, sizeof(CvContour),
				storage_center, CV_POLY_APPROX_DP, 3, 1 );

			region=(CvMat*)h_seq_detect;
			cvMoments( region, &moments,0 );

			//Ò»œ×ŸØŒŽÐÎÐÄ
			m00 = moments.m00;
			m10 = moments.m10;
			m01 = moments.m01;

			inv_m00 = 1. / m00;
			xc = cvRound( m10 * inv_m00 );
			yc = cvRound( m01 * inv_m00 );

			pt.x=xc;
			pt.y=yc;

			//add the global variable;
			pic_position.x=xc;
			pic_position.y=yc;

			cvReleaseMemStorage(&storage_center);
		}
	}
	else
	{
//		__android_log_print(ANDROID_LOG_INFO,"ARDrone","cannot find any contours!");
		//printf("Cannot find any contours!\n"); //Ã»ÓÐÕÒµœH

		cvReleaseMemStorage(&h_storage_detect);

		cvReleaseImage(&pGray_detect);
		cvReleaseImage(&pGray_edge_detect);
		return cvPoint(0,0);
	}

	cvReleaseMemStorage(&h_storage_detect);

	cvReleaseImage(&pGray_detect);
	cvReleaseImage(&pGray_edge_detect);
	return pt;
}

/*****************************************************************************/
/*
\œ«Œì²âµœµÄµã±ê×¢³öÀŽ
*/
void fix_H_position(CvPoint pt, IplImage* img)
{

	CvPoint pt1,pt2;
	pt1.x=pt.x-15;
	pt1.y=pt.y;
	pt2.x=pt.x+15;
	pt2.y=pt.y;
	cvLine( img, pt1, pt2, CV_RGB(0,255,0), 1, CV_AA, 0 );

	pt1.x=pt.x;
	pt1.y=pt.y-15;
	pt2.x=pt.x;
	pt2.y=pt.y+15;
	cvLine( img, pt1, pt2, CV_RGB(0,255,0), 1, CV_AA, 0 );
}

static opengl_video_stage_config_t opengl_video_config;

const vp_api_stage_funcs_t opengl_video_stage_funcs = {
	(vp_api_stage_handle_msg_t) NULL,
	(vp_api_stage_open_t) opengl_video_stage_open,
	(vp_api_stage_transform_t) opengl_video_stage_transform,
	(vp_api_stage_close_t) opengl_video_stage_close
};



C_RESULT opengl_video_stage_open(vlib_stage_decoding_config_t *cfg)
{
	IplImage* img = cvCreateImage(cvSize(176, 144),8,1);
//    __android_log_print( ANDROID_LOG_INFO, "ARDrone", "Enter in opengl_video_stage_open\n" );
	vp_os_mutex_init( &opengl_video_config.mutex );
//	ardrone_at_set_toy_configuration("video:video_channel", "1");
	
	vp_os_mutex_lock( &opengl_video_config.mutex );
	opengl_video_config.data = vp_os_malloc(VIDEO_HEIGHT * VIDEO_WIDTH * 4);
	vp_os_memset(opengl_video_config.data, 0x0, VIDEO_HEIGHT * VIDEO_WIDTH * 4);
	vp_os_mutex_unlock( &opengl_video_config.mutex );

	h_contour_standard = get_standard_H_contour();
	if(!h_contour_standard)
		;
	//		exit(-101);   //»ñµÃ±ê×ŒHÂÖÀªÊ§°Ü
//   __android_log_print( ANDROID_LOG_INFO, "ARDrone", "Exit opengl_video_stage_open\n" );
	
	return C_OK;
}

C_RESULT opengl_video_stage_transform(vlib_stage_decoding_config_t *cfg, vp_api_io_data_t *in, vp_api_io_data_t *out)
{
	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "Enter opengl_video_stage_transform\n" );
	my_rgb24(cfg->picture,&pixbuf_data);
//	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "After my_rgb24\n" );
	IplImage * pOriImage,*pDstImage;
	CvRect rect;
	CvPoint pt;

	pOriImage = cvCreateImage(cvSize(cfg->picture->width, cfg->picture->height), 8, 3);

	if(channel == 0)
	{
			rect = cvRect(0, 0, 320, 240);
			pDstImage = cvCreateImage(cvSize(320, 240), 8, 3);
	}
	else if(channel==1)
	{
			rect = cvRect(0, 0, 176, 144);
			pDstImage = cvCreateImage(cvSize(176, 144), 8, 3);
	}

//	pixbuf_data=cfg->picture->y_buf;  //Ò»Ö¡œâÂëµÄÍŒÆ¬
	pOriImage->imageData = &pixbuf_data;
    cvSaveImage("/sdcard/27.jpg", pOriImage, 0);
	cvSetImageROI(pOriImage, rect);
	cvCopy(pOriImage, pDstImage, 0);

	pt = get_H_position(h_contour_standard, pDstImage);

	if((pt.x==0)&&(pt.y==0))
		 //printf("no H in it.\n");
		 ;
	else
		{
				fix_H_position(pt, pDstImage);
				b_H_land = 1;
				__android_log_print(ANDROID_LOG_INFO,"ARDrone","find  H  here!!!");
				cvSaveImage("/sdcard/23.jpg", pDstImage, 0);
		}
		cvReleaseImage(&pOriImage);

	cvReleaseImage(&pDstImage);
	my_rgb565(cfg->picture,&pixbuf_data565);

	vp_os_mutex_lock( &out->lock );
	if(out->status == VP_API_STATUS_INIT)
	{
		out->status = VP_API_STATUS_PROCESSING;
	}
	
	if( in->status == VP_API_STATUS_ENDED ) 
	{
		out->status = in->status;
	}
	
	if(out->status == VP_API_STATUS_PROCESSING )
	{
		vp_os_mutex_lock( &opengl_video_config.mutex );
		
		if(cfg->num_picture_decoded > opengl_video_config.num_picture_decoded)
		{
			opengl_video_config.num_picture_decoded = cfg->num_picture_decoded;
			/*
         opengl_video_config.bytesPerPixel	= 2;
			opengl_video_config.widthImage		= cfg->controller.width;
			opengl_video_config.heightImage		= cfg->controller.height;		
			
			if(opengl_video_config.bytesPerPixel == 2)
			{
				opengl_video_config.format = GL_RGB;
				opengl_video_config.type = GL_UNSIGNED_SHORT_5_6_5;				
			}
		   */ 
			if (opengl_video_config.data != NULL)
			{   
//				__android_log_print( ANDROID_LOG_INFO, "ARDrone", "Enter opengl_video_config\n" );
				vp_os_memcpy(opengl_video_config.data, &pixbuf_data565, cfg->picture->width * cfg->picture->height );
//				vp_os_memcpy(opengl_video_config.data, cfg->picture, cfg->picture->width * cfg->picture->height );

			}
		
			out->numBuffers = in->numBuffers;
			out->indexBuffer = in->indexBuffer;
			out->buffers = in->buffers;
		}

		vp_os_mutex_unlock( &opengl_video_config.mutex );
	}
	vp_os_mutex_unlock( &out->lock );

	return C_OK;
}
C_RESULT my_rgb24(vp_api_picture_t *picture,uint8_t *dst)
{
	 uint32_t width, height;
	  static uint32_t bytesPerPixel = 0;
//	  __android_log_print( ANDROID_LOG_INFO, "ARDrone", "0000000000000000\n" );
	  int32_t line, col, linewidth;
	  int32_t y, u, v, r, g, b;
	  int32_t vr, ug, vg, ub;
	  uint8_t *py, *pu, *pv;
//	  __android_log_print( ANDROID_LOG_INFO, "ARDrone", "1111111111111111\n" );
	  bytesPerPixel = 3;

	  int32_t lineSz0 = picture->y_line_size;
	  int32_t lineSz1 = picture->cb_line_size;
	  int32_t lineSz2 = picture->cr_line_size;

	  width = picture->width;
	  height = picture->height;

	  linewidth = width - (width >> 1);

	  if(1)
	  {
// 		  __android_log_print( ANDROID_LOG_INFO, "ARDrone", "22222222222222222222\n" );

	        py = picture->y_buf;

	        pu = picture->cb_buf;

	        pv = picture->cr_buf;
//	        __android_log_print( ANDROID_LOG_INFO, "ARDrone", "********height=%d****,***wid th=%d\n",height,width);
//	        *pu = 0x80;
//	        *pv = 0x80;
	        for (line = 0; line < (int32_t)height; line++) {
	  	for (col = 0; col < (int32_t)width; col++) {
//	  	   __android_log_print( ANDROID_LOG_INFO, "ARDrone", "********height=%d****,***width=%d\n",height,width);
	  		  y   = *py;
	  	  y   = y << 8;
	  	  u   = *pu - 128;
	  	  ug  = 88 * u;

	  	  ub  = 454 * u;
	  	  v   = *pv - 128;
	  	  vg  = 183 * v;
	  	  vr  = 359 * v;
	  	  VP_STAGES_YUV2ARGB_LIMIT(r, y +      vr);
	  	  VP_STAGES_YUV2ARGB_LIMIT(g, y - ug - vg);
	  	  VP_STAGES_YUV2ARGB_LIMIT(b, y + ub     );
//	  	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "*line=%d****,***col=%d*****r=%d,g=%d,b=%d********",line,col,r,g,b);
	  	  *dst = b;
//	  	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "******bbbbbbbbbb*****");
	  	  dst++;
	  	  *dst = g;
//	  	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "******gggggggggg*****");
	  	  dst++;
	  	  *dst = r;
//	  	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "******rrrrrrrrrr*****");

	  	  dst++;
	  	  py++;
//	  	__android_log_print( ANDROID_LOG_INFO, "ARDrone", "******aaaaaaaaa******");
	  	  if (col & 1)
	  	    {
	  	      pu++;
	  	      pv++;
	  	    } // No else
	  	}

	  	pu -= linewidth;
	  	pv -= linewidth;

	  	if (line & 1)
	  	  {
	  	    pu += lineSz1;
	  	    pv += lineSz2;
	  	  } // No else
	  	py += lineSz0 - width;
	        }

	  }//end of if
//	  __android_log_print( ANDROID_LOG_INFO, "ARDrone", "4444444444444444\n" );
}

C_RESULT my_rgb565(vp_api_picture_t *picture,uint8_t *dst)
{
	  static uint32_t bytesPerPixel = 0;
//	  __android_log_print( ANDROID_LOG_INFO, "ARDrone", "0000000000000000\n" );
	  uint32_t dst_rbytes=0;
	  bytesPerPixel = 2;

	  uint32_t  w, width;
	  uint32_t  h, height;
	  width = picture->width >> 1;
	  height = picture->height >> 1;
	  dst_rbytes=width * bytesPerPixel;

	  if(1)
	  {

//	        __android_log_print( ANDROID_LOG_INFO, "ARDrone", "********height=%d****,***width=%d\n",height,width);
//	        *pu = 0x80;
//	        *pv = 0x80;
	        for (h = 0; h < height; h++)
	          {
	            uint8_t*  s0;
	            uint8_t*  s1;
	            uint8_t*  s2;
	            uint16_t* d0;
	            uint16_t* d1;

	            s0 = picture->y_buf  + (h * picture->y_line_size * 2);
	            s1 = picture->cb_buf + (h * picture->cb_line_size);
	            s2 = picture->cr_buf + (h * picture->cr_line_size);
	            d0 = (uint16_t*) (dst + (h * dst_rbytes * 2));
	            d1 = (uint16_t*) (dst + (h * dst_rbytes * 2) + dst_rbytes);

	            for (w = 0; w < width; w++)
	            {
	              int32_t   y, cb, cr;
	              int32_t   c_r, c_g, c_b;
	              int16_t   r, g, b;

	              cb = ((uint32_t) *(s1++)) - 0x80L;
	              cr = ((uint32_t) *(s2++)) - 0x80L;
	              c_r = cr * 359L;
	              c_g = (cb * -88L) + (cr * -183L);
	              c_b = cb * 454L;

	              y = ((uint32_t) s0[0]) << 8;
	              r = (int16_t) ((y + c_r + 0x000) >> 11);      VP_STAGES_YUV2RGB_SAT5U(r);
	              g = (int16_t) ((y + c_g + 0x000) >> 10);      VP_STAGES_YUV2RGB_SAT6U(g);
	              b = (int16_t) ((y + c_b + 0x000) >> 11);      VP_STAGES_YUV2RGB_SAT5U(b);
	              d0[0] = (r << 11) | (g << 5) | (b << 0);

	              y = ((uint32_t) s0[1]) << 8;
	              r = (int16_t) ((y + c_r + 0x400) >> 11);      VP_STAGES_YUV2RGB_SAT5U(r);
	              g = (int16_t) ((y + c_g + 0x200) >> 10);      VP_STAGES_YUV2RGB_SAT6U(g);
	              b = (int16_t) ((y + c_b + 0x400) >> 11);      VP_STAGES_YUV2RGB_SAT5U(b);
	              d0[1] = (r << 11) | (g << 5) | (b << 0);

	              y = ((uint32_t) s0[picture->y_line_size + 0]) << 8;
	              r = (int16_t) ((y + c_r + 0x600) >> 11);      VP_STAGES_YUV2RGB_SAT5U(r);
	              g = (int16_t) ((y + c_g + 0x300) >> 10);      VP_STAGES_YUV2RGB_SAT6U(g);
	              b = (int16_t) ((y + c_b + 0x600) >> 11);      VP_STAGES_YUV2RGB_SAT5U(b);
	              d1[0] = (r << 11) | (g << 5) | (b << 0);

	              y = ((uint32_t) s0[picture->y_line_size + 1]) << 8;
	              r = (int16_t) ((y + c_r + 0x200) >> 11);      VP_STAGES_YUV2RGB_SAT5U(r);
	              g = (int16_t) ((y + c_g + 0x100) >> 10);      VP_STAGES_YUV2RGB_SAT6U(g);
	              b = (int16_t) ((y + c_b + 0x200) >> 11);      VP_STAGES_YUV2RGB_SAT5U(b);
	              d1[1] = (r << 11) | (g << 5) | (b << 0);

	              s0 += 2;
	              d0 += 2;
	              d1 += 2;
	            }
	          }
//	        __android_log_print( ANDROID_LOG_INFO, "ARDrone", "4444444444444444\n" );
	      }
}

C_RESULT opengl_video_stage_close(vlib_stage_decoding_config_t *cfg)
{
	vp_os_free(opengl_video_config.data);
	
	return C_OK;
}

opengl_video_stage_config_t* opengl_video_stage_get(void)
{
	return &opengl_video_config;
}
