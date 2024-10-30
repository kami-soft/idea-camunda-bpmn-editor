package dev.camunda.bpmn.editor.browser.jsquery.impl;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import dev.camunda.bpmn.editor.scratch.ScriptFileManager;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of HandledJSQuery for closing script files.
 * This class creates a JavaScript function that can be called from the browser
 * to close a script file identified by its virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
public class CloseScriptFileJSQuery extends HandledJSQuery {

    /**
     * The JavaScript code template for creating the closeScriptExternalFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String CLOSE_SCRIPT_FILE_JS = """
            window.closeScriptExternalFile = function(virtualFileId) {
                %s
            }""";

    /**
     * Constructs a new CloseScriptFileJSQuery.
     *
     * @param browser           The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param scriptFileManager The ScriptFileManager used to close the script file.
     */
    public CloseScriptFileJSQuery(@NotNull JBCefBrowser browser, @NotNull ScriptFileManager scriptFileManager) {
        super(browser, virtualFileId -> {
            scriptFileManager.closeScriptScratchFile(virtualFileId);
            return new JBCefJSQuery.Response(null);
        });
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query creates a function in the browser's window object that can be called to close a script file.
     *
     * @return A String representing the JavaScript query to create the closeScriptExternalFile function.
     */
    @Override
    public @NotNull String getQuery() {
        return CLOSE_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("virtualFileId"));
    }
}