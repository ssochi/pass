package com.example.administrator.pass.Service;

/**
 * Created by Administrator on 2016/2/22.
 */
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetUtil {


    public static String getRequestGetData(String URL){
        HttpURLConnection conn=null;
        try {

            URL url=new URL(URL);
            conn=(HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is=conn.getInputStream();
            String state=getStringFromInputStream(is);
            return state;



        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(conn!=null){
                conn.disconnect();
            }
        }


        return "Fail";
    }

    /**
     * 根据输入流返回一个字符串
     * @param is
     * @return
     * @throws Exception
     */
    private static String getStringFromInputStream(InputStream is) throws Exception{

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buff=new byte[1024];
        int len=-1;
        while((len=is.read(buff))!=-1){
            baos.write(buff, 0, len);
        }
        is.close();
        String html=baos.toString();
        baos.close();


        return html;
    }
}