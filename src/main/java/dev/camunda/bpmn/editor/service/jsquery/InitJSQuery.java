package dev.camunda.bpmn.editor.service.jsquery;

import static java.util.Objects.nonNull;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.util.Alarm;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;

/**
 * An abstract class that provides functionality to initialize and manage JavaScript queries
 * in a JBCefBrowser instance. This class implements {@link Disposable} to ensure proper cleanup
 * of resources.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public abstract class InitJSQuery implements Disposable {

    private final String query;
    private final JBCefBrowserWrapper browser;

    private Alarm alarm;
    private JBCefJSQuery jbCefJSQuery;

    /**
     * Constructs an InitJSQuery instance with the specified browser, query function, and handler.
     *
     * @param browser       The JBCefBrowserWrapper instance
     * @param queryFunction A function that generates the JavaScript query string
     * @param handler       A function that handles the response from the JavaScript query
     */
    protected InitJSQuery(JBCefBrowserWrapper browser,
                          Function<JBCefJSQuery, String> queryFunction,
                          Function<? super String, ? extends String> handler) {
        this.browser = browser;
        this.jbCefJSQuery = browser.createJBCefJSQuery(request -> new JBCefJSQuery.Response(handler.apply(request)));
        this.query = queryFunction.apply(jbCefJSQuery);
    }

    /**
     * Constructs an InitJSQuery instance with the specified browser, query function, handler, and delay.
     *
     * @param browser       The JBCefBrowserWrapper instance
     * @param queryFunction A function that generates the JavaScript query string
     * @param handler       A consumer that handles the response from the JavaScript query
     * @param delayMillis   The delay in milliseconds before the handler is executed
     */
    protected InitJSQuery(JBCefBrowserWrapper browser,
                          Function<JBCefJSQuery, String> queryFunction,
                          Consumer<? super String> handler,
                          int delayMillis) {
        this.browser = browser;
        this.alarm = new Alarm(this);
        this.jbCefJSQuery = browser.createJBCefJSQuery(response -> {
            alarm.cancelAllRequests();
            alarm.addRequest(() -> handler.accept(response), delayMillis);
            return new JBCefJSQuery.Response(null);
        });

        this.query = queryFunction.apply(jbCefJSQuery);
    }

    /**
     * Executes the JavaScript query on the associated JBCefBrowser.
     * The query is executed at the current URL of the browser.
     */
    public void executeQuery() {
        browser.executeQuery(query);
    }

    /**
     * Disposes of the JBCefJSQuery instance.
     * This method ensures that the resources associated with the query are properly released.
     */
    @Override
    @PreDestroy
    public void dispose() {
        if (nonNull(jbCefJSQuery)) {
            jbCefJSQuery.dispose();
        }
    }
}