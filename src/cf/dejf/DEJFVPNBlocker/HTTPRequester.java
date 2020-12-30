package cf.dejf.DEJFVPNBlocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequester {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static boolean isVPN(String ip) throws IOException {

        URL obj = new URL("https://whatismyipaddress.com/ip/" + ip);

        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = httpURLConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if(response.toString().contains("Network sharing device or proxy server") || response.toString().contains("Corporate")) {
                return true;
            } else {
                return false;
            }

        } else {
            System.out.println("GET request for VPN check failed!");
            return false;
        }

    }

}
