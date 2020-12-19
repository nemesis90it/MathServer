package com.nemesis.mathcore.expressionsolver;

import com.nemesis.mathcore.expressionsolver.utils.Constants;
import com.nemesis.mathcore.expressionsolver.utils.MathCoreContext;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;


public class FunctionSimplifierTest {

    @Test
    public void testSimplify() {

        Map<String, String> tests = new LinkedHashMap<>();

        tests.put("1", "1");
        tests.put("1/2", "0.5");
        tests.put("x", "x");
        tests.put("-(-x)", "x");
        tests.put("-2(-x)", "2x");
        tests.put("x+1", "x+1");
        tests.put("1+x", "x+1");
        tests.put("2x", "2x");
        tests.put("x2", "2x");
        tests.put("(2x)(3x)", "6x^2");
        tests.put("(2x)+(3x)", "5x");
        tests.put("(2x)-(3x)", "-x");
        tests.put("2x(2x+3x)", "10x^2");
        tests.put("(2x)((2x)+(3x))", "10x^2");
        tests.put("(8x)+(2x)+(3x)", "13x");
        tests.put("(8x)(2x)+1+(3x)", "16x^2+3x+1");
        tests.put("(8x)(2x+1)+1+(3x)", "16x^2+11x+1");
        tests.put("(8y)+(2x)+(3x)", "5x+8y");
        tests.put("2(8y-x)", "-2x+16y");
        tests.put("2(8y+3x)", "6x+16y");
        tests.put("2(8y-3x)", "-6x+16y");
        tests.put("-2(8y+3x)", "-6x-16y");
        tests.put("2(-(8y+3x))", "-6x-16y");
        tests.put("(30x)/(15x)", "2");
        tests.put("-(-log(x))", "log(x)");
        tests.put("-(1-log(x))", "log(x)-1");
        tests.put("-log(x)+2log(x)", "log(x)");
        tests.put("-log(x)^2+2log(x)", "-log(x)^2+2log(x)");
        tests.put("log(x)+2log(x)^2", "2log(x)^2+log(x)");
        tests.put("(30log(x))/(15log(x))", "2");
        tests.put("(30log(x)^2)/(15log(x))", "2log(x)");
        tests.put("24/(2x)", "12/x");
        tests.put("24/(2x+6x)", "3/x");
        tests.put("(30x^4)/(15x)", "2x^3");
        tests.put("24/(2y+3x)", "24/(3x+2y)");
        tests.put("24/(2/y+3x)", "24/(2/y+3x)");
//        tests.put("24/(2/y+3x-2x)", "24/(x+2/y)"); // TODO: see SumSimilarMonomial
        tests.put("-24/(2/y+3x)", "-24/(2/y+3x)");
        tests.put("-24/-(2/y+3x)", "24/(2/y+3x)");
        tests.put("4x/x^2", "4/x");
        tests.put("4(2/y)", "8/y");
        tests.put("-4(-(2/y+3x))", "8/y+12x");
        tests.put("7x+4y-2x+2", "5x+4y+2");
        tests.put("7x+4y-2x+2-4y", "5x+2");
        tests.put("-7x+4y-2x+2-(4y)", "-9x+2");
        tests.put("-7log(x)+4y-2log(x)+2-4y", "-9log(x)+2");
        tests.put("-7log(x)+4y-2log(x)+2-5y+3x", "3x-y-9log(x)+2");
        tests.put("-7log(x)+4y-2log(x)+2-4y+3x", "3x-9log(x)+2");
        tests.put("(2x)+(3x)+(8y)", "5x+8y");
        tests.put("-2x^4+3x^7+8y", "3x^7-2x^4+8y");
        tests.put("-(2x^4)+(3x^7)", "3x^7-2x^4");
        tests.put("-(2x^4)-(3x^7)", "-3x^7-2x^4");
        tests.put("(x^2)^4", "x^8");
        tests.put("x^1", "x");
        tests.put("x^0", "1");
        tests.put("x+3!-log(x)", "x-log(x)+6");
        tests.put("x+(2+1)!-log(x)", "x-log(x)+6");
        tests.put("x+(2+1)!-log(100)", "x+4");
        tests.put("x+(2+1)!-log(100)^3", "x-2");
        tests.put("log(x^y)", "ylog(x)");
        tests.put("ln(" + Constants.E_CHAR + ")", "1");
        tests.put("log(10)", "1");
        tests.put("log(1)", "0");
        tests.put("log(x/x)", "0");
        tests.put("8log(y)/(2log(y))", "4");
        tests.put("8x+8log(y)/(2log(y))", "8x+4");
        tests.put("8x-3|2y-3x-5x|", "-3|-8x+2y|+8x");
        tests.put("8x-2|2y|+3|2y|-1", "|2y|+8x-1");
        tests.put("8x-|2|2y|+3|2y||-1", "-|5|2y||+8x-1");

//        tests.put("-(-x)^(-(725))+(-z)-(-z)^8/(-y)", "");
//        tests.put("(-z)^672/z", "z^671");


//        tests.put("(-(root(2-th,260)31^2))/-984", "15.7476213335668825"); // TODO: FIX ROUNDING
//        tests.put("(-y)^-61/628", "");
//        tests.put("45!/(-y)^-805", "");
//        tests.put("(-x)-805.9894819166412-636-681-915+898.9730671981358+185-450", "");
//        tests.put("-(-(-300.33140269592127))^654.3329086881552(-x)^605.0640803698778/800.6447609406129", "");
//        tests.put("-ln(-((-z)-257))/(-z)-√710", "");
//        tests.put("-(-y)^-124/z/(-x)", "");
//        tests.put("z", "");
//        tests.put("(-z)^760.8503828393979/454/y", "");
//        tests.put("y/(-z)/-42^877/-cos(-2.126610993367748)", "");
//        tests.put("ctg(4.329813835474953)", "");
//        tests.put("(-x)!-490/-958.7754871295377-530", "");
//        tests.put("-(-y)^x-6.284855462286965^x+x!", "");
//        tests.put("238^(-z)/112+y-275", "");
//        tests.put("-(-x)^581.8484290528733+885!", "");
//        tests.put("-ln(135)/y", "");
//        tests.put("-394+500-196.05832930301526", "");
//        tests.put("(-y)^-ctg(-2.494210822265269)-663.953976555784+149", "");
//        tests.put("(90.74081595797911/(811(-y)^777))/(27!(-x))+541", "");
//        tests.put("x^(68)/(19401)", "");
//        tests.put("269!", "");
//        tests.put("(-z)^x-(-957)!", "");
//        tests.put("-(-z)^-706", "");
//        tests.put("-318+433!", "");
//        tests.put("-|-890|+(-y)-(-z)^799/467.25083730386086+(-y)^-14+519/974", "");
//        tests.put("tan(3.650906721097014)27+323^813", "");
//        tests.put("604-802^z", "");
//        tests.put("(-x)^-365-ln(-(-422))-128", "");
//        tests.put("ctg(-3.549206777971449)+189/(-178(-z)^307)", "");
//        tests.put("(-x)", "");
//        tests.put("-(-y)^-335.27664787808396(-y)^631", "");
//        tests.put("(-z)/-(-z)^-729.4554855451387/-310-177.71143777072984", "");
//        tests.put("y+134^820", "");
//        tests.put("-945.0186340630752^108y/55-541.1783317918237", "");
//        tests.put("358!(-y)^808", "");
//        tests.put("yy+(-z)+196+997", "");
//        tests.put("x749", "");
//        tests.put("818!+639597.9145457926439", "");
//        tests.put("(-y)ln(|-958|)/(-z)^584+y!+537396", "");
//        tests.put("-964^(-(-948))+y!", "");
//        tests.put("0", "");
//        tests.put("(-y)^462-(-y)^z989.6272801102537y", "");
//        tests.put("(-x)^91.71200651247824", "");
//        tests.put("269^814/-360+(-z)x97", "");
//        tests.put("(-x)^-379.9004526295852", "");
//        tests.put("x^-591/root(1-th,-917)+230-ln(|-948.6146027393127|)", "");
//        tests.put("-cosec(-0.052658204338161596)+x^515", "");
//        tests.put("(-x)/-148.18635754740305+z-ln(|15|)", "");
//        tests.put("(-y)^596", "");
//        tests.put("(-x)+89!-68/(-y)+765", "");
//        tests.put("(-z)^x+x-cos(-3.2787597968339255)/-185+81.3808670429994", "");
//        tests.put("-root(1-th,-383.6131679161735)/(-y)-|-55|", "");
//        tests.put("833!/(-(-x)^36.87904379527029270)", "");
//        tests.put("z^-184/139+(-y)^(-(-314))554", "");
//        tests.put("(-y)/-147", "");
//        tests.put("(-x)/(-x)^-179.18945553114096+(-z)x!", "");
//        tests.put("-x^478", "");
//        tests.put("z+y", "");
//        tests.put("(-x)^188/-620/-95+x-32", "");
//        tests.put("-z^-628.800152562715/(x36)", "");
//        tests.put("(-(-√x+940!))+486+x", "");
//        tests.put("(-z)^(-z)/503!", "");
//        tests.put("(-y)-(-x)^(-z)-(-y)^(-z)+(-y)^640", "");
//        tests.put("-(-y)^893-809+346-161-81.98961402677118", "");
//        tests.put("-(-x)^-326/578.4866150529595/(|144198!|-(-x)^-234.19487716350073)+579^402", "");
//        tests.put("572^x/(zz)", "");
//        tests.put("171!+x!-94.19236518504681+x", "");
//        tests.put("299.45562335590324^(-(-82.80586948942081))608-56^829.2335143646949", "");
//        tests.put("(-y)-692.6952255417067", "");
//        tests.put("-(-z)^497.4556011629686-(-z)^-319.8014856121607", "");
//        tests.put("-cot(3.217832277934951)^614.1068416503881/y", "");
//        tests.put("x/((-y)^-873.9157607681276321)-575.1068213671902", "");
//        tests.put("-(-z)^552z", "");
//        tests.put("y+207", "");
//        tests.put("-|-608.6452105077004/z|", "");
//        tests.put("(-z)-root(1-th,527)+8", "");
//        tests.put("(-y)+843", "");
//        tests.put("(-(-552))-(-y)^-855/-214+(-((-y)/126.82911660784724))^375", "");
//        tests.put("-188^767/cotg(-4.428022445912534)", "");
//        tests.put("-z^-349.5792292596327(-(310.99394619627685-692))^86-465-991484^430.8699253516486", "");
//        tests.put("(-y)^-770.1002630667762/x+893.9865568043739+172!", "");
//        tests.put("-y^368764.2077531219489", "");
//        tests.put("(-y)/332^(-z)+550", "");
//        tests.put("-(-y)^-851z!", "");
//        tests.put("893.2970867667392^856.5390362424336", "");
//        tests.put("z/(308^377612)-337.7918833836262^334(-y)", "");
//        tests.put("|614.0052357569583|", "");
//        tests.put("cos(-1.740043720795188)/-993", "");
//        tests.put("-(-z)^797-√801+(-y)/(-z)/-773", "");
//        tests.put("(-z)^379/473/-423+(-z)^-896/-87^x", "");
//        tests.put("(-(653.0136350411354))x-469.545447927261", "");
//        tests.put("x!-csc(0.8386715054759106)/-567", "");
//        tests.put("19.71391539656542^(-y)/-280^750.618190292821", "");


        MathCoreContext.setNumericMode(MathCoreContext.Mode.DECIMAL);
        this.doTestSimplify(tests);
//
        tests.put("1/2", "1/2");
        tests.put("2/4", "1/2");
        tests.put("4/2", "2");
        tests.put("4x/2x", "2");
////        tests.put("4x/(2y)", "???");

        MathCoreContext.setNumericMode(MathCoreContext.Mode.FRACTIONAL);
        this.doTestSimplify(tests);

    }

    private void doTestSimplify(Map<String, String> tests) {
        for (String function : tests.keySet()) {
            String errorMessage = "ERROR ON FUNCTION: " + function;
            String result = null;
            try {
                System.out.println("\nTesting [" + function + "]");
                long start = System.nanoTime();
                result = ExpressionUtils.simplify(function).toString();
                long stop = System.nanoTime();
                System.out.println("Elapsed time: " + (stop - start) / 1000000d + " ms");
                System.out.println(function + " -> " + result);
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail(errorMessage);
            }
            Assert.assertEquals(errorMessage, tests.get(function), result.toString());
        }
    }

}
