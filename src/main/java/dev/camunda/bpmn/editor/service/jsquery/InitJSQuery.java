package dev.camunda.bpmn.editor.service.jsquery;

import static java.util.Objects.nonNull;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import java.util.function.Function;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.cef.browser.CefBrowser;

/**
 * An abstract class representing an initial JavaScript query to be executed in a JBCefBrowser.
 * This class provides the functionality to create and execute JavaScript queries, and manage their lifecycle.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public abstract class InitJSQuery {

    private final CefBrowser cefBrowser;
    private final String query;
    private JBCefJSQuery jbCefJSQuery;

    /**
     * Constructs an InitJSQuery instance with the specified browser, query function, and handler.
     *
     * @param browser       The JBCefBrowser instance
     * @param queryFunction A function that generates the JavaScript query string
     * @param handler       A function that handles the response from the JavaScript query
     */
    protected InitJSQuery(JBCefBrowser browser,
                          Function<JBCefJSQuery, String> queryFunction,
                          Function<? super String, ? extends JBCefJSQuery.Response> handler) {
        this.cefBrowser = browser.getCefBrowser();
        this.jbCefJSQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
        jbCefJSQuery.addHandler(handler);
        this.query = queryFunction.apply(jbCefJSQuery);
    }

    /**
     * Executes the JavaScript query on the associated JBCefBrowser.
     * The query is executed at the current URL of the browser.
     */
    public void executeQuery() {
        cefBrowser.executeJavaScript(query, cefBrowser.getURL(), 0);
    }

    /**
     * Disposes of the JBCefJSQuery instance.
     * This method ensures that the resources associated with the query are properly released.
     */
    @PreDestroy
    public void destroy() {
        if (nonNull(jbCefJSQuery)) {
            jbCefJSQuery.dispose();
        }
    }
}