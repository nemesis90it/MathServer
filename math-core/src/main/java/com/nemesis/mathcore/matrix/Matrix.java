package com.nemesis.mathcore.matrix;

import java.math.BigDecimal;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    BigDecimal[][] data;
    int r; // row offset
    int c;
    private int columns;
    private int rows;

    public Matrix(BigDecimal[][] data) {
        this(data, 0, 0, data.length, data[0].length);
    }

    Matrix(BigDecimal[][] data, int r, int c, int rows, int columns) {
        this.data = data.clone();
        this.r = r;
        this.c = c;
        this.rows = rows;
        this.columns = columns;
    }

    public Matrix getSubMatrix(int row_offset, int column_offset, int columns, int rows) {
        return new Matrix(data, this.r + row_offset, this.c + column_offset, columns, rows);
    }

    public BigDecimal get(int r, int c) {
        if (r >= rows) {
            throw new IndexOutOfBoundsException(r);
        }
        if (c >= columns) {
            throw new IndexOutOfBoundsException(c);
        }
        int row = this.r + r;
        int column = this.c + c;
        return data[row][column];
    }

    public Matrix add(Matrix m) {
        // TODO: check size
        BigDecimal[][] result = new BigDecimal[rows][columns];
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                result[i][j] = data[r + i][c + j].add(m.get(i, j));
            }
        }
        return new Matrix(result);
    }

    public Matrix subtract(Matrix m) {
        // TODO: check size
        BigDecimal[][] result = new BigDecimal[rows][columns];
        for (int i = 0; i < m.getRows(); i++) {
            for (int j = 0; j < m.getColumns(); j++) {
                result[i][j] = data[r + i][c + j].subtract(m.get(i, j));
            }
        }
        return new Matrix(result);
    }

    public Matrix multiply(Matrix m) {
        throw new UnsupportedOperationException("TODO"); // classic multiply?
    }

    public Matrix horizontalMerge(Matrix matrix) {

        BigDecimal[][] result = new BigDecimal[this.rows][this.columns + matrix.columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = this.get(i, j);
            }
        }

        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result[i][j + columns] = matrix.get(i, j);
            }
        }

        return new Matrix(result);
    }


    public Matrix verticalMerge(Matrix matrix) {

        BigDecimal[][] result = new BigDecimal[this.rows + matrix.rows][this.columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                result[i][j] = this.get(i, j);
            }
        }

        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result[i + rows][j] = matrix.get(i, j);
            }
        }

        return new Matrix(result);
    }


    public synchronized String toString() {

        StringBuffer sb = new StringBuffer("\n");

        for (int i = r; i < r + rows; i++) {
            for (int j = c; j < c + columns; j++)
                sb.append(data[i][j]).append("    \t");
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.append("\n").toString();
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

}