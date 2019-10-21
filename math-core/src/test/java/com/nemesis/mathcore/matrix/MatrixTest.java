//package com.nemesis.mathcore.matrix;
//
//import java.math.BigDecimal;
//
//public class MatrixTest {
//
//    public static void main(String[] args) throws Exception {
//
//        BigDecimal[][] a = {
//                {new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4)},
//                {new BigDecimal(5), new BigDecimal(6), new BigDecimal(7), new BigDecimal(8)},
//                {new BigDecimal(9), new BigDecimal(10), new BigDecimal(11), new BigDecimal(12)},
//                {new BigDecimal(13), new BigDecimal(14), new BigDecimal(15), new BigDecimal(16)},
//        };
//        BigDecimal[][] b = {
//                {new BigDecimal(11), new BigDecimal(12), new BigDecimal(13), new BigDecimal(14)},
//                {new BigDecimal(15), new BigDecimal(16), new BigDecimal(17), new BigDecimal(18)},
//                {new BigDecimal(19), new BigDecimal(110), new BigDecimal(111), new BigDecimal(112)},
//                {new BigDecimal(113), new BigDecimal(114), new BigDecimal(115), new BigDecimal(116)},
//        };
//
//
////        BigDecimal[][] a = {
////                {new BigDecimal(1), new BigDecimal(3)},
////                {new BigDecimal(7), new BigDecimal(5)},
////        };
////        BigDecimal[][] b = {
////                {new BigDecimal(6), new BigDecimal(8)},
////                {new BigDecimal(4), new BigDecimal(2)},
////        };
//
//        SquareMatrix squareMatrix_a = new SquareMatrix(a);
//        SquareMatrix squareMatrix_b = new SquareMatrix(b);
//        SquareMatrix m = squareMatrix_a.multiply(squareMatrix_b);
//        System.out.println("Strassen version:");
//        System.out.println(m);
//        System.out.println();
//
//        int size = a.length;
//
//        BigDecimal[][] m2 = new BigDecimal[size][size];
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                BigDecimal temp = BigDecimal.ZERO;
//                for (int k = 0; k < size; k++) {
//                    temp = temp.add(a[i][k].multiply(b[k][j]));
//                }
//                m2[i][j] = temp;
//            }
//        }
//
//        System.out.println("Classic version:");
//
//        for (int r = 0; r < size; r++) {
//            for (int c = 0; c < size; c++) {
//                System.out.print(m2[r][c] + " \t");
//            }
//            System.out.println();
//        }
//
//        System.out.println();
//
//    }
//
//}
