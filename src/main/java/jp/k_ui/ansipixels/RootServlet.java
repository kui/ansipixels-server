package jp.k_ui.ansipixels;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class RootServlet extends HttpServlet {
  private static final long serialVersionUID = -6237014533447726392L;
  private static final String FOOTER = "\nSee http://...\n";
  private static final Logger LOG = LoggerFactory.getLogger(RootServlet.class);

  private static final Cache<String, BufferedImage> PNG_CACHE = CacheBuilder.newBuilder()
      .maximumSize(300)
      .softValues()
      .build();
  private static final Cache<String, String> ANSITEXT_CACHE = CacheBuilder.newBuilder()
      .maximumSize(300)
      .softValues()
      .build();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    String path = req.getRequestURI();
    int pathLength = path.length();
    if (pathLength > 1) {
      if (pathLength > 4 && path.endsWith(".png")) {
        handleAnsiPixelsPng(res, path.substring(1, path.length() - 5));
      } else {
        handleAnsiPixelsText(res, path.substring(1));
      }
      return;
    }

    String refString = req.getHeader("referer");
    if (refString == null) {
      handleRoot(res);
      return;
    }

    URI ref = null;
    try {
      ref = new URI(refString);
    } catch (URISyntaxException e) {
      handleError(res, "Invalid Refferer URL: " + refString);
      LOG.debug(e.getMessage(), e);
      return;
    }

    String refQuery = ref.getQuery();
    if (refQuery.length() > 0) {
      handleAnsiPixelsPng(res, refQuery);
      return;
    }

    handleRoot(res);
  }

  private void handleAnsiPixelsText(HttpServletResponse res, String base64)
      throws IOException {
    String txtCache = ANSITEXT_CACHE.getIfPresent(base64);
    if (txtCache != null) {
      handleAnsiText(res, txtCache);
      LOG.debug("Using the cached ANSI text");
      return;
    }

    Pixels p;
    try {
      p = getPixels(base64);
    } catch (HandlableException e) {
      handleError(res, String.format("Unexpected data format: %s: %s", e.getMessage(), base64));
      LOG.debug(e.getMessage(), e);
      return;
    }

    String txt = p.toAnsiText();
    ANSITEXT_CACHE.put(base64, txt);
    handleAnsiText(res, txt);
  }

  private void handleAnsiText(HttpServletResponse res, String txt) throws IOException {
    res.setContentType("application/octet-stream");
    res.getWriter()
        .append(txt)
        .close();
  }

  private void handleAnsiPixelsPng(HttpServletResponse res, String base64) throws IOException {
    BufferedImage imgCache = PNG_CACHE.getIfPresent(base64);
    if (imgCache != null) {
      handleImage(res, imgCache);
      LOG.debug("Using the cached image");
      return;
    }

    Pixels p;
    try {
      p = getPixels(base64);
    } catch (HandlableException e) {
      handleError(res, String.format("Unexpected data format: %s", base64));
      LOG.debug(e.getMessage(), e);
      return;
    }

    BufferedImage img = p.toImageWithLongSide(280);
    PNG_CACHE.put(base64, img);
    handleImage(res, img);
  }

  private void handleImage(HttpServletResponse res, BufferedImage img) throws IOException {
    res.setContentType("image/png");
    ServletOutputStream o = res.getOutputStream();
    ImageIO.write(img, "png", o);
    o.close();
  }

  private void handleError(HttpServletResponse res, String message)
      throws IOException {
    res.setContentType("text/plain");
    res.getWriter()
        .append(message)
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
