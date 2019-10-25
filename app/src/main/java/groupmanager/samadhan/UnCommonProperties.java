package groupmanager.samadhan;

//New Change 6 Oct 2015
public class UnCommonProperties {
	
	private String MainClientID="samadhan";/////// if want remove group code in login page
	private String Service_PackageName="groupmanager.samadhan.Service_Call_New";
	private String AppTitle="ENTREPRENEUR INDIA LIVE";
	private String LocationApp="https://play.google.com/store/apps/details?id=groupmanager.samadhan&hl=en";
	private String Addgrp="NO";
	private String HeadName="ENTREPRENEUR INDIA LIVE";//it display in login xml for heading
	private String PackageName="groupmanager.samadhan";//it display package name in export data
	public boolean ShowAllEventOption=false;//it Allow to ShowAll Event CheckBox or Not In Event
	
	public String GET_MainClientID()
	{
		return MainClientID;
	}
	
	public String GET_headName()
	{
		return HeadName;
	}

	public String GET_Service_PackageName()
	{
		return Service_PackageName;
	}
	
	public String GET_AppTitle()
	{
		return AppTitle;
	}
	
	public String GET_Addgroup()
	{
		return Addgrp;
	}
	
	public String GET_AppLocation()
	{
		return LocationApp;
	}
	
	public String GET_PackageName()
	{
		return PackageName;
	}
	
}
