package dev.camunda.bpmn.editor.browser.jsquery.impl;

import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import java.awt.datatransfer.StringSelection;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of HandledJSQuery for opening script files.
 * This class creates a JavaScript function that can be called from the browser
 * to open a script file with the given text content.
 *
 * @author Oleksandr Havrysh
 */
public class SetClipboardJSQuery extends HandledJSQuery {

    /**
     * The JavaScript code template for creating the openScriptExternalFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String SET_CLIPBOARD_JS = """
            window.copyBpmnClipboard = function(text) {
                %s
            }""";

    /**
     * Constructs a new OpenScriptFileJSQuery.
     *
     * @param browser           The JBCefBrowser instance on which the JavaScript query will be executed.
     */
    public SetClipboardJSQuery(@NotNull JBCefBrowser browser) {
        super(browser, text -> {
            CopyPasteManager.getInstance().setContents(new StringSelection(text));
            return new JBCefJSQuery.Response(null);
        });
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
        return SET_CLIPBOARD_JS.formatted(jbCefJSQuery.inject("text"));
    }
}