package pl.genschu.bloomooemulator.interpreter.variable.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DatabaseState {
    private List<String> columns = List.of();
    private List<List<String>> data = new ArrayList<>();
    private int currentRow = 0;

    public List<String> columns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns == null ? List.of() : List.copyOf(columns);
    }

    public List<List<String>> data() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = (data == null) ? new ArrayList<>() : new ArrayList<>(data);
        if (currentRow >= this.data.size()) currentRow = 0;
    }

    public int rowsNo() {
        return data.size();
    }

    public int currentRowIndex() {
        return currentRow;
    }

    public List<String> currentRow() {
        if (data.isEmpty()) return List.of();
        if (currentRow < 0 || currentRow >= data.size()) return List.of();
        return data.get(currentRow);
    }

    public void select(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            currentRow = rowIndex;
        }
    }

    public void next() {
        if (data.isEmpty()) {
            currentRow = 0;
            return;
        }
        currentRow++;
        if (currentRow >= data.size()) currentRow = 0;
    }

    public void removeAll() {
        data.clear();
        currentRow = 0;
    }

    public int find(String columnName, String columnValue, int defaultIndex) {
        int colIdx = getColumnIndex(columnName);
        if (colIdx < 0) return defaultIndex;

        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            String cell = (colIdx < row.size()) ? row.get(colIdx) : "";
            if (cell != null && cell.equalsIgnoreCase(Objects.toString(columnValue, ""))) {
                return i;
            }
        }
        return defaultIndex;
    }

    private int getColumnIndex(String columnName) {
        if (columnName == null) return -1;
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).equalsIgnoreCase(columnName)) return i;
        }
        return -1;
    }

    /**
     * Update current row using values from STRUCT in column order.
     * Minimal: expects a list of strings sized like columns (or smaller).
     */
    public void updateCurrentRow(List<String> newValues) {
        if (data.isEmpty()) return;
        if (currentRow < 0 || currentRow >= data.size()) return;

        List<String> row = new ArrayList<>(data.get(currentRow));
        for (int i = 0; i < newValues.size(); i++) {
            if (i < row.size()) row.set(i, newValues.get(i));
            else row.add(newValues.get(i));
        }
        data.set(currentRow, row);
    }
}
