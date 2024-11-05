package dev.camunda.bpmn.editor.service.jsquery;

import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Manages a collection of SimpleJSQuery objects for initialization and execution.
 * This class is responsible for adding, executing, and disposing of JavaScript queries.
 *
 * <p>The InitJSQueryManager is designed to work with the JBCefBrowser and provides a way to manage
 * various JavaScript queries that interact with the BPMN editor and script files.</p>
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class InitJSQueryManager {

    private final List<InitJSQuery> initQueries;

    /**
     * Executes all initialization queries in the order they were added.
     * This method should be called when all queries need to be executed, typically during initialization.
     */
    public void executeInitQueries() {
        initQueries.forEach(InitJSQuery::executeQuery);
    }
}