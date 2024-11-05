package dev.camunda.bpmn.editor.ui.component;

import static java.awt.BorderLayout.CENTER;

import com.intellij.openapi.Disposable;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

/**
 * A panel for the BPMN Editor.
 * This class extends JPanel and provides methods for adding and replacing components within the panel.
 * It implements the Disposable interface to ensure proper cleanup of resources.
 *
 * @author Oleksandr Havrysh
 */
public class BpmnEditorComponent extends JPanel implements Disposable {

    /**
     * Constructs a new BpmnEditorPanel with a BorderLayout.
     */
    public BpmnEditorComponent() {
        super(new BorderLayout());
    }

    /**
     * Adds the specified component to the center of the panel.
     *
     * @param comp The component to add
     */
    public void put(Component comp) {
        add(comp, CENTER);
    }

    /**
     * Removes all components from the panel and adds the specified component to the center.
     *
     * @param comp The component to set
     */
    public void set(Component comp) {
        removeAll();
        put(comp);
        revalidate();
        repaint();
    }

    /**
     * Disposes of the panel and its associated resources.
     * This method is called to ensure proper cleanup of resources.
     */
    @Override
    public void dispose() {
        removeAll();
    }
}