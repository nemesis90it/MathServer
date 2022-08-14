package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.utils.ExpressionUtils;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.nemesis.mathcore.expressionsolver.utils.Constants.E_CHAR;


public class ExpressionEvaluatorTest {

    @Test
    public void testEvaluate() {

        Map<String, String> tests = new LinkedHashMap<>();

        tests.put("1", "1");
        tests.put("-1", "-1");
        tests.put("1+2", "3");
        tests.put("-1+2", "1");
        tests.put("2*4+7", "15");
        tests.put("2*6/3+7", "11");
        tests.put("(-1)", "-1");
        tests.put("-(2+1)", "-3");
        tests.put("-(2+1)+5", "2");
        tests.put("-(2+1)-5", "-8");
        tests.put("-(2+1)-(5+1)", "-9");
        tests.put("-(2+6)/4", "-2");
        tests.put("1/-2", "-0.5");
        tests.put("-1/2", "-0.5");
        tests.put("1/2", "0.5");
        tests.put("6/2", "3");
        tests.put("(1)", "1");
        tests.put("(1+2)", "3");
        tests.put("(1/2)", "0.5");
        tests.put("(6/2)", "3");
        tests.put("60/2/3", "10");
        tests.put("60/(4/2)", "30");
        tests.put("3!", "6");
        tests.put("-3!", "-6");
        tests.put("3!+(2+2)!", "30");
        tests.put("(3+1)!+(2+2)!", "48");
        tests.put("(3+2)!", "120");
        tests.put("-(3+2)!", "-120");
        tests.put("(3+2)!+1", "121");
        tests.put("(1+2)!!", "720");
        tests.put("(1+2)!!!", "2601218943565795100204903227081043611191521875016945785727541837850835631156947382240678577958130457082619920575892247259536641565162052015873791984587740832529105244690388811884123764341191951045505346658616243271940197113909845536727278537099345629855586719369774070003700430783758997420676784016967207846280629229032107161669867260548988445514257193985499448939594496064045132362140265986193073249369770477606067680670176491669403034819961881455625195592566918830825514942947596537274845624628824234526597789737740896466553992435928786212515967483220976029505696699927284670563747137533019248313587076125412683415860129447566011455420749589952563543068288634631084965650682771552996256790845235702552186222358130016700834523443236821935793184701956510729781804354173890560727428048583995919729021726612291298420516067579036232337699453964191475175567557695392233803056825308599977441675784352815913461340394604901269542028838347101363733824484506660093348484440711931292537694657354337375724772230181534032647177531984537341478674327048457983786618703257405938924215709695994630557521063203263493209220738320923356309923267504401701760572026010829288042335606643089888710297380797578013056049576342838683057190662205291174822510536697756603029574043387983471518552602805333866357139101046336419769097397432285994219837046979109956303389604675889865795711176566670039156748153115943980043625399399731203066490601325311304719028898491856203766669164468791125249193754425845895000311561682974304641142538074897281723375955380661719801404677935614793635266265683339509760000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        tests.put("(1+2)!!+1", "721");
        tests.put("3!!", "720");
        tests.put("-3!!", "-720");
        tests.put("3!!+1", "721");
        tests.put("120/3!", "20");
        tests.put("5!/5", "24");
        tests.put("2.3+5.1", "7.4");
        tests.put("(2*4+7)/((30+2*1)-29)-1", "4");
        tests.put("(2*4+7)/((30+3!*2)-70)-1", "-1.5357142857142857");
        tests.put("3^2", "9");
        tests.put("-3^2", "-9");
        tests.put("3^2+1", "10");
        tests.put("3^2*5", "45");
        tests.put("3^2*(5+1)", "54");
        tests.put("3^2*4^5", "9216");
        tests.put("2^-3", "0.125");
        tests.put("2^(-3)", "0.125");
        tests.put("(2+4^3)^5", "1252332576");
        tests.put("(2+4^3)^5+1", "1252332577");
        tests.put("(2+4^3)^5/2", "626166288");
        tests.put("(2+4^3)+(3+5)^5/2", "16450");
        tests.put("3^(2*4)", "6561");
        tests.put("3^(2*4)+1", "6562");
        tests.put("3^(2*4)/3", "2187");
        tests.put("3^(2*4)/(3+1)", "1640.25");
        tests.put("(1+2)^(8/4+1)", "27");
        tests.put("(1+2)^(8/4+1)+1", "28");
        tests.put("(1+2)^(8/4+1)/3", "9");
        tests.put("(1+(6/3)+2)^(8/4+1)", "125");
        tests.put("((6/3)^(1+2))^(2+1)", "512");
        tests.put("4^3^2", "262144");
        tests.put("(4^3)^2", "4096");
        tests.put("4^-3^2", "0.0000038146972656");
        tests.put("-2^3^2", "-512");
        tests.put("-2^-3^2", "-0.001953125");
        tests.put("2^3^(2+1)", "134217728");
        tests.put("2^3^(2+1)+2^3", "134217736");
        tests.put("2^3^(2+1)/(2^3)", "16777216");
        tests.put("2^3^(2!+1)/(2^3)!", "3328.8126984126984127");
        tests.put("2^((3*2)-(1+2))", "8");
        tests.put("((1+2)+(3+4))^3", "1000");
        tests.put("((1+2)+(3+4))^3+1", "1001");
        tests.put("((4+2)/2+(3+4))^3+1", "1001");
        tests.put("((2+2)+(8+4)/2)^3", "1000");
        tests.put("((2+2)+(8+4)/2)^3+1", "1001");
        tests.put("log(10,100)", "2");
        tests.put("log(10,1000)!", "6");
        tests.put("-log(10,1000)!", "-6");
        tests.put("-log(10,100)", "-2");
        tests.put("ln(10)", "2.30258509299404568"); // TODO: why 17 decimals instead of 16?
        tests.put("1+log(10,100)", "3");
        tests.put("1+ln(10)", "3.30258509299404568"); // TODO: why 17 decimals instead of 16?
        tests.put("log(10,100)+1", "3");
        tests.put("ln(10)+1", "3.30258509299404568"); // TODO: why 17 decimals instead of 16?
        tests.put("2*log(10,100)+1", "5");
        tests.put("-2*log(10,100)+1", "-3");
        tests.put("(log(10,100))^3", "8");
        tests.put("-(log(10,100))^3", "-8");
        tests.put(String.valueOf(E_CHAR), "2.718281828459045");
        tests.put("π", "3.141592653589793"); // TODO: why 15 decimals instead of 16?
//        tests.put("ln(" + E_CHAR + ")", "1"); // TODO: manage scaling
        tests.put("log(10,100)^3", "8");
        tests.put("log(10,100)^(1+2)", "8");
        tests.put("log(10,100)^log(10,1000)", "8");
        tests.put("log(10,100)^log(10,1000)^2", "512");
        tests.put("3^log(10,1000)", "27");
        tests.put("3^4!", "282429536481");
        tests.put("(1+2)^log(10,1000)", "27");
        tests.put("(1+2)^3!", "729");
        tests.put("3!^(1+2)!", "46656");
        tests.put("3!^-(1+2)!", "0.0000214334705075");
        tests.put("4!^3!", "191102976");
        tests.put("-4!^3!", "-191102976");
        tests.put("(1+2)^2^3", "6561");
        tests.put("-(1+2)^2^3", "-6561");
        tests.put("20-4+5", "21");
        tests.put("20-4-5", "11");
        tests.put("20-4+1-5", "12");
        tests.put("20-(3*2)+1", "15");
        tests.put("20-log(10,100)+1", "19");
        tests.put("-20-1+1", "-20");
//        tests.put("3^(1/2)", "1.732050807568877"); TODO
        tests.put("√3", "1.732050807568877"); // TODO: why 15 decimals instead of 16?
        tests.put("∛3", "1.442249570307408"); // TODO: why 15 decimals instead of 16?
        tests.put("∜3", "1.316074012952492"); // TODO: why 15 decimals instead of 16?
        tests.put("|2-5|", "3");
        tests.put("|2-|-5||", "3");
        tests.put("|2-|-10|+|-5+1||", "4");
        tests.put("2(3+4)", "14");
        tests.put("2|3-4|", "2");


        // TODO: test complex logarithms
        // TODO: test all operations with decimal numbers

        MathCoreContext.setNumericMode(MathCoreContext.Mode.DECIMAL);
        this.doTestEvaluate(tests);

        // TODO
//        tests.put("1/2", "1/2");
//        tests.put("4/2", "2");
//        tests.put("4*x/2*x", "2");
//        tests.put("4*x/(2*y)", "???");

//        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);
//        this.doTestEvaluate(tests);

    }

    private void doTestEvaluate(Map<String, String> tests) {
        for (String expression : tests.keySet()) {
            String errorMessage = "ERROR ON EXPRESSION: " + expression;
            BigDecimal result = null;
            try {
                System.out.println("Testing [" + expression + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.evaluate(expression);
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000 + " µs\n");

            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(expression), result.toString());
        }
    }

}
