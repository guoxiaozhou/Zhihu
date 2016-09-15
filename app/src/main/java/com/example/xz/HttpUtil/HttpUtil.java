package com.example.xz.HttpUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xz on 2016/8/8.
 */
public class HttpUtil  {
    public static ArrayList<Bitmap> bitmapList=new ArrayList<Bitmap>();
    public static ArrayList<Bitmap> topbitmapList=new ArrayList<Bitmap>();
    public static String getJsonContent(String urlStr){
        try {

            URL url=new URL(urlStr);

            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            //设置连接属性
            Log.i("HttpUtil", "连接网络");

            httpURLConnection.setConnectTimeout(2000);
            httpURLConnection.setDoInput(true);  //确保可以使用httpURLConnection.getInputStream();
            httpURLConnection.setRequestMethod("GET");

            Log.i("httpURLConnection", httpURLConnection.toString());

            int respCode=httpURLConnection.getResponseCode();

            if(respCode==200){
                return  ConvertStreamJson(httpURLConnection.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String ConvertStreamJson(InputStream inputStream) {
        Log.i("httpUtil","方法调用成功");
        String jsonStr="";
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        byte[] buffer=new byte[1024];
        int len=0;
        try {
            while ((len=inputStream.read(buffer,0,buffer.length))!=-1){
                out.write(buffer,0,len);
            }
            inputStream.close();
            out.close();
            jsonStr=new String(out.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }
    public static Start getStart(String jsonStr){
        Start start=new Start();
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonStr);
            start.setImageName(jsonObject.getString("text"));
            start.setStartImageUrl(jsonObject.getString("img"));
            Log.i("getStart",jsonObject.getString("text"));
            Log.i("getStart",jsonObject.getString("img"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return start;
    }
    public static ArrayList<Story> getStories(String jsonStr){
        ArrayList<Story> list= new ArrayList<Story>();
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonStr);
            JSONArray storiesArray=jsonObject.getJSONArray("stories");
            Log.i("getStories",storiesArray.toString());
            for(int i=0;i<storiesArray.length();i++){
                JSONObject jsonItem=storiesArray.getJSONObject(i);
                Story story=new Story();
                story.setUrlId("http://daily.zhihu.com/story/"+jsonItem.getInt("id"));
                story.setTitle(jsonItem.getString("title"));
                JSONArray imagesArray= jsonItem.getJSONArray("images");
                String imageuUrl=(String)imagesArray.get(0);
               Log.i("getStories","imageUrl"+imageuUrl);
                story.setImageUrl(imageuUrl);
                Bitmap bitmap=getBitmap(imageuUrl);
                bitmapList.add(bitmap);
                list.add(story);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static ArrayList<Story> gettopStories(String jsonStr){
        ArrayList<Story> list= new ArrayList<Story>();
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonStr);
            JSONArray topStoriesArray=jsonObject.getJSONArray("top_stories");
            for(int i=0;i<topStoriesArray.length();i++){
                JSONObject jsonItem=topStoriesArray.getJSONObject(i);
                Story story=new Story();
                story.setUrlId("http://daily.zhihu.com/story/"+jsonItem.getInt("id"));
                story.setTitle(jsonItem.getString("title"));
                String imageuUrl=jsonItem.getString("image");
                story.setImageUrl(imageuUrl);
                Log.i("gettopStories","imageUrl"+imageuUrl);
                Bitmap bitmap=getBitmap(imageuUrl);
                topbitmapList.add(bitmap);
                list.add(story);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

  /**  public static List<Map<String,Object>> getData(ArrayList<Story> stotiesList) throws IOException {
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map ;
        Log.d("getData", "getData方法");

        for (int i = 0; i < stotiesList.size(); i++) {
            map = new HashMap<String, Object>();
            map.put("id", stotiesList.get(i).getId());
            map.put("title", stotiesList.get(i).getTitle());
            Log.i("getData", stotiesList.get(i).getTitle());
          //  map.put("imageUrl", stotiesList.get(i).getImageUrl());
          //  Log.i("getDate",stotiesList.get(i).getImageUrl());


            Log.i("getData","bitmap"+bitmap.toString());
            map.put("image",bitmap);
            mapList.add(map);
        }
        Log.i("getData","size="+stotiesList.size());
        return mapList;
    }
   **/
    public static Bitmap getBitmap(String imagePath) throws IOException {
        URL url=new URL(imagePath);
        HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
        urlConnection.setConnectTimeout(2000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoInput(true);
        if(urlConnection.getResponseCode()==200){
            InputStream inputStream=urlConnection.getInputStream();
            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        return null;
    }
}
