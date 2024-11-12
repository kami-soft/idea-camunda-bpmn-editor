package dev.camunda.bpmn.editor.util;

/**
 * A utility interface that holds constant values used throughout the BPMN Editor.
 *
 * <p>This interface provides a centralized location for various constant values,
 * such as numeric constants, string constants, and common callback query functions.</p>
 *
 * @author Oleksandr Havrysh
 */
public interface Constants {

    int ZERO = 0;
    String DOT = ".";
    String JS = "js";
    String EMPTY = "";
    int SUCCESS_CODE = 200;
    int NOT_FOUND_CODE = 404;
    String CONTEXT_PATH = "/";
    String SHA_256 = "SHA-256";
    String JAVASCRIPT = "javascript";
    String ORIGIN_BPMN = "originBpmn";

    /**
     * JavaScript function for handling successful responses in callback queries.
     */
    String SUCCESS_CALLBACK_QUERY = """
            function(response) {
                resolve(response);
            }""";

    /**
     * JavaScript function for handling failure responses in callback queries.
     */
    String FAILURE_CALLBACK_QUERY = """
            function(error_code, error_message) {
                reject(new Error(`Error ${error_code}: ${error_message}`));
            }""";
}