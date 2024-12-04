package dev.camunda.bpmn.editor.server.handler;

import dev.camunda.bpmn.editor.project.ClipboardManager;
import lombok.RequiredArgsConstructor;

/**
 * Handles HTTP requests related to clipboard operations in the BPMN Editor.
 * This handler is responsible for retrieving content from the clipboard
 * and sending it as a response to HTTP requests.
 *
 * <p>The ClipboardHandler extends AbstractServerHandler and works in conjunction
 * with the ClipboardService to provide clipboard functionality over HTTP.</p>
 *
 * <p>This handler is typically used to support copy-paste operations within
 * the BPMN Editor, allowing clipboard content to be accessed via HTTP requests.</p>
 *
 * @author Oleksandr Havrysh
 */
@RequiredArgsConstructor
public class ClipboardServerHandler extends AbstractServerHandler {

    private final ClipboardManager clipboardManager;

    /**
     * Retrieves the current content of the clipboard as a byte array.
     *
     * <p>This method overrides the abstract method from AbstractServerHandler.
     * It uses the injected ClipboardService to get the current clipboard content
     * and converts it to a byte array for HTTP transmission.</p>
     *
     * <p>Note that this method ignores the path parameter and always returns
     * the current clipboard content, regardless of the requested path.</p>
     *
     * @param path The path of the requested resource (ignored in this implementation).
     * @return A byte array containing the current clipboard content.
     */
    @Override
    protected byte[] getContent(String path) {
        return clipboardManager.getContent().getBytes();
    }
}