package com.e.belle.Http_Handler;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class GlobalPostingMethod {

    public URL createUrl(String strinUrl) throws MalformedURLException {

        try {
            return new URL(strinUrl);
        } catch (MalformedURLException e){
            throw  new MalformedURLException("Error with creating URL..");
        }
    }
    public HttpHandlerModel getHttpRequest(URL url) {

        if(url == null)
         return setReturnMessage(false,"URL is null Check the URL pass on it.");
            HttpURLConnection urlConnection = null;
         try {
           urlConnection = (HttpURLConnection) url.openConnection();
           urlConnection.setRequestMethod("GET");
           urlConnection.connect();

           if(urlConnection.getResponseCode() == 200)
               return setReturnMessage(true,readFromStream(urlConnection.getInputStream()));
            else
             return setReturnMessage(false,"Error response code:" + urlConnection.getResponseCode());

         } catch (Exception e) {
             return setReturnMessage(false,"Problem retrieving the user JSON results." + e.getMessage());
         } finally {
             if (urlConnection != null)
                 urlConnection.disconnect();

         }
    }

    public HttpHandlerModel postHttpRequest(URL url, JSONObject postedjason) {

     if(url == null)
         return setReturnMessage(false,"URL is null Check the URL pass on it.");
     HttpURLConnection urlConnection = null;

     try {
         urlConnection = (HttpURLConnection) url.openConnection();
         urlConnection.setRequestMethod("POST");
         urlConnection.setDoOutput(true);
         urlConnection.setRequestProperty("Content-Type", "application/json");
         urlConnection.connect();
         DataOutputStream printout = new DataOutputStream(urlConnection.getOutputStream());
         printout.writeBytes(postedjason.toString());
         printout.flush();
         printout.close();
         // TODO: 11-Sep-19   If the request was successful (response code 200),     // then read the input stream and parse the response.
         if (urlConnection.getResponseCode() == 200)
             return setReturnMessage(true, readFromStream(urlConnection.getInputStream()));
         else
             return setReturnMessage(false, "Error response code: " + urlConnection.getResponseCode());

     } catch (IOException e) {
         return setReturnMessage(false, "Problem retrieving the user JSON results." + e.getMessage());
     } finally {
         if (urlConnection != null)
             urlConnection.disconnect();

     }
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        if(inputStream!=null) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream,writer, Charset.forName("UTF-8"));
            String theString = writer.toString();
            return theString;
        } return null;
    }

    public HttpHandlerModel setReturnMessage(boolean connectStatus, String passdata) {
        HttpHandlerModel returnmodel = new HttpHandlerModel();
        returnmodel.setConnectStatus(connectStatus);
        returnmodel.setJsonResponse(passdata);
        return returnmodel;
    }

}
