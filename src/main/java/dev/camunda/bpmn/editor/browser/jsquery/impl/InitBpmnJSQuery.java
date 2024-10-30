package dev.camunda.bpmn.editor.browser.jsquery.impl;

import static java.util.Base64.getEncoder;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.intellij.ui.jcef.JBCefBrowser;
import dev.camunda.bpmn.editor.browser.jsquery.SimpleJSQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A specific implementation of SimpleJSQuery for initializing BPMN in the browser.
 * This class creates a JavaScript query to set the BPMN XML content and initialize the BPMN application.
 *
 * @author Oleksandr Havrysh
 */
public class InitBpmnJSQuery extends SimpleJSQuery {

    private static final String EMPTY_BPMN_XML = "";

    /**
     * The JavaScript code template for setting the BPMN XML and initializing the app.
     * The %s placeholder will be replaced with the Base64 encoded BPMN XML.
     */
    private static final String INIT_BPMN_JS = """
            window.bpmnXml = `%s`;
            initApp(window.bpmnXml);""";

    /**
     * The Base64 encoded BPMN XML content.
     */
    private final String encodedBpmnXml;

    /**
     * Constructs a new InitBpmnJSQuery.
     *
     * @param browser The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param bpmnXml The BPMN XML content to be initialized in the browser.
     */
    public InitBpmnJSQuery(@NotNull JBCefBrowser browser, @Nullable String bpmnXml) {
        super(browser);
        this.encodedBpmnXml = new String(getEncoder().encode((isBlank(bpmnXml) ? EMPTY_BPMN_XML : bpmnXml).getBytes()));
    }

    /**
     * Returns the JavaScript query string to be executed.
     * This query sets the BPMN XML content in the browser's window object and calls the initApp function.
     *
     * @return A String representing the JavaScript query to initialize the BPMN application.
     */
    @Override
    public @NotNull String getQuery() {
        return INIT_BPMN_JS.formatted(encodedBpmnXml);
    }
}