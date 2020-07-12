package com.nemesis.mathcore.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.math.BigDecimal;

@Slf4j
public class ExponentialFunctionsTest {

    @Test
    public void nthRoot() {

        long start;
        long end;

        start = System.currentTimeMillis();
        final BigDecimal root_100 = ExponentialFunctions.nthRoot(new BigDecimal("21"), 2, 100);
        end = System.currentTimeMillis();

        long time_100 = end - start;

        start = end;
        final BigDecimal root_200 = ExponentialFunctions.nthRoot(new BigDecimal("21"), 2, 200);
        end = System.currentTimeMillis();

        long time_200 = end - start;

        start = end;
        final BigDecimal root_400 = ExponentialFunctions.nthRoot(new BigDecimal("21"), 2, 400);
        end = System.currentTimeMillis();

        long time_400 = end - start;

        start = end;
        final BigDecimal root_800 = ExponentialFunctions.nthRoot(new BigDecimal("21"), 2, 800);
        end = System.currentTimeMillis();

        long time_800 = end - start;

        start = end;
        final BigDecimal root_1600 = ExponentialFunctions.nthRoot(new BigDecimal("21"), 2, 1600);
        end = System.currentTimeMillis();

        long time_1600 = end - start;

        log.debug("100 iterations: [{}] ms, res: [{}]", time_100, root_100);
        log.debug("200 iterations: [{}] ms, res: [{}]", time_200, root_200);
        log.debug("400 iterations: [{}] ms, res: [{}]", time_400, root_400);
        log.debug("800 iterations: [{}] ms, res: [{}]", time_800, root_800);
        log.debug("1600 iterations: [{}] ms, res: [{}]", time_1600, root_1600);

//        ("√21", "4.582575694955840006588047193728008488984456576767971902607")
//        ; // 4.582575694955840006588047193728008488984456576767971902607...
//        ("∛21", "2.758924176381120669465791108358521582252712086038936032806")
//        ; // 2.758924176381120669465791108358521582252712086038936032806...
//        ("∜21", "2.140695142928072326546796300065136781766513555688408142096")
//        ; // 2.140695142928072326546796300065136781766513555688408142096...
    }
}