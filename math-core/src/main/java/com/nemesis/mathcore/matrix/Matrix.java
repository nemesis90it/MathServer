package com.nemesis.mathcore.matrix;

import com.nemesis.mathcore.expressionsolver.utils.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.components.Component;
import com.nemesis.mathcore.expressionsolver.components.Expression;
import com.nemesis.mathcore.expressionsolver.components.Term;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUBTRACT;
import static com.nemesis.mathcore.expressionsolver.operators.ExpressionOperator.SUM;
import static java.util.logging.Level.OFF;

public class Matrix {

    private final static Logger logger;

    static {
        logger = Logger.getLogger(Matrix.class.getName());
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.OFF);
        logger.addHandler(handler);
        logger.setLevel(OFF);
    }

    Component[][] data;

    public Matrix(Component[][] data) {
        this(data, 0, 0, data.length, data[0].length);
    }

    Matrix(Component[][] data, int row_offset, int column_offset, int rows, int columns) {
        this.data = new Component[rows][columns];
        for (int i = row_offset; i < rows; i++) {
            for (int j = column_offset; j < columns; j++) {
                this.data[i][j] = data[i][j].getClone();
            }
        }
    }

    public Matrix getSubMatrix(int row_offset, int column_offset, int columns, int rows) {
        return new Matrix(data, row_offset, column_offset, columns, rows);
    }

    public Component get(int r, int c) {
        return data[r][c];
    }

    public Matrix add(Matrix m) {
        // TODO: check size
        Component[][] result = new Component[m.getRows()][m.getColumns()];
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                result[i][j] = ExpressionUtils.simplify(new Expression(Term.getTerm(data[i][j]), SUM, new Expression(Term.getTerm(m.get(i, j)))));
            }
        }
        return new Matrix(result);
    }

    public Matrix subtract(Matrix m) {
        // TODO: check size
        Component[][] result = new Component[m.getRows()][m.getColumns()];
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                result[i][j] = ExpressionUtils.simplify(new Expression(Term.getTerm(data[i][j]), SUBTRACT, new Expression(Term.getTerm(m.get(i, j)))));
            }
        }
        return new Matrix(result);
    }

    public Matrix multiply(Matrix m) {
        throw new UnsupportedOperationException("Not implemented"); // TODO classic multiply?
    }

    public Matrix horizontalMerge(Matrix other) {

        final int thisRows = this.getRows();
        final int thisColumns = this.getColumns();
        final int otherColumns = other.getColumns();
        final int otherRows = other.getRows();

        Component[][] result = new Component[thisRows][thisColumns + otherColumns];

        for (int i = 0; i < thisRows; i++) {
            for (int j = 0; j < thisColumns; j++) {
                result[i][j] = this.get(i, j);
            }
        }

        for (int i = 0; i < otherRows; i++) {
            for (int j = 0; j < otherColumns; j++) {
                result[i][thisColumns + j] = other.get(i, j);
            }
        }

        return new Matrix(result);
    }


    public Matrix verticalMerge(Matrix other) {

        final int thisRows = this.getRows();
        final int otherRows = other.getRows();
        final int thisColumns = this.getColumns();
        final int otherColumns = other.getColumns();

        Component[][] result = new Component[thisRows + otherRows][thisColumns];

        for (int i = 0; i < thisRows; i++) {
            for (int j = 0; j < thisColumns; j++) {
                result[i][j] = this.get(i, j);
            }
        }

        for (int i = 0; i < otherRows; i++) {
            for (int j = 0; j < otherColumns; j++) {
                result[thisRows + i][j] = other.get(i, j);
            }
        }

        return new Matrix(result);
    }


    public synchronized String toString() {

        StringBuilder sb = new StringBuilder("\n");

        final int rows = this.getRows();
        final int columns = this.getColumns();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                sb.append(data[i][j]).append("    \t");
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.append("\n").toString();
    }

    public int getColumns() {
        return data[0].length;
    }

    public int getRows() {
        return data.length;
    }

}