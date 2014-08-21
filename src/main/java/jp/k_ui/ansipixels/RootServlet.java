package jp.k_ui.ansipixels;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RootServlet extends HttpServlet {
    private static String FOOTER = "\nSee http://...\n";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String path = req.getRequestURI();
        if (path.length() > 1) {
            handleAnsiPixels(res, path.substring(1));
        } else {
            handleRoot(res);
        }
    }

    private void handleAnsiPixels(HttpServletResponse res, String base64) throws IOException {
        Pixels p;
        try {
            p = getPixels(base64);
        } catch (HandlableException e) {
            handleError(res, base64, e);
            return;
        }
        res.setContentType("application/octet-stream");
        PrintWriter w = res.getWriter();
        p.writeAsAnsiText(w);
        w.close();
    }

    private void handleError(HttpServletResponse res, String base64, HandlableException e) throws IOException {
        res.setContentType("text/plain");
        res.getWriter()
                .append("Unexpected data format: ").append(e.getMessage()).append(": ").append(base64)
                .append(FOOTER)
                .close();
    }

    private Pixels getPixels(String base64) throws HandlableException {
        try {
            byte[] zipped = DecodeUtils.convertToBytesFromBase64(base64);
            byte[] jsonBytes = DecodeUtils.convertToBytesFromZippedBytes(zipped);
            String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
            List<List<Integer>> codes = DecodeUtils.parseCodeMatrix(jsonString, "pixels");
            return new Pixels(codes);
        } catch (IOException | IllegalArgumentException e) {
            throw new HandlableException(e.getMessage(), e);
        }
    }

    private void handleRoot(HttpServletResponse res) throws IOException {
        res.setContentType("text/plain");
        res.getWriter()
                .append("Expect the request path to /{base64ZippedJson}")
                .append(FOOTER)
                .close();
    }
}
