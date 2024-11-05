package dev.camunda.bpmn.editor.service.jsquery.impl;

import static dev.camunda.bpmn.editor.util.Constants.FAILURE_CALLBACK_QUERY;
import static dev.camunda.bpmn.editor.util.Constants.SUCCESS_CALLBACK_QUERY;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import dev.camunda.bpmn.editor.service.script.ScriptFileService;

/**
 * A specific implementation of InitJSQuery for opening script files.
 * This class creates a JavaScript function that can be called from the browser
 * to open a script file with the given text content.
 *
 * @author Oleksandr Havrysh
 */
public class OpenScriptFileJSQuery extends InitJSQuery {

    /**
     * The JavaScript code template for creating the openScriptExternalFile function.
     * The %s placeholder will be replaced with the injected handler code.
     */
    private static final String OPEN_SCRIPT_FILE_JS = """
            window.openScriptExternalFile = function(text) {
                return new Promise((resolve, reject) => {
                    %s
                });
            }""";

    /**
     * Constructs a new OpenScriptFileJSQuery.
     *
     * @param browser           The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param scriptFileService The ScriptFileService used to create the script file.
     */
    public OpenScriptFileJSQuery(JBCefBrowser browser, ScriptFileService scriptFileService) {
        super(browser,
                jbCefJSQuery -> OPEN_SCRIPT_FILE_JS.formatted(jbCefJSQuery.inject("text", SUCCESS_CALLBACK_QUERY,
                        FAILURE_CALLBACK_QUERY)),
                text -> new JBCefJSQuery.Response(scriptFileService.create(text)));
    }
}