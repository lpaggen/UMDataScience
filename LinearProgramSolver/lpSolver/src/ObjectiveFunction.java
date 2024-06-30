public class ObjectiveFunction {
    private double[] coefficients; // list of coefficients of the constraint
    private boolean maximize; // either True for max, False for min

    public ObjectiveFunction(double[] coefficients, boolean maximize) {
        this.coefficients = coefficients;
        this.maximize = maximize;
    }

    public void setCoefficients(double[] coefficients) { // method to set coefficients of obj
        this.coefficients = coefficients;
    }

    public double[] getCoefficients() { // this just returns a list of the coefficients in obj
        return coefficients;
    }

    public void setMaximize(boolean maximize) { // this lets us set if problem is max or min
        this.maximize = maximize;
    }

    public boolean isMaximize() { // this returns if the problem is max or min (T/F)
        return maximize;
    }
}
