package lab2;

public class Common {
    public static int s2i(String s) {
        int i = 0;

        try {
            i = Integer.parseInt(s.trim());
        } catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        return i;
    }

    public static double R1() {
        java.util.Random generator = new java.util.Random(System.currentTimeMillis());

        double U = generator.nextDouble();
        double V = generator.nextDouble();
        double X = Math.sqrt((8 / Math.E)) * (V - 0.5) / U;

        if (!(R2(X, U)) || !(R3(X, U) || !(R4(X, U)))) {
            return -1;
        }

        return X;
    }

    public static boolean R2(double X, double U) {
        return (X * X) <= (5 - 4 * Math.exp(.25) * U);
    }

    public static boolean R3(double X, double U) {
        return !((X * X) >= (4 * Math.exp(-1.35) / U + 1.4));
    }

    public static boolean R4(double X, double U) {
        return (X * X) < (-4 * Math.log(U));
    }
}

