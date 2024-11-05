package dev.camunda.bpmn.editor.ui.component.table;

import static java.lang.Math.max;
import static java.util.Objects.isNull;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Utility class for automatically resizing the columns of a JTable to fit their content.
 *
 * <p>This class provides methods to adjust the column widths of a JTable based on the content
 * of the table cells and the table header. It ensures that the columns are wide enough to
 * display the content without truncation, while also considering a specified column margin.</p>
 *
 * @author Oleksandr Havrysh
 */
public class ColumnsAutoSizer {

    /**
     * Resizes the columns of the given table to fit their content with a default column margin of 5 pixels.
     *
     * @param table The JTable to resize
     */
    public static void sizeColumnsToFit(JTable table) {
        sizeColumnsToFit(table, 10);
    }

    /**
     * Resizes the columns of the given table to fit their content with the specified column margin.
     *
     * @param table        The JTable to resize
     * @param columnMargin The margin to add to the column width
     */
    public static void sizeColumnsToFit(JTable table, int columnMargin) {
        var tableHeader = table.getTableHeader();
        if (isNull(tableHeader)) {
            return;
        }

        var headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        var minWidths = new int[table.getColumnCount()];
        var maxWidths = new int[table.getColumnCount()];

        for (var columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            var headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));
            minWidths[columnIndex] = headerWidth + columnMargin;
            var maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth);
            maxWidths[columnIndex] = max(maxWidth, minWidths[columnIndex]) + columnMargin;
        }

        adjustMaximumWidths(table, minWidths, maxWidths);

        for (var i = 0; i < minWidths.length; i++) {
            if (minWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);
            }

            if (maxWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMaxWidth(maxWidths[i]);
                table.getColumnModel().getColumn(i).setWidth(maxWidths[i]);
            }
        }
    }

    /**
     * Adjusts the maximum widths of the columns to ensure they fit within the table's width.
     *
     * @param table     The JTable to adjust
     * @param minWidths The minimum widths of the columns
     * @param maxWidths The maximum widths of the columns
     */
    private static void adjustMaximumWidths(JTable table, int[] minWidths, int[] maxWidths) {
        if (table.getWidth() > 0) {
            var breaker = 0;

            while (sum(maxWidths) > table.getWidth() && breaker < 10000) {
                var highestWidthIndex = findLargestIndex(maxWidths);
                maxWidths[highestWidthIndex] -= 1;
                maxWidths[highestWidthIndex] = max(maxWidths[highestWidthIndex], minWidths[highestWidthIndex]);
                breaker++;
            }
        }
    }

    /**
     * Returns the maximum required width for a column based on its content and header width.
     *
     * @param table       The JTable to measure
     * @param columnIndex The index of the column to measure
     * @param headerWidth The width of the column header
     * @return The maximum required width for the column
     */
    private static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth) {
        var maxWidth = headerWidth;
        var column = table.getColumnModel().getColumn(columnIndex);
        var cellRenderer = column.getCellRenderer();
        if (isNull(cellRenderer)) {
            cellRenderer = new DefaultTableCellRenderer();
        }

        for (var row = 0; row < table.getModel().getRowCount(); row++) {
            var valueWidth = cellRenderer.getTableCellRendererComponent(table,
                            table.getModel().getValueAt(row, columnIndex),
                            false,
                            false,
                            row,
                            columnIndex)
                    .getPreferredSize()
                    .getWidth();

            maxWidth = (int) max(maxWidth, valueWidth);
        }

        return maxWidth;
    }

    /**
     * Finds the index of the largest value in an array.
     *
     * @param widths The array of widths
     * @return The index of the largest value
     */
    private static int findLargestIndex(int[] widths) {
        var largestIndex = 0;
        var largestValue = 0;
        for (var i = 0; i < widths.length; i++) {
            if (widths[i] > largestValue) {
                largestIndex = i;
                largestValue = widths[i];
            }
        }

        return largestIndex;
    }

    /**
     * Sums the values in an array.
     *
     * @param widths The array of widths
     * @return The sum of the values
     */
    private static int sum(int[] widths) {
        var sum = 0;
        for (var width : widths) {
            sum += width;
        }

        return sum;
    }
}