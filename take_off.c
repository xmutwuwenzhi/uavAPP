#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <android/log.h>
#include <time.h>

/* A.R.Drone OS dependant includes */
#include <config.h>
//#include <VP_Os/vp_os_print.h>
#include <VP_Os/vp_os_malloc.h>
#include <VP_Os/vp_os_delay.h>
#include <VP_Os/vp_os_types.h>

/* A.R.Drone Video API includes */
#include <VP_Api/vp_api.h>
#include <VP_Api/vp_api_error.h>
#include <VP_Api/vp_api_stage.h>
#include <VP_Api/vp_api_picture.h>
#include <VP_Stages/vp_stages_io_file.h>
#include <VP_Stages/vp_stages_i_camif.h>
#include <VLIB/Stages/vlib_stage_decode.h>
#include <VP_Stages/vp_stages_yuv2rgb.h>
#include <VP_Stages/vp_stages_buffer_to_picture.h>

/* A.R.Drone Tool includes */
#include <ardrone_tool/ardrone_tool.h>
#include <ardrone_tool/Com/config_com.h>

/* Configuration file */


#include <ardrone_api.h>
#include <take_off.h>
#include <navdata.h>


DEFINE_THREAD_ROUTINE(take_off, data)
{
	C_RESULT  res = C_OK;
	C_RESULT  AT_time=30;
	int32_t   i;
	uint32_t  take_off_data = 290718208;  //The command of the ARDrone taking off;
	uint32_t     land = 290717696;
	//ARDrone taking off command;
	//ardrone_at_set_ui_value(take_off);
	//vp_os_delay(AT_time);
	//After the ARDrone taking off,should send the flat-trim command;
	//ardrone_at_set_flat_trim();
	//vp_os_delay(AT_time);
	//__android_log_print(ANDROID_LOG_INFO, "ARDrone", "in the take_off thread");
	for(i=0;i<200;i++)
	{
		ardrone_at_set_ui_value(take_off_data);
		vp_os_delay(AT_time);
	}


//	START_THREAD(ARDrone_control, 0);
	__android_log_print(ANDROID_LOG_INFO, "ARDrone", "out of  the take_off thread");

	vp_os_delay(AT_time);

	//ARDrone land command;
	ardrone_at_set_ui_value(land);
	return res;
}
