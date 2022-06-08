#include <ardrone_gps_client.h>
#include <android/log.h>
#include <gps.h>

/*---------------------------------------------------------------------------------------------------------------------
Initialization local variables before event loop  
---------------------------------------------------------------------------------------------------------------------*/
inline C_RESULT demo_gps_client_init( void* data )
{
	/**	======= INSERT USER CODE HERE ========== **/
	/* Initialize your navdata handler */
	/**	======= INSERT USER CODE HERE ========== **/

  return C_OK;
}



//GPS��ݵ�ȫ�ֱ���
char gps_time[20]="" , lati[20]="", longti[20]="", angle[20]="";
//char *gps_time="" , *lati, *longti, *angle;
//float32_t angle_clbr;

//extern float32_t   ARDrone_phi;      //ARDrone roll in degrees;
//dextern float32_t   ARDrone_theta;    //ARDrone pitch in degrees;

//#define GPS_LINE 1
//#define ANGLE_LINE 2
/*---------------------------------------------------------------------------------------------------------------------
Navdata handling function, which is called every time navdata are received
---------------------------------------------------------------------------------------------------------------------*/
inline C_RESULT demo_gps_client_process( const char* const gpsdata)
{
	__android_log_print("ANDROID_LOG_INFO","ARDrone","GPS_CLIENT_PROCESS");

		/** ======= INSERT USER CODE HERE ========== **/
		static char line[100] = "";
		//char x[10]="", y[10]="", z[10]="";
		//float32_t fx = 0.0, fy = 0.0, fz = 0.0, fx_trans = 0.0, fy_trans = 0.0, fz_trans = 0.0, theta, phi;
		static int start = 0, dot = 0, line_ch = 0;
		int ch = 0,  i, dot_num = 0, bit = 0;
		uint32_t cks = 0, gpscks = 0, bit_a, bit_b;


		//¶ÔµÚÒ»×é»º³åÇøÖµœøÐÐÔ€ŽŠÀí£¬Ê×ÏÈÕÒµœ/n
    	if (start == 0)
		{
			for(ch = 0; gpsdata[ch]!='\0'; ch++)
			{
				if(ch>4)
					if(gpsdata[ch]=='A'&&gpsdata[ch-1]=='G'&&gpsdata[ch-2]=='G'&&gpsdata[ch-5]=='$')
					{		
						int i;
						start = 1;

						for(i = 0;i<6;i++)
							line[line_ch++]=gpsdata[ch-5+i];
						ch++;
						break;
					}
			}
		}
		if(start == 1)
		{			
			for(;gpsdata[ch]!='\0';ch++)
			{
				line[line_ch] = gpsdata[ch];

				if(line[line_ch] == '\n')//ÐŽ\nÊ±£¬winÏÂ×Ô¶¯×ª»»Îª   \r\n(0a0d),   linuxÏÂ²»»á×ª£¬ËùÒÔÎªÁËŒæÈÝ£¬ÕâÀïÊÇ¶Á\n£¬Ã»Çø±ð£¬¶ŒÊÇÖµ×îºóµÄ»»ÐÐ·û0x0d
				{
					if(line[0]=='$'&&line[3]=='G'&&line[4]=='G'&&line[5]=='A')  //ÎªGPSÐÐ
					{

						//»ñÈ¡Ž«»ØµÄÐ£ÑéºÍ ×¢Òâ\nÊÇÁœžö×ÖœÚ£º 0a 0d »Ø³µŒÓ»»ÐÐ
						if (
								(
									(line[line_ch-3] >= '0' && line[line_ch-3] <= '9') || \
									(line[line_ch-3] >= 'A' && line[line_ch-3] <= 'F')
								)	
								&&	
								(
									(line[line_ch-2] >= '0' && line[line_ch-2] <= '9') || \
									(line[line_ch-2] >= 'A' && line[line_ch-2] <= 'F')
								)
							)
						{
							if(isalpha(line[line_ch-3])) bit_a = line[line_ch-3] - 'A' + 10;
							else bit_a = line[line_ch-3] - '0';
							if(isalpha(line[line_ch-2])) bit_b = line[line_ch-2] - 'A' + 10;
							else bit_b = line[line_ch-2] - '0';
							gpscks = 16*bit_a + bit_b;
						}
						else
						//	printf("[GPS] Checksum received error: bita:%c bitb:%c\n", line[line_ch-3], line[line_ch-2]);

						//ŒÆËãÊµŒÊµÄÐ£ÑéºÍ
						i = 1;
						while(line[i]!='*')
						{
							cks = cks^line[i];
							i++;
						}

						if( cks == gpscks )
						{
				
							//printf("%s",line);
							//Ÿ­Î³¶ÈÊ±ŒäœâËã
							for(i=0;line[i]!='\n';i++)
							{
								if(line[i]==',')
								{
									dot_num++;
									bit = 0;
									continue;
								}
								switch(dot_num)
								{
								case 1:
									gps_time[bit] = line[i];
									bit++;
									break;
								case 2:								
									lati[bit] = line[i];
									bit++;
									break;		
								case 4:
									longti[bit] = line[i];
									bit++;
									break;
								default:
									break;
								}
							}
						}
						else
						{		
							printf("[GPS] Checksum failed : %d (distant) / %d (local)\n", gpscks, cks);
						}
						cks=0;
						//printf("GPS: %s,%s,%s\n",time, lati, longti);
					}
					else if(line[0]=='A'&&line[1]=='x'&&line[2]=='e'&&line[3]=='s') //ÎªœÇ¶ÈÐÐ
					{
						int i=6,j=0;
						vp_os_memset(angle, 0 , 20);

						for(;line[i]!=',';i++)		//ÇãœÇ²¹³¥Ç°µÄœÇ¶È
							angle[j++]=line[i];
				/*		//ÇãœÇ²¹³¥
						for(;line[i]!=',';i++)		//ÇãœÇ²¹³¥Ç°µÄœÇ¶È
							angle[j++]=line[i];
						i++;
						for(j=0;line[i]!=',';i++)   //µç×ÓÂÞÅÌµÄxÖáŽÅ³¡Ç¿¶È
							x[j++]=line[i];
						i++;
						for(j=0;line[i]!=',';i++)		//µç×ÓÂÞÅÌµÄyÖáŽÅ³¡Ç¿¶È
							y[j++]=line[i];
						i++;
						for(j=0;line[i]!=',';i++)  //µç×ÓÂÞÅÌµÄzÖáŽÅ³¡Ç¿¶È
							z[j++]=line[i];

						//psi = ARDrone_psi/57.296;      //ARDrone yaw in degrees;
						phi = ARDrone_phi/57.296;      //ARDrone roll in degrees;
						theta = ARDrone_theta/57.296;    //ARDrone pitch in degrees;
						fx=atof(x);
						fy=atof(y);
						fz=atof(z);
						fx_trans = fx*cos(theta)+fy*sin(theta)*sin(phi)+fz*sin(theta)*cos(phi);
						fy_trans = fy*cos(phi)-fz*sin(phi);
						angle_clbr = (atan2(fy_trans, fx_trans)+3.14159)*57.296;*/
					}
					line_ch = 0;
					vp_os_memset(line, 0 , 100);
					continue;
				}
				line_ch++;
			}
		}
		__android_log_print("ANDROID_LOG_INFO","ARDrone","gps_time[20]=%s , lati[20]=%s, longti[20]=%s, angle[20]=%s;",&gps_time, &lati, &longti, &angle);

		return C_OK;
}





/*---------------------------------------------------------------------------------------------------------------------
 Relinquish the local resources after the event loop exit 
---------------------------------------------------------------------------------------------------------------------*/
inline C_RESULT demo_gps_client_release( void )
{
	/**	======= INSERT USER CODE HERE ========== **/
	/* Clean up your navdata handler */
	/**	======= INSERT USER CODE HERE ========== **/
  return C_OK;
}



/* 
Registering the navdata handling function to 'navdata client' which is part 
of the ARDroneTool.
You can add as many navdata handlers as you want.
Terminate the table with a NULL pointer.
*/
BEGIN_GPS_HANDLER_TABLE
  GPS_HANDLER_TABLE_ENTRY(demo_gps_client_init, demo_gps_client_process, demo_gps_client_release, NULL)
END_GPS_HANDLER_TABLE
