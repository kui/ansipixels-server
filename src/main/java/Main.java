import jp.k_ui.ansipixels.RootServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHandler;

public class Main {
  public static void main(String[] args) throws Exception {
    int port = Integer.parseInt(System.getProperty("port", "8080"), 10);

    HandlerCollection handler = new HandlerCollection();

    ServletHandler servlet = new ServletHandler();
    servlet.addServletWithMapping(RootServlet.class, "/*");
    handler.addHandler(servlet);

    RequestLogHandler log = new RequestLogHandler();
    log.setRequestLog(new Slf4jRequestLog());
    handler.addHandler(log);

    Server server = new Server(port);
    server.setHandler(handler);
    server.start();
    server.join();
  }
}
