import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class LinearProgram {
    private ObjectiveFunction objectiveFunction;
    private final ArrayList<Constraint> constraints;
    private boolean maximize;

    public LinearProgram(double[] objectiveCoefficients, boolean maximize) {
        this.constraints = new ArrayList<>();
        this.objectiveFunction = new ObjectiveFunction(objectiveCoefficients, maximize);
    }
    public void setObjectiveFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public void add(String type, double RHS, double... constraintArray) {
        int constraintType = -1; // Initialize to an invalid value for better debugging

        switch (type) {
            case "<=", "leq" -> constraintType = 0;
            case "=", "eq" -> constraintType = 1;
            case ">=", "geq" -> constraintType = 2;
            case null, default -> System.out.println("Unrecognized constraint type: " + type);
        }

        if (constraintType != -1) {
            Constraint constraint = new Constraint(constraintArray, RHS, constraintType);
            this.constraints.add(constraint);
            System.out.println("Added constraint: Type=" + constraintType + ", RHS=" + RHS + ", Array=" + Arrays.toString(constraintArray));
        }
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public ObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }

    // this gets the constraints of the LP
    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    // returns max or min, need for initialize tableau and potentially invert final solution (Z)
    public boolean isMaximize() {
        return maximize;
    }

    // gets the count of "abnormal" constraints, need for 2-phase method solver
    public int countConstraintsOfType(int type) {
        int count = 0;
        for (Constraint constraint : constraints) {
            if (constraint.getType() == type) {
                count++;
            }
        }
        return count;
    }

    // display LP in non-augmented form ("semantic" form ?)
    public void display() { // this method prints the linear program before any changes are made to it
        System.out.println(Arrays.toString(objectiveFunction.getCoefficients()) + " = " + 0);
        for (Constraint constraint : constraints) {
            String eq = null;
            if (constraint.getType() == 0) {
                eq = " <= ";
            } else if (constraint.getType() == 1) {
                eq = " = ";
            } else if (constraint.getType() == 2) {
                eq = " >= ";
            }
            System.out.println(Arrays.toString(constraint.getCoefficients()) + eq + constraint.getRHS());
        }
    }

    public void displayAugmentedForm() {
        // TO DO : PRINT TABLEAU OF SIMPLEX
        Tableau tableau = new Tableau(this);
        tableau.displayTableau();
    }

    public void solve() {
        Tableau primalTableau = new Tableau(this);
        primalTableau.solve();
    }
}

