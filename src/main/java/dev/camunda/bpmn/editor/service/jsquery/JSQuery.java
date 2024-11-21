package dev.camunda.bpmn.editor.service.jsquery;

import static java.util.Objects.nonNull;

import com.intellij.openapi.Disposable;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.util.Alarm;
import dev.camunda.bpmn.editor.service.browser.JBCefBrowserWrapper;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

/**
 * Represents an initialization JavaScript query for a JBCefBrowser instance.
 * This class provides functionality to create, manage, and execute JavaScript queries
 * in a JBCefBrowser, with support for both synchronous and asynchronous operations.
 *
 * <p>The InitJSQuery class supports two types of query execution:
 * <ul>
 *   <li>Synchronous execution with immediate response handling</li>
 *   <li>Asynchronous execution with delayed response handling</li>
 * </ul>
 *
 * <p>This class implements {@link Disposable} to ensure proper cleanup of resources
 * when the query is no longer needed.
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class JSQuery implements Disposable {

    private static final String SUCCESS_CALLBACK_QUERY = """
            function(response) {
                resolve(response);
            }""";
    private static final String FAILURE_CALLBACK_QUERY = """
            function(error_code, error_message) {
                reject(new Error(`Error ${error_code}: ${error_message}`));
            }""";
    private static final String PROMISE_JS_FUNCTION = """
            window.%s = function(text) {
                return new Promise((resolve, reject) => {
                    %s
                });
            }""";
    private static final String JS_FUNCTION = """
            window.%s = function(text) {
                %s
            }""";
    private static final String QUERY_RESULT = "text";

    private final String query;
    private final JBCefBrowserWrapper browser;

    private Alarm alarm;
    private JBCefJSQuery jbCefJSQuery;

    /**
     * Constructs an InitJSQuery instance for synchronous execution.
     * This constructor creates a query that returns a Promise in JavaScript,
     * allowing for immediate handling of the response.
     *
     * @param browser      The JBCefBrowserWrapper instance to execute the query on
     * @param functionName The name of the JavaScript function to be created in the browser's window object
     * @param handler      A function that handles the response from the JavaScript query
     */
    public JSQuery(JBCefBrowserWrapper browser,
                   String functionName,
                   Function<String, String> handler) {
        this.browser = browser;
        this.jbCefJSQuery = browser.createJBCefJSQuery(request -> new JBCefJSQuery.Response(handler.apply(request)));
        var queryCallback = jbCefJSQuery.inject(QUERY_RESULT, SUCCESS_CALLBACK_QUERY, FAILURE_CALLBACK_QUERY);
        this.query = PROMISE_JS_FUNCTION.formatted(functionName, queryCallback);
    }

    /**
     * Constructs an InitJSQuery instance for asynchronous execution with delayed response handling.
     * This constructor creates a query that executes a JavaScript function and handles the response
     * after a specified delay.
     *
     * @param browser      The JBCefBrowserWrapper instance to execute the query on
     * @param functionName The name of the JavaScript function to be created in the browser's window object
     * @param handler      A consumer that handles the response from the JavaScript query
     * @param delayMillis  The delay in milliseconds before the handler is executed
     */
    public JSQuery(JBCefBrowserWrapper browser,
                   String functionName,
                   Consumer<String> handler,
                   int delayMillis) {
        this.browser = browser;
        this.alarm = new Alarm(this);
        this.jbCefJSQuery = browser.createJBCefJSQuery(response -> {
            alarm.cancelAllRequests();
            alarm.addRequest(() -> handler.accept(response), delayMillis);
            return new JBCefJSQuery.Response(null);
        });
        this.query = JS_FUNCTION.formatted(functionName, jbCefJSQuery.inject(QUERY_RESULT));
    }

    /**
     * Executes the JavaScript query on the associated JBCefBrowser.
     * This method injects and executes the prepared JavaScript function in the browser's context.
     * The query is executed at the current URL of the browser.
     */
    public void executeQuery() {
        browser.executeQuery(query);
    }

    /**
     * Disposes of the resources associated with this InitJSQuery instance.
     * This method ensures that the JBCefJSQuery and Alarm (if used) are properly disposed of,
     * preventing memory leaks and resource issues.
     * It's automatically called when the object is no longer needed if managed by a disposer.
     */
    @Override
    public void dispose() {
        if (nonNull(jbCefJSQuery)) {
            jbCefJSQuery.dispose();
        }
    }
}