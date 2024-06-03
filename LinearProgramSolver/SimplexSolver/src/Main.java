import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        double[] objectiveCoefficients = {2.2, 4.9, 5.3, 6.1};
        boolean maximize = true;

        // can create an initial LP with objective function and maximize type
        LinearProgram lp = new LinearProgram(objectiveCoefficients, maximize);

        // creating and adding constraints like this
        double[] constraintCoefficients1 = {3.4, 6.3, 7.8, 2.1};
        double constraintRHS1 = 15.4;
        int constraintType1 = 0; // so here just assume we set to leq for simplicity
        Constraint constraint1 = new Constraint(constraintCoefficients1, constraintRHS1, constraintType1);
        lp.addConstraint(constraint1);

        double[] constraintCoefficients2 = {4.7, 9.3, 2.1, 9.6};
        double constraintRHS2 = 13.6;
        int constraintType2 = 0; // so here just assume we set to leq for simplicity
        Constraint constraint2 = new Constraint(constraintCoefficients2, constraintRHS2, constraintType2);
        lp.addConstraint(constraint2);

        // print to check if all runs correctly so far
        System.out.println(Arrays.toString(lp.getObjectiveFunction().getCoefficients()));
        for(Constraint constraint : lp.getConstraints()) {
            System.out.println(Arrays.toString(constraint.getCoefficients()));
        }
    }
}

