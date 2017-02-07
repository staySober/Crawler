package get;

import com.sun.tools.javac.util.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.util.NodeList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import static org.htmlparser.lexer.InputStreamSource.BUFFER_SIZE;

/**
 * Created by sober on 2017/2/7.
 */
public class SoberCrawler {



    public static void main(String[] args) throws Exception {
        URL url=new URL("http://h5.yit.com/activity/59.html?_spm=3.112.674.2");
        URLConnection conn=url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 7.0;)");
        conn
                .setRequestProperty(
                        "Accept",
                        "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");
        BufferedReader bf=new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        StringBuilder sb=new StringBuilder();
        String line=null;
        while ((line=bf.readLine())!=null){
            sb.append(line+"\r\n");
        }
        bf.close();
        String htmlContent = sb.toString();
        // 实例化Parser对象
        Parser parser = Parser.createParser(htmlContent,"utf-8");
        // 设置编码
        parser.setEncoding("UTF-8");
        String filterStr = "img";
        //HTML解析
        NodeFilter filter = new TagNameFilter(filterStr);
        NodeList nodeList = parser.extractAllNodesThatMatch(filter);
        System.out.println("size: " + nodeList.size());

        for (int i = 0; i < nodeList.size(); i++) {
            ImageTag imageTag = (ImageTag) nodeList.elementAt(i);
            String imageUrl = imageTag.getImageURL();
            String imageUrl2=imageTag.getAttribute("data-echo");
            System.out.println("iamge " + (i+1) + ": " + imageUrl);
            //保存图片
            if (!imageUrl.equals("http://imgcms.yit.com/h5/images/spacer.gif") && imageUrl!=null){
                try {

                    saveToFile(imageUrl,"fetch");
                }catch (Exception e){
                    System.out.println("*********warning:"+imageUrl2+"**********save failed");
                }finally {
                    continue;
                }
            }else {
                if(imageUrl2!=null) {
                    saveToFile(imageUrl2, "fetch");
                }
            }
        }

    }

    public static void saveToFile(String destUrl, String title) {
        FileOutputStream fos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL url = null;
        byte[] buf = new byte[BUFFER_SIZE];
        int size = 0;
        int pos = destUrl.lastIndexOf('/');

        String fileName = "";
        if (pos != -1)
            fileName = destUrl.substring(pos + 1, destUrl.length());
        else
            fileName = destUrl.substring(destUrl.length() - 10, destUrl
                    .length());
        String path = "/Users/sober/Desktop/image"+File.separator;

        System.out.println("title: " + title);

        if (null != title && !"".equals(title)) {
            File file = new File(path + title + File.separator);
            if (!file.exists()) {
                file.mkdirs();

            }

            path = file.getPath();
        }
        path = path + File.separator + fileName;
        System.out.print(path);
        // 建立链接
        try {
            url = new URL(destUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            // 连接指定的资源
            httpUrl.connect();
            // 获取网络输入流
            bis = new BufferedInputStream(httpUrl.getInputStream());
            // 建立文件

            fos = new FileOutputStream(path);

            // 保存文件
            while ((size = bis.read(buf)) != -1)
                fos.write(buf, 0, size);

            fos.close();
            bis.close();
            httpUrl.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        System.out.println("  Save Image Success");
    }

}
