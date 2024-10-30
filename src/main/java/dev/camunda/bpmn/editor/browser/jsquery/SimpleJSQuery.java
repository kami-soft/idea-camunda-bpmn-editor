package dev.camunda.bpmn.editor.browser.jsquery;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefBrowser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract class representing a simple JavaScript query to be executed in a JBCefBrowser.
 * This class provides a basic structure for creating and executing JavaScript queries.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public abstract class SimpleJSQuery implements Disposable {

    /**
     * The JBCefBrowser instance on which the JavaScript query will be executed.
     */
    protected final JBCefBrowser browser;

    /**
     * Returns the JavaScript query string to be executed.
     *
     * @return A String representing the JavaScript query.
     */
    public abstract @NotNull String getQuery();

    /**
     * Disposes of any resources held by this SimpleJSQuery instance.
     * This implementation is empty and can be overridden by subclasses if needed.
     */
    @Override
    public void dispose() {
    }

    /**
     * Executes the JavaScript query on the associated JBCefBrowser.
     * The query is executed at the current URL of the browser.
     */
    public void executeQuery() {
        browser.getCefBrowser().executeJavaScript(getQuery(), browser.getCefBrowser().getURL(), 0);
    }
}