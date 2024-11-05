package dev.camunda.bpmn.editor.service.jsquery.impl;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;

/**
 * A specific implementation of InitJSQuery for setting focus on a script file.
 * This class creates a JavaScript function that can be called from the browser
 * to set focus on a script file identified by its virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
public class SetFocusScriptFileJSQuery extends InitJSQuery {

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
     * @param scriptFileService The ScriptFileService used to set focus on the script file.
     */
    public SetFocusScriptFileJSQuery(JBCefBrowser browser, ScriptFileService scriptFileService) {
        super(browser,
                jbCefJSQuery -> SET_FOCUS_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("virtualFileId")),
                virtualFileId -> {
                    scriptFileService.setFocus(virtualFileId);
                    return new JBCefJSQuery.Response(null);
                });
    }
}