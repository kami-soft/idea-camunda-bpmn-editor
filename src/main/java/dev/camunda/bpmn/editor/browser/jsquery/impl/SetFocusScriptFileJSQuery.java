package dev.camunda.bpmn.editor.browser.jsquery.impl;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.browser.jsquery.HandledJSQuery;
import dev.camunda.bpmn.editor.scratch.ScriptFileManager;
import org.jetbrains.annotations.NotNull;

/**
 * A specific implementation of HandledJSQuery for setting focus on a script file.
 * This class creates a JavaScript function that can be called from the browser
 * to set focus on a script file identified by its virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
public class SetFocusScriptFileJSQuery extends HandledJSQuery {

    /**
     * The JavaScript code template for creating the setFocusVirtualFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String SET_FOCUS_SCRIPT_FILE_JS = """
            window.setFocusVirtualFile = function(virtualFileId) {
                %s
            }""";

    /**
     * Constructs a new SetFocusScriptFileJSQuery.
     *
     * @param browser           The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param scriptFileManager The ScriptFileManager used to set focus on the script file.
     */
    public SetFocusScriptFileJSQuery(@NotNull JBCefBrowser browser, @NotNull ScriptFileManager scriptFileManager) {
        super(browser, virtualFileId -> {
            scriptFileManager.setFocus(virtualFileId);
            return new JBCefJSQuery.Response(null);
        });
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query creates a function in the browser's window object that can be called to set focus on a script file.
     *
     * @return A String representing the JavaScript query to create the setFocusVirtualFile function.
     */
    @Override
    public @NotNull String getQuery() {
        return SET_FOCUS_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("virtualFileId"));
    }
}