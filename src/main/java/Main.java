import jp.k_ui.ansipixels.RootServlet;

import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class Main {
  public static void main(String[] args) throws Exception {
    String host = System.getProperty("host", "localhost");
    int port = Integer.parseInt(System.getProperty("port", "8080"), 10);
    boolean useAccesslog = Boolean.parseBoolean(System.getProperty("useAccesslog", "false"));

    HandlerCollection handler = new HandlerCollection();

    ServletHandler servlet = new ServletHandler();
    servlet.addServletWithMapping(RootServlet.class, "/*");
    handler.addHandler(servlet);

    if (useAccesslog) {
      RequestLogHandler log = new RequestLogHandler();
      log.setRequestLog(new Slf4jRequestLog());
      handler.addHandler(log);
    }

    InetSocketAddress addr = InetSocketAddress.createUnresolved(host, port);
    Server server = new Server(addr);
    server.setHandler(handler);
    server.start();
    server.join();
  }
}
