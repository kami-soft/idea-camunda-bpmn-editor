package dev.camunda.bpmn.editor.ui.component.notification;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.ui.EditorNotificationPanel;

public class ErrorNotification extends EditorNotificationPanel {

    public ErrorNotification(FileEditor fileEditor, String message) {
        super(fileEditor, Status.Error);
        text(message);
    }
}