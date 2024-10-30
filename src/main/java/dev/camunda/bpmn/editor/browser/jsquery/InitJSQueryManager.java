package dev.camunda.bpmn.editor.browser.jsquery;

import com.intellij.openapi.Disposable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;

/**
 * Manages a collection of SimpleJSQuery objects for initialization and execution.
 * This class is responsible for adding, executing, and disposing of JavaScript queries.
 *
 * @author Oleksandr Havrysh
 */
public class InitJSQueryManager implements Disposable {

    private final List<SimpleJSQuery> initQueries = new CopyOnWriteArrayList<>();

    /**
     * Adds a SimpleJSQuery to the list of initialization queries.
     *
     * @param simpleJsQuery The SimpleJSQuery to be added to the initialization list.
     * @return The InitJSQueryManager instance, for method chaining.
     */
    public InitJSQueryManager addInitQuery(@NotNull SimpleJSQuery simpleJsQuery) {
        initQueries.add(simpleJsQuery);
        return this;
    }

    /**
     * Executes all initialization queries in the order they were added.
     * This method should be called when all queries need to be executed, typically during initialization.
     */
    public void executeInitQueries() {
        initQueries.forEach(SimpleJSQuery::executeQuery);
    }

    /**
     * Disposes of all SimpleJSQuery objects in the initialization list.
     * This method is called when the JSQueryManager is no longer needed, ensuring proper cleanup.
     */
    @Override
    public void dispose() {
        initQueries.forEach(SimpleJSQuery::dispose);
        initQueries.clear();
    }
}