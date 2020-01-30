package com.nemesis.mathcore.matrix;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.OFF;

public class SquareMatrix extends Matrix {

    private final static Logger logger;

    static {
        logger = Logger.getLogger(SquareMatrix.class.getName());
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.OFF);
        logger.addHandler(handler);
        logger.setLevel(OFF);
    }

    private int size;

    public SquareMatrix(BigDecimal[][] data) {
        super(data, 0, 0, data.length, data[0].length);
        if (data.length != data[0].length) {
            throw new IllegalArgumentException("Input must be a square matrix");
        }
        size = data.length;
    }

    private SquareMatrix(BigDecimal[][] data, int r, int c, int size) {
        super(data, r, c, size, size);
        this.size = size;
    }

    public SquareMatrix getSubMatrix(int row_offset, int column_offset, int size) {
        SquareMatrix subMatrix = new SquareMatrix(super.data, super.r + row_offset, super.c + column_offset, size);
        return subMatrix;
    }

    @Override
    public SquareMatrix add(Matrix m) {
        return new SquareMatrix(super.add(m).data);
    }

    @Override
    public SquareMatrix subtract(Matrix m) {
        return new SquareMatrix(super.subtract(m).data);
    }

    @Override
    public SquareMatrix multiply(Matrix m) {

        if (!(m instanceof SquareMatrix sm)) {
            throw new IllegalArgumentException("Input must be a square matrix");
        }

        if (((SquareMatrix) m).size == 1) {
            BigDecimal[][] matrixElement = {{this.get(0, 0).multiply(m.get(0, 0))}};
            return new SquareMatrix(matrixElement);
        }

        int hs = this.size / 2;

        SquareMatrix a11 = this.getSubMatrix(0, 0, hs);
        SquareMatrix a12 = this.getSubMatrix(0, hs, hs);
        SquareMatrix a21 = this.getSubMatrix(hs, 0, hs);
        SquareMatrix a22 = this.getSubMatrix(hs, hs, hs);

        SquareMatrix b11 = sm.getSubMatrix(0, 0, hs);
        SquareMatrix b12 = sm.getSubMatrix(0, hs, hs);
        SquareMatrix b21 = sm.getSubMatrix(hs, 0, hs);
        SquareMatrix b22 = sm.getSubMatrix(hs, hs, hs);

        Map<Integer, Supplier<SquareMatrix>> matrixSuppliers = new HashMap<>();

        matrixSuppliers.put(1, () -> a11.add(a22).multiply(b11.add(b22)));
        matrixSuppliers.put(2, () -> a21.add(a22).multiply(b11));
        matrixSuppliers.put(3, () -> a11.multiply(b12.subtract(b22)));
        matrixSuppliers.put(4, () -> a22.multiply(b21.subtract(b11)));
        matrixSuppliers.put(5, () -> a11.add(a12).multiply(b22));
        matrixSuppliers.put(6, () -> a21.subtract(a11).multiply(b11.add(b12)));
        matrixSuppliers.put(7, () -> a12.subtract(a22).multiply(b21.add(b22)));

        Map<Integer, Matrix> matrices = new ConcurrentHashMap<>();

        new ArrayList<>(matrixSuppliers.entrySet()).parallelStream().forEach(
                entry -> matrices.put(entry.getKey(), entry.getValue().get())
        );

        Matrix c11 = matrices.get(1).add(matrices.get(4)).subtract(matrices.get(5)).add(matrices.get(7));
        Matrix c12 = matrices.get(3).add(matrices.get(5));
        Matrix c21 = matrices.get(2).add(matrices.get(4));
        Matrix c22 = matrices.get(1).subtract(matrices.get(2)).add(matrices.get(3)).add(matrices.get(6));

        Matrix c1 = c11.horizontalMerge(c12);
        Matrix c2 = c21.horizontalMerge(c22);

        Matrix c = c1.verticalMerge(c2);

        return new SquareMatrix(c.data);
    }

}