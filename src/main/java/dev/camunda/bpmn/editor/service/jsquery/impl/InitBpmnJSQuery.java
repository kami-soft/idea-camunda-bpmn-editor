package dev.camunda.bpmn.editor.service.jsquery.impl;

import static dev.camunda.bpmn.editor.util.Base64Utils.encode;

import dev.camunda.bpmn.editor.service.jsquery.InitJSQuery;
import org.cef.browser.CefBrowser;

/**
 * A specific implementation of InitJSQuery for initializing BPMN in the browser.
 * This class creates a JavaScript query to set the BPMN XML content and initialize the BPMN application.
 *
 * @author Oleksandr Havrysh
 */
public class InitBpmnJSQuery extends InitJSQuery {

    /**
     * The JavaScript code template for setting the BPMN XML and initializing the app.
     * The %s placeholder will be replaced with the Base64 encoded BPMN XML.
     */
    private static final String INIT_BPMN_JS = """
            window.bpmnXml = `%s`;
            initApp(window.bpmnXml);""";

    /**
     * Constructs a new InitBpmnJSQuery.
     *
     * @param cefBrowser The CefBrowser instance on which the JavaScript query will be executed.
     * @param originBpmn The BPMN XML content to be initialized in the cefBrowser.
     */
    public InitBpmnJSQuery(CefBrowser cefBrowser, String originBpmn) {
        super(cefBrowser, INIT_BPMN_JS.formatted(encode(originBpmn)));
    }
}