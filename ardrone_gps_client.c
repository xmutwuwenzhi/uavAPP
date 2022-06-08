/**************************************
*	Navdata�߳����ļ�
*  �߳��õ�����Ҫ�����̻߳ص����ȵ�
**************************************/
#include <errno.h>
#include <string.h>
#include <android/log.h>
#include <config.h>
#include <android/log.h>

#include <VP_Os/vp_os_print.h>
#include <VP_Com/vp_com.h>

#include <ardrone_api.h>
#include <ardrone_tool/ardrone_tool.h>
#include <ardrone_gps_client.h>
#include <ardrone_tool/Com/config_com.h>

#include <config_com.h>


#ifndef _WIN32
#include <sys/socket.h>
#include <sys/ioctl.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <unistd.h>
#endif

static bool_t bContinue = TRUE; 
static uint32_t num_retries = 0;   
static vp_os_cond_t gps_client_condition; 
static vp_os_mutex_t gps_client_mutex; 

static vp_com_socket_t gps_socket;
static Read gps_read      = NULL;
static Write gps_write    = NULL;

static uint8_t gps_buffer[GPS_MAX_SIZE];

C_RESULT ardrone_gps_client_init(void)
{
  C_RESULT res;
  __android_log_print(ANDROID_LOG_INFO, "ARDrone", "in the ardrone_gps_client_init");

  COM_CONFIG_SOCKET_NAVDATA(&gps_socket, VP_COM_CLIENT, GPS_PORT, wifi_ardrone_ip);
  gps_socket.protocol = VP_COM_UDP;
  gps_socket.is_multicast = 1;      // enable multicast for gps
  gps_socket.multicast_base_addr = MULTICAST_BASE_ADDR;

  vp_os_mutex_init(&gps_client_mutex);
  vp_os_cond_init(&gps_client_condition, &gps_client_mutex);
	
  res = C_OK;

  return res;
}

DEFINE_THREAD_ROUTINE( gps_update, nomParams )    
{
	C_RESULT res = C_OK;
	struct timeval tv;
	int32_t i, size;

	__android_log_print(ANDROID_LOG_INFO, "ARDrone" ,"Thread gps_update in progress...\n");
#ifdef _WIN32
	int timeout_for_windows=1000/*milliseconds*/;
#endif
	tv.tv_sec   = 1/*second*/;
	tv.tv_usec  = 0;
	if( VP_FAILED(vp_com_open(COM_GPS(), &gps_socket, &gps_read, &gps_write)) )
	{
		__android_log_print(ANDROID_LOG_INFO, "ARDrone" ,"VP_Com : Failed to open socket for gps!\n");
		res = C_FAIL;
	}
	if( VP_SUCCEEDED(res) )
	{
		__android_log_print(ANDROID_LOG_INFO, "ARDrone" ,"Thread gps_update in progress...\n");
//		PRINT("Thread gps_update in progress...\n");
#ifdef _WIN32
		setsockopt((int32_t)gps_socket.priv, SOL_SOCKET, SO_RCVTIMEO, (const char*)&timeout_for_windows, sizeof(timeout_for_windows));
		/* Added by Stephane to force the drone start sending data. */
		if(gps_write)
		{	int sizeinit = sizeof("start now"); gps_write( (void*)&gps_socket, (int8_t*)"start now", &sizeinit ); }
#else
		setsockopt((int32_t)gps_socket.priv, SOL_SOCKET, SO_RCVTIMEO, (const char*)&tv, sizeof(tv));
#endif

		i = 0;
		while( ardrone_gps_handler_table[i].init != NULL )
		{
		// if init failed for an handler we set its process function to null
		// We keep its release function for cleanup
			if( VP_FAILED( ardrone_gps_handler_table[i].init(ardrone_gps_handler_table[i].data) ) )
			ardrone_gps_handler_table[i].process = NULL;

			i ++;
		}
		while( !ardrone_tool_exit() 
           && bContinue /*&& (num_retries<=GPS_MAX_RETRIES)*/)
		{
			//vp_os_memset(gps_buffer, 0, GPS_MAX_RETRIES);
			size = GPS_MAX_SIZE;
			vp_os_memset(gps_buffer, 0, GPS_MAX_SIZE);
			
				usleep(500000);
				res = gps_read( (void*)&gps_socket, (int8_t*)&gps_buffer[0], &size );
#ifdef _WIN32	
				if( size <= 0 )
#else
				if( size == 0 )
#endif
				{
					// timeout
					DEBUG_PRINT_SDK("Timeout when reading gpsdata - resending a gps request on port %i\n",GPS_PORT);
					/* Resend a request to the drone to get navdatas */
					if(gps_write)
					{	
						int sizeinit = sizeof("start now"); 
						gps_write( (void*)&gps_socket, (int8_t*)"start now", &sizeinit ); 
					}
					//num_retries++;
				} 
				//else
					//num_retries = 0;
				
				if( VP_SUCCEEDED( res ) )
				{
					i = 0;

					while( ardrone_gps_handler_table[i].init != NULL )
					{
						if( ardrone_gps_handler_table[i].process != NULL )
						ardrone_gps_handler_table[i].process( &gps_buffer[0] );
						//Sleep(1000);
						i++;
					}
				}

		}
	
		// Release resources alllocated by handlers
		i = 0;
		while( ardrone_gps_handler_table[i].init != NULL )
		{
			ardrone_gps_handler_table[i].release();

			i ++;
		}

		/*if(gps_write)
		{	int sizeend = sizeof("client shutdown"); gps_write( (void*)&gps_socket, (int8_t*)"client shutdown", &sizeend );
		//DEBUG_PRINT_SDK("gps end cmd sended\n");
		}*/
	}
	
	vp_com_close(COM_GPS(), &gps_socket);

	DEBUG_PRINT_SDK("Thread gps_update ended\n");

	return (THREAD_RET)res;
}

uint32_t ardrone_gps_client_get_num_retries(void)
{
  return num_retries;
}

C_RESULT ardrone_gps_client_shutdown(void)
{
   bContinue = FALSE;
   return C_OK;
}

uint32_t ardrone_gps_compute_cks( uint8_t* nv, int32_t size )
{
	uint32_t cks = 0;
	return cks;
}
