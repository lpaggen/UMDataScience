public class Constraint {
    private final double rhs; // RHS of constraint
    private final double[] coefficients; // list of coefficients in constraint
    private final int type; // 0, 1, 2 -> leq, eq, geq

    public Constraint(double[] coefficients, double rhs, int type) {
        this.rhs = rhs;
        this.coefficients = coefficients;
        this.type = type;
    }

    public double getRHS() {
        return rhs; // return RHS of constraint
    }

    public double[] getCoefficients() {
        return coefficients; // return list of coefficients
    }

    public int getType() {
        return type; // return leq eq geq
    }
}

