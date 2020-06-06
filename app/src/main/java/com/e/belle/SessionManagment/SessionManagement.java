package com.e.belle.SessionManagment;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManagement {
    SharedPreferences sp;

  public SessionManagement(Context context) {
    sp = context.getSharedPreferences("BelleLingeries",Context.MODE_PRIVATE);
    }

    public void setUserName(String userName) {      setSharedPreferences("name", userName); }

    public void setUserType(String userType) {     setSharedPreferences("user_type", userType); }

    public void setUserEmail(String userEmail) {    setSharedPreferences("email_id", userEmail); }

    public void setRoleId(String RoleId) {      setSharedPreferences("role_id", RoleId); }

    public void setUserId(String UserId) {      setSharedPreferences("user_id", UserId); }

    public void setAddress(String address) {
    setSharedPreferences("address", address);
}

    public void setMenu(String menu) {
         setSharedPreferences("menu", menu);
}



// get data from sharedpreferences

    public String getUserName() {
    return getDataFromSharedPreferences("name");
}

    public String getUserType() {
        return getDataFromSharedPreferences("user_type");
    }

    public String getUserEmail() {
    return getDataFromSharedPreferences("email_id");
}

    public String getRoleId() {
        return getDataFromSharedPreferences("role_id");
    }

    public String getUserId() {
        return getDataFromSharedPreferences("user_id");
    }

    public String getMenu() {
    return getDataFromSharedPreferences("menu");
}


//public LoginModel.user_detailsModel getAddress() {
// return new Gson().fromJson(getDataFromSharedPreferences("address"), LoginModel.user_detailsModel.class);
//}
   private String getDataFromSharedPreferences(String Key) {
    try {
        return sp.getString(Key, null);
    } catch (Exception e) {
        return "";
    }
}

    private void setSharedPreferences(String key, String value) {
    SharedPreferences.Editor editor = sp.edit();
    editor.putString(key,value);
    editor.commit();
    }
    public void ClearSession() {
    SharedPreferences.Editor editor = sp.edit();
    editor.clear();
    editor.commit();
    }
}
