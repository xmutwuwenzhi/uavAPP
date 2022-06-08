#include <cv.h>
#include <highgui.h>

/*****************************************************************************/
/*
\将检测到的点标注出来
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

