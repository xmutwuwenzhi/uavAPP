package com.parrot.ARDrone;

public class AddressInfo
{
	public String jd;
	public String wd;
	public String msgStr;
	public String listStr;
	
	public AddressInfo(String jingdu, String weidu, String message, String list)
	{
		this.jd       =  jingdu;
		this.wd       =  weidu;
		this.msgStr   =  message;
		this.listStr  =  list;
	}
}