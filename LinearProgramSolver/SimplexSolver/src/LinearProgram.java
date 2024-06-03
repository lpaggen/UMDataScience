import java.util.ArrayList;
import java.util.List;

public class LinearProgram {
    private ObjectiveFunction objectiveFunction;
    private ArrayList<Constraint> constraints;
    private boolean maximize;

    public LinearProgram(double[] objectiveCoefficients, boolean maximize) {
        this.constraints = new ArrayList<>();
        this.objectiveFunction = new ObjectiveFunction(objectiveCoefficients, maximize);
    }
    public void setObjectiveFunction(ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = objectiveFunction;
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public ObjectiveFunction getObjectiveFunction() {
        return objectiveFunction;
    }

    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }
}

