package com.e.belle.AllApiLinks;

public class AllApiLinks {

  //public static String globalurl = "https://hcm.pristinefulfil.com";
  public static String globalurl = "https://pristinefulfil.com";
  public static String LoginApi = globalurl+"/api/User/UserLogin";
  public static String GetDayMarketList = globalurl +"/api/AppActivity/GetAssignedMarkettosp";
  public static String GetArea = globalurl +"/api/AppActivity/GetArea?market_id=";
  public static String GetRetailer = globalurl+"/api/AppActivity/GetRetailer?area_id=";
  public static String GetRetailerDetails = globalurl+"/api/AppActivity/GetRetailerDetailes/";
  public static String CreateOrderHeader = globalurl+"/api/AppActivity/CreateOrderHeader";

  public static String ApprovalList = globalurl+"/api/Order/GetAppOrderHeaderDetai?dist=";
  public static String OrderListLine = globalurl+"/api/Order/GetOrderLine?order_no=";
  public static String CancelOkOrder = globalurl+"/api/AppActivity/ChangeStatusOrder";

  public static String versioncodeapiurl = globalurl + "/api/Version/CheckForUpdate?type=BelleySale_App";
}
