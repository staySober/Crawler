package get;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sober on 2017/2/7.
 */
public class Demo1 {

    public static String getContent(String urlString)
            throws Exception {
        URL url = new URL(urlString);

        URLConnection conn = url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 7.0;)");
        conn
                .setRequestProperty(
                        "Accept",
                        "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, */*");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = "";
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\r\n");
        }

        return sb.toString();
    }

    private String processText(String content) {
        content = content.trim().replaceAll("&nbsp;", " ");

        return content;
    }

    public String getSinaArticleContent(String url)
            throws Exception {
        String content = getContent(url);

        StringBuilder sb = new StringBuilder();

        Parser parser = Parser.createParser(content, "utf-8");
        AndFilter filter = new AndFilter(new TagNameFilter("div"),
                new HasAttributeFilter("class", "articalContent   "));

        Node node = null;
        NodeList nodeList = parser.extractAllNodesThatMatch(filter);
        for (int i = 0; i < nodeList.size(); ++i) {
            node = nodeList.elementAt(i);
            sb.append(node.toPlainTextString());
        }

        return processText(sb.toString());
    }

    public static void main(String[] args)
            throws Exception {

        System.out.println(new Demo1().getSinaArticleContent("http://blog.sina.com.cn/s/blog_4701280b0100jbqq.html"));
    }


}
