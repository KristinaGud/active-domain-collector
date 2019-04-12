package selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActiveDomainSelector {
    private Logger log = LoggerFactory.getLogger(ActiveDomainSelector.class);

    private HttpURLConnection createConnection(String hostName) throws IOException {
        URL url = new URL(hostName);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("user-agent", "Chrome");
        return con;
    }

    private String checkRedirectingLocation(HttpURLConnection con) {
        return con.getHeaderField("Location");
    }

    boolean isActive(String hostName, String orgHostName, PrintWriter writerAct, PrintWriter writerTryLater, int triesIfRedirect) throws IOException {
        HttpURLConnection connection = createConnection(hostName);
        int responseCode = connection.getResponseCode();

        if(responseCode==200) {
            if (hostName.equals(orgHostName)) {
                log.info(hostName + " is active");
                writerAct.println(hostName);
            } else {
                String message = orgHostName + " redirected to " + hostName + " and is active";
                log.info(message);
                writerAct.println(message);
            }
            return true;
        } else if (responseCode >= 300 && responseCode < 400) {
            if (triesIfRedirect > 0) {
                String redirectionLocation = checkRedirectingLocation(connection);
                return isActive(redirectionLocation, orgHostName, writerAct, writerTryLater, triesIfRedirect - 1);
            } else {
                log.info("Too many redirects after " + hostName);
            }
        } else if (responseCode == 503) {
            log.info(hostName + " try later, response code: " + responseCode);
            writerTryLater.println(hostName);
        } else {
            log.info(hostName + " is inactive, response code: " + responseCode);
        }
        return false;
    }
}
