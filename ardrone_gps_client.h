#ifndef _ARDRONE_GPS_CLIENT_H_
#define _ARDRONE_GPS_CLIENT_H_

#include <VP_Os/vp_os_types.h>
#include <VP_Api/vp_api_thread_helper.h>

#include <ardrone_api.h>
#include <config.h>

#define GPS_PORT   6666
#define COM_GPS()             wifi_com()
#define COM_CONFIG_GPS()      wifi_config()
#define COM_CONNECTION_GPS()  wifi_connection()
#define COM_CONFIG_SOCKET_GPS(socket, type, opt, serverhost)

#define GPS_MAX_RETRIES	5
#define GPS_MAX_SIZE		1024
// Facility to declare a set of gps handler
// Handler to resume control thread is mandatory
#define BEGIN_GPS_HANDLER_TABLE \
  ardrone_gps_handler_t ardrone_gps_handler_table[] = {

#define END_GPS_HANDLER_TABLE					\
  { NULL, NULL, NULL, NULL }						\
};

#define GPS_HANDLER_TABLE_ENTRY( init, process, release, init_data_ptr ) \
  { (ardrone_gps_handler_init_t)init, process, release, init_data_ptr },

typedef C_RESULT (*ardrone_gps_handler_init_t)( void* data );
typedef C_RESULT (*ardrone_gps_handler_process_t)( const char* const gpsdata );
typedef C_RESULT (*ardrone_gps_handler_release_t)( void );

typedef struct _ardrone_gps_handler_t {
  ardrone_gps_handler_init_t    init;
  ardrone_gps_handler_process_t process;
  ardrone_gps_handler_release_t release;

  void*                             data; // Data used during initialization
} ardrone_gps_handler_t;

extern ardrone_gps_handler_t ardrone_gps_handler_table[] WEAK;

uint32_t ardrone_gps_client_get_num_retries(void);
C_RESULT ardrone_gps_client_init(void);
C_RESULT ardrone_gps_client_shutdown(void);
uint32_t ardrone_gps_compute_cks( uint8_t* nv, int32_t size );


PROTO_THREAD_ROUTINE( gps_update , nomParams );

#endif // _ARDRONE_GPS_CLIENT_H_
