package dev.camunda.bpmn.editor.browser.jsquery;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * An abstract class extending SimpleJSQuery to provide handled JavaScript queries.
 * This class allows for the creation of JavaScript queries that can handle responses from the browser.
 *
 * @author Oleksandr Havrysh
 */
public abstract class HandledJSQuery extends SimpleJSQuery {

    /**
     * The JBCefJSQuery instance used to create and manage the JavaScript query.
     */
    protected final JBCefJSQuery jbCefJSQuery;

    /**
     * Constructs a new HandledJSQuery.
     *
     * @param browser The JBCefBrowser instance on which the JavaScript query will be executed.
     * @param handler A function that handles the response from the JavaScript query.
     *                It takes a String as input and returns a JBCefJSQuery.Response.
     */
    public HandledJSQuery(@NotNull JBCefBrowser browser,
                          @NotNull Function<? super String, ? extends JBCefJSQuery.Response> handler) {
        super(browser);
        this.jbCefJSQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
        jbCefJSQuery.addHandler(handler);
    }

    /**
     * Disposes of the resources held by this HandledJSQuery instance.
     * This method ensures that the JBCefJSQuery is properly disposed of when no longer needed.
     */
    @Override
    public void dispose() {
        jbCefJSQuery.dispose();
    }
}