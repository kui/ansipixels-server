package jp.k_ui.ansipixels;

/**
 * Created by kui on 14/08/21.
 */
public class HandlableException extends Exception {
    public HandlableException(String message, Exception e) {
        super(message, e);
    }
}
