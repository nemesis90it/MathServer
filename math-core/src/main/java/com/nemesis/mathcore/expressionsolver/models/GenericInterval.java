package com.nemesis.mathcore.expressionsolver.models;

public interface GenericInterval extends Comparable<GenericInterval> {

    @Override
    default int compareTo(GenericInterval other) {

        return this.toString().compareTo(other.toString()); // TODO

//        DoubleDelimitersInterval otherInterval = (DoubleDelimitersInterval) other;
//
//        Comparator<String> delimitersComparator = (thisDelimiter, otherDelimiter) -> {
//            if (otherDelimiter.equals(Constants.NEG_INFINITY) || thisDelimiter.equals(Constants.NEG_INFINITY)) {
//                return 1;
//            }
//            if (otherDelimiter.equals(Constants.INFINITY) || thisDelimiter.equals(Constants.INFINITY)) {
//                return -1;
//            }
//            return -1 * new BigDecimal(thisDelimiter).compareTo(new BigDecimal(otherDelimiter));
//        };
//
//        final String thisLeft = this.getLeftDelimiter();
//        final String otherLeft = otherInterval.getLeftDelimiter();
//
//        // TODO
//
//        if (thisLeft == null) {
//            // compare right delimiters ?
//        }
//        if (otherLeft == null) {
//            // compare right delimiters ?
//        }
//
//        if (otherLeft == null ^ thisLeft == null) {
//            // compare right delimiters ?
//        }
//
//        if (otherLeft == null && thisLeft == null) {
//            // compare right delimiters
//        }
//
//        return delimitersComparator.compare(thisLeft, otherLeft);

    }

    String toLatex();

    interface GenericType {
        String getStringPattern();
        String getLatexPattern();
    }

}
