package dev.camunda.bpmn.editor.browser.jsquery.impl;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.util.Objects.isNull;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A specific implementation of HandledJSQuery for opening script files.
 * This class creates a JavaScript function that can be called from the browser
 * to open a script file with the given text content.
 *
 * @author Oleksandr Havrysh
 */
public class GetClipboardJSQuery extends HandledJSQuery {

    /**
     * The JavaScript code template for creating the openScriptExternalFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String GET_CLIPBOARD_JS = """
            window.getBpmnClipboard = function(text) {
                return new Promise((resolve, reject) => {
                    %s
                });
            }""";

    /**
     * Constructs a new OpenScriptFileJSQuery.
     *
     * @param browser The JBCefBrowser instance on which the JavaScript query will be executed.
     */
    public GetClipboardJSQuery(@NotNull JBCefBrowser browser) {
        super(browser, text -> new JBCefJSQuery.Response(getClipboardString()));
    }

    /**
     * Retrieves the current text content of the system clipboard.
     *
     * @return The text content of the clipboard, or null if the clipboard is empty or an error occurs.
     */
    private static @Nullable String getClipboardString() {
        var clipboard = getDefaultToolkit().getSystemClipboard();
        if (isNull(clipboard) || !clipboard.isDataFlavorAvailable(stringFlavor)) {
            return null;
        }

        try {
            return (String) clipboard.getData(stringFlavor);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query creates a function in the browser's window object that can be called to open a script file.
     * The function returns a Promise that resolves with the virtual file ID of the created script file.
     *
     * @return A String representing the JavaScript query to create the openScriptExternalFile function.
     */
    @Override
    public @NotNull String getQuery() {
        return GET_CLIPBOARD_JS.formatted(jbCefJSQuery.inject("text",
                """
                        function(response) {
                            resolve(response);
                        }""",
                """
                        function(error_code, error_message) {
                            reject(new Error(`Error ${error_code}: ${error_message}`));
                        }"""));
    }
}