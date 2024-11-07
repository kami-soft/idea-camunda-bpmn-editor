package dev.camunda.bpmn.editor.service.jsquery.init;

import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;

/**
 * A specific implementation of InitJSQuery for closing script files.
 * This class creates a JavaScript function that can be called from the browser
 * to close a script file identified by its virtual file ID.
 *
 * @author Oleksandr Havrysh
 */
public class CloseScriptFileJSQuery extends InitJSQuery {

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
     * @param browser           The JBCefBrowserWrapper instance on which the JavaScript query will be executed.
     * @param scriptFileService The ScriptFileService used to close the script file.
     */
    public CloseScriptFileJSQuery(JBCefBrowserWrapper browser, ScriptFileService scriptFileService) {
        super(browser,
                jbCefJSQuery -> CLOSE_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("virtualFileId")),
                scriptFileService::close,
                0);
    }
}