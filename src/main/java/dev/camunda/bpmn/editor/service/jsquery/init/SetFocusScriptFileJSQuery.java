package dev.camunda.bpmn.editor.service.jsquery.init;

import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
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
     * @param browser           The JBCefBrowserWrapper instance on which the JavaScript query will be executed.
     * @param scriptFileService The ScriptFileService used to set focus on the script file.
     */
    public SetFocusScriptFileJSQuery(JBCefBrowserWrapper browser, ScriptFileService scriptFileService) {
        super(browser,
                jbCefJSQuery -> SET_FOCUS_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("virtualFileId")),
                scriptFileService::setFocus,
                0);
    }
}