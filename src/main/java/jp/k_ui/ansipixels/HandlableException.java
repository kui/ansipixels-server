package jp.k_ui.ansipixels;

public class HandlableException extends Exception {
  private static final long serialVersionUID = -759555878195620901L;

  public HandlableException(String message, Exception e) {
    super(message, e);
  }
}
