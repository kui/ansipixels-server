package jp.k_ui.ansipixels;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Test;

public class PixelsTest {

  @Test
  public void printAnsiText() {
    List<List<Integer>> rows = asList(
        asList(0, null, null, 0),
        asList(1, 2, 3, 4)
        );
    Pixels p = new Pixels(rows);
    System.out.print(p.toAnsiText());
  }
}
