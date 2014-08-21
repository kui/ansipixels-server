package jp.k_ui.ansipixels;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class Pixels {
    final static String[] ANSI_COLOR_CODES = new String[256];
    final static String ANSI_COLOR_RESET_CODE = "\u001b[0m";

    static {
        String f = "\u001B[48;5;%dm";
        for (int i = 0; i < 256; i++) {
            ANSI_COLOR_CODES[i] = String.format(f, i);
        }
    }

    final private List<List<Integer>> rows;

    public Pixels(List<List<Integer>> rows) {
        this.rows = rows;
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
            for (Integer code : row) {
                if ((code == null && prevCode != null) ||
                        (code != null && !code.equals(prevCode))) {
                    w.append(code == null ?
                            ANSI_COLOR_RESET_CODE :
                            ANSI_COLOR_CODES[code]);
                }
                w.append("  ");
                prevCode = code;
            }
            if (prevCode != null) w.append(ANSI_COLOR_RESET_CODE);
            w.append("\n");
        }
    }
}

