package com.e.belle.Model;

import java.util.ArrayList;

public class LoginModel {

    public boolean condition;
    public String message = null;
    public String name = null;
    public String email_id = null;
    public String role_id = null;
    public String user_type = null;
    public String created_by = null;
    public String user_id = null;

    public ArrayList<menu_name> menu;

    public class menu_name {
        public String id;
        public String title;
        public String translate;
        public String type;
        public String icon;
        public ArrayList<childElement> children;

        public class childElement {
            public String id;
            public String title;
            public String type;
            public String url;
        }
    }
}





//    public ArrayList<user_detailsModel> user_details;
//    public ArrayList<user_role_permission> user_role_permission;


//    public class user_detailsModel {
//        public boolean condition;
//        public String message = null;
//        public String code = null;
//        public String name = null;
//        public String contact_no = null;
//        public String email_id = null;
//        public String login_on = null;
//    }
//    public class user_role_permission {
//        public String page_id;
//        public String menu_name;
//        public String is_parent;
//        public ArrayList<childElement> childElement;
//    }
//    public class childElement {
//        public String page_id;
//        public String menu_name;
//        public String is_parent;
//    }
//}
