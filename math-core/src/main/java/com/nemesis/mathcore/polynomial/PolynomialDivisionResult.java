package com.nemesis.mathcore.polynomial;

/**
 * Created by Sebastiano Motta on 07/08/2018.
 */

public class PolynomialDivisionResult {

    private Polynomial q;
    private Polynomial r;

    public Polynomial getQ() {
        return q;
    }

    public void setQ(Polynomial q) {
        this.q = q;
    }

    public Polynomial getR() {
        return r;
    }

    public void setR(Polynomial r) {
        this.r = r;
    }

    public PolynomialDivisionResult(Polynomial q, Polynomial r) {
        this.q = q;
        this.r = r;
    }
}
