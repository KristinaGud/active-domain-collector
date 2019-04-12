package selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        ActiveDomainSelector ads = new ActiveDomainSelector();
        int actCount = 0;
        int allCount = 0;
        List<String> urlList = Files.lines(Paths.get(args[0])).collect(Collectors.toList());
        PrintWriter writerAct = new PrintWriter(args[1]);
        PrintWriter writerTryLater = new PrintWriter(args[2]);

        for (String hostName: urlList) {
            try {
                allCount++;
                if (ads.isActive(hostName, hostName, writerAct, writerTryLater, 5)) {
                    actCount++;
                }
            }

            catch (java.net.UnknownHostException | javax.net.ssl.SSLProtocolException |
                    javax.net.ssl.SSLHandshakeException | java.net.ProtocolException |
                    java.net.SocketException| java.net.MalformedURLException e) {
                log.info(hostName + " is invalid one");
            }
        }

        writerAct.close();
        writerTryLater.close();
        log.info("Checked domains: " + allCount);
        log.info("Active domains: "  + actCount);
    }
}
