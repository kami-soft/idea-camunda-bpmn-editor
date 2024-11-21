package dev.camunda.bpmn.editor.service.clipboard;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.util.Objects.isNull;

import com.intellij.openapi.ide.CopyPasteManager;
import java.awt.datatransfer.StringSelection;

/**
 * The ClipboardService class provides methods to interact with the system clipboard.
 * It allows for retrieving and setting text content in the clipboard, facilitating
 * copy-paste operations within the application.
 * <p>
 * This service uses both the AWT Toolkit for clipboard access and the IntelliJ
 * IDEA's CopyPasteManager for setting clipboard contents, ensuring compatibility
 * with the IDE's clipboard handling mechanisms.
 */
public class ClipboardService {

    /**
     * Retrieves the current text content from the system clipboard.
     * <p>
     * This method attempts to access the system clipboard and extract its content
     * as a string. It handles potential issues such as an unavailable clipboard
     * or unsupported data flavors.
     *
     * @return The text content of the clipboard as a String, or null if:
     * - The clipboard is empty
     * - The clipboard does not contain text data
     * - An error occurs while accessing the clipboard
     */
    public String getContent() {
        var clipboard = getDefaultToolkit().getSystemClipboard();
        if (isNull(clipboard) || !clipboard.isDataFlavorAvailable(stringFlavor)) {
            return null;
        }

        try {
            return (String) clipboard.getData(stringFlavor);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Sets the specified text content to the system clipboard.
     * <p>
     * This method uses IntelliJ IDEA's CopyPasteManager to set the clipboard content,
     * ensuring proper integration with the IDE's clipboard handling. The text is
     * wrapped in a StringSelection object before being set to the clipboard.
     *
     * @param text The text content to be set in the clipboard. If null, an empty
     *             string will be set to the clipboard.
     */
    public void setContent(String text) {
        CopyPasteManager.getInstance().setContents(new StringSelection(text != null ? text : ""));
    }
}