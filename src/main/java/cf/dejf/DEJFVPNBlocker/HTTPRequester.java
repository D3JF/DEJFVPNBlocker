package cf.dejf.DEJFVPNBlocker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequester {

    private static final String USER_AGENT = "Mozilla/5.0";

    public static boolean checkIp(String ip) throws IOException {

        URL obj = new URL("https://whatismyipaddress.com/ip/" + ip);

        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString().contains("Network sharing device or proxy server") || response.toString().contains("Corporate");

        } else {
            System.out.println("GET request for VPN check failed!");
            return false;
        }

    }

}
