package jp.k_ui.ansipixels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import com.google.common.collect.ImmutableList;

public class Pixels {
  private final static Color[] STANDARD_COLORS = new Color[] {
      new Color(0, 0, 0), new Color(221, 0, 0), new Color(0, 221, 0), new Color(221, 221, 0),
      new Color(0, 0, 221), new Color(221, 0, 221), new Color(0, 221, 221),
      new Color(221, 221, 221),

      new Color(102, 102, 102), new Color(255, 102, 102), new Color(102, 255, 102),
      new Color(255, 255, 102), new Color(102, 102, 255), new Color(255, 102, 255),
      new Color(102, 255, 255), new Color(255, 255, 255),
  };
  private final static int ANSI_RGB_STEP = 255 / 5;
  private final static float ANSI_GRAYSCALE_STEP = 255f / 23;

  private final static String ANSI_COLOR_RESET_CODE = "\u001b[0m";
  private final static ImmutableList<String> ANSI_COLOR_CODES = generateAnsiColorCodes();
  private final static ImmutableList<Color> ANSI_COLORS = generateColors();
  private final static Color DEFAULT_COLOR = new Color(51, 51, 51, 1);

  final private List<List<Integer>> rows;

  public Pixels(List<List<Integer>> rows) {
    this.rows = rows;
  }

  private static ImmutableList<String> generateAnsiColorCodes() {
    ImmutableList.Builder<String> b = ImmutableList.builder();
    for (int i = 0; i < 256; i++) {
      b.add(String.format("\u001B[48;5;%dm", i));
    }
    return b.build();
  }

  private static ImmutableList<Color> generateColors() {
    ImmutableList.Builder<Color> b = ImmutableList.builder();
    for (int i = 0; i < 256; i++) {
      b.add(generateColor(i));
    }
    return b.build();
  }

  private static Color generateColor(int code) {
    if (code < 16) {
      return STANDARD_COLORS[code];
    } else if (code < 232) {
      int base = code - 16;
      int r = base / 36;
      int gb = base % 36;
      int g = gb / 6;
      int b = gb % 6;
      return new Color(ANSI_RGB_STEP * r, ANSI_RGB_STEP * g, ANSI_RGB_STEP * b);
    } else if (code < 256) {
      int base = code - 232;
      return Color.getHSBColor(0, 0, ANSI_GRAYSCALE_STEP * base);
    }
    throw new RuntimeException("Unexpected ANSI color code: " + code);
  }

  public String toAnsiText() {
    Writer w = new StringWriter();
    try {
      writeAsAnsiText(w);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return w.toString();
  }

  public void writeAsAnsiText(Writer w) throws IOException {
    w.append(ANSI_COLOR_RESET_CODE);

    for (List<Integer> row : rows) {
      Integer prevCode = null;
      if (row == null)
        continue;

      for (Integer code : row) {
        if ((code == null && prevCode != null) ||
            (code != null && !code.equals(prevCode))) {
          w.append(code == null ?
              ANSI_COLOR_RESET_CODE :
              ANSI_COLOR_CODES.get(code));
        }
        w.append("  ");
        prevCode = code;
      }

      if (prevCode != null)
        w.append(ANSI_COLOR_RESET_CODE);
      w.append("\n");
    }
  }

  public BufferedImage toImageWithLongSide(int maxLongSidePixels) throws IOException {
    int width = getWidth();
    int height = getHeight();
    if (width > height) {
      if (width > maxLongSidePixels)
        throw new IllegalArgumentException("Too small maxLengthSidePixels: " + maxLongSidePixels);
      return toImage(maxLongSidePixels / width);
    } else {
      if (height > maxLongSidePixels)
        throw new IllegalArgumentException("Too small maxLengthSidePixels: " + maxLongSidePixels);
      return toImage(maxLongSidePixels / height);
    }
  }

  public BufferedImage toImage(int pixelSize) {
    if (pixelSize <= 0) {
      throw new IllegalArgumentException("Expect pixelSize to be more than 0: " + pixelSize);
    }
    int width = getWidth() * pixelSize;
    int height = getHeight() * pixelSize;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D g = image.createGraphics();
    drawPixels(pixelSize, g);
    image.flush();
    return image;
  }

  private void drawPixels(int pixelSize, Graphics2D g) {
    int ymax = rows.size();
    for (int y = 0; y < ymax; y++) {
      List<Integer> row = rows.get(y);
      if (row == null)
        continue;

      int offsetY = y * pixelSize;
      int xmax = row.size();
      for (int x = 0; x < xmax; x++) {
        int offsetX = x * pixelSize;
        Integer code = row.get(x);
        g.setColor((code == null) ? DEFAULT_COLOR : ANSI_COLORS.get(code));
        g.fillRect(offsetX, offsetY, pixelSize, pixelSize);
      }
    }
  }

  public int getHeight() {
    return rows.size();
  }

  private int width = -1;

  public int getWidth() {
    if (width != -1) {
      return width;
    }

    int w = 0;
    for (List<Integer> r : rows) {
      int s = r.size();
      if (s > w)
        w = s;
    }
    width = w;
    return w;
  }
}
