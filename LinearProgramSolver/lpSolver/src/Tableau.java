import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// Tableau should be an interface of some sort
public class Tableau {
    private double[][] tableau;
    private boolean twoPhase; // if 2 phase, need to solve differently than regular simplex
    private final int numConstraints;
    private final int numVariables;
    private final int countGeq;
    private final int countEq;
    double[] objectiveCoefficients;
    int[] artificialVariablesIndex; // save index of artificial variables to check optimality
    private double[] originalObjective;

    // when initializing the Tableau of the LP, we can have either primal or dual
    public Tableau(LinearProgram lp) {
        this.twoPhase = lp.countConstraintsOfType(1) > 0 || lp.countConstraintsOfType(2) > 0; // 2phase if ANY eq or geq
        this.numConstraints = lp.getConstraints().size();
        this.numVariables = lp.getObjectiveFunction().getCoefficients().length;
        this.countEq = lp.countConstraintsOfType(1); // " = " constraints, +1 artificial variable instead of slack
        this.countGeq = lp.countConstraintsOfType(2); // " >= " constraints, -1 surplus + 1 artificial
        this.objectiveCoefficients = lp.getObjectiveFunction().getCoefficients();
        this.artificialVariablesIndex = new int[countEq + countGeq + 1]; // init with number of artificial variables
        initializeTableau(lp);
    }

    public void displayTableau() {
        ArrayList<String> variablesIndicator = new ArrayList<>();
        int tableauLength = tableau[0].length;
        for (int i = 0; i < tableauLength - 1; i++) {
            variablesIndicator.add(" X" + (i + 1));
        }
        variablesIndicator.add("RHS");
        System.out.println(variablesIndicator);
        for (double[] row : tableau) {
            System.out.println(Arrays.toString(row));
        }
    }

    // this method implements the classic pivot operation in the tableau
    public void pivot(int idxEnterVarCol, int idxEnterVarRow) {
        double enteringVariable = tableau[idxEnterVarRow][idxEnterVarCol];

        // need to divide by entering variable
        for (int k = 0; k < tableau[0].length; k++) {
            tableau[idxEnterVarRow][k] /= enteringVariable;
        }

        // Update all other rows
        for (int i = 0; i < tableau.length; i++) {
            if (i != idxEnterVarRow) { // avoid row where variable enters basis
                double pivotRatio = tableau[i][idxEnterVarCol]; // ratio to multiply
                for (int j = 0; j < tableau[0].length; j++) { // iterate over all variables in row
                    tableau[i][j] -= pivotRatio * tableau[idxEnterVarRow][j];
                }
            }
        }
    }

    // this method finds the row index of the entering variable
    private int[] getEnterVar() {
        int enterVarIdxCol = MiscFunctions.indexOfSmallest(tableau[0]);
        int enterVarIdxRow;
        double[] ratioList = new double[numConstraints];
        double ratio;

        // for loop iterate over column and find smallest ratio
        for (int i = 0; i < numConstraints; i++) {
            double RHS = tableau[i + 1][tableau[0].length - 1]; // get RHS of constraint i
            if (tableau[i + 1][enterVarIdxCol] > 0) {
                ratio = RHS / tableau[i + 1][enterVarIdxCol];
            } else {
                ratio = Double.MAX_VALUE; // some big number to discourage it from being selected
            }
            ratioList[i] = ratio;
        }
        enterVarIdxRow = MiscFunctions.indexOfSmallest(ratioList) + 1;
        return new int[]{enterVarIdxRow, enterVarIdxCol};
    }

    public int[] getEnterVarPhase1() {
        // need to get index of the entering variable (in this case the first 1 in the Z row)
        int enterVarIdxCol = 0;
        int enterVarIdxRow = 0;
        for (int i = 0; i < tableau[0].length; i++) {
            enterVarIdxCol = tableau[0][i] == 1 ? i : 0; // set equal to i, 0 otherwise
        }
        for (int j = 0; j < numConstraints; j++) {
            enterVarIdxRow = tableau[j][enterVarIdxCol] == 1 ? j : 0; // set equal to j, 0 otherwise
        }
        return new int[]{enterVarIdxRow, enterVarIdxCol};
    }

    // this method checks for optimality
    public boolean isOptimal() {
        boolean optimal;
        int countNegative = 0;
        for (int i = 0; i < tableau[0].length; i++) {
            if (tableau[0][i] < 0) {
                countNegative++;
            }
        }
        optimal = countNegative == 0; // if any negative in obj, not optimal
        return optimal;
    }

    private HashMap<String, Double> getBasis() {
        HashMap<String, Double> basis = new HashMap<>(); // Initialize the HashMap
        for (int i = 0; i < tableau[0].length - 1; i++) {
            ArrayList<Integer> basicVarIdxRow = new ArrayList<>(); // i need an arrayList here because i need to add elements dynamically
            int countNonZero = 0; // Initialize countNonZero at 0 for each variable
            for (int j = 0; j < numConstraints + 1; j++) {
                if (tableau[j][i] != 0) { // Check if the entry is non-zero
                    countNonZero++;
                    basicVarIdxRow.add(j); // add the index of the row of the basic variable (potentially basic)
                }
            }
            if (countNonZero == 1) {
                basis.put("X" + (i + 1), tableau[basicVarIdxRow.getFirst()][tableau[0].length - 1]);
            }
        }
        return basis;
    }

    // find the ratio of entering variables to see lowest one
    public void solve() {
        boolean twoPhase = this.twoPhase();
        boolean optimal = this.isOptimal();
        if (twoPhase) {
            this.solvePhase1();
        }
        while (!optimal) { // while not optimal, run pivots
            int enterVarIdxCol = this.getEnterVar()[1];
            int enterVarIdxRow = this.getEnterVar()[0];
            this.pivot(enterVarIdxCol, enterVarIdxRow);
            optimal = this.isOptimal();
        }
        System.out.println();
        System.out.println("OPTIMAL TABLEAU");
        this.displayTableau();
        System.out.println();
        System.out.println("BASIC VARIABLES AND VALUES");
        System.out.println(this.getBasis());
    }

    private boolean twoPhase() {
        return twoPhase;
    }

    // to see if phase1 is optimal, need to check if Z is 0, else not optimal yet
    private boolean isFirstPhaseOptimal() {
        return tableau[0][tableau[0].length - 1] == 0;
    }

    // checks if first phase is optimal
    private boolean isFirstPhaseFeasible() {
        int countZeroArtificialVar = 0;
        for (int i = 0; i < this.artificialVariablesIndex.length; i++) {
            if (tableau[0][this.artificialVariablesIndex[i]] != 0) { // check if artificial variables and objective value are 0
                countZeroArtificialVar++; // increment, optimality not reached
            }
        }
        return countZeroArtificialVar == 0; // if all artificial variables have been eliminated from top row, optimality reached
    }

    // solves for phase 1
    public void solvePhase1() {
        while (!this.isFirstPhaseOptimal() && !this.isFirstPhaseFeasible()) { // need to pivot until we enter feasible region of LP
            int enterVarIdxCol = this.getEnterVarPhase1()[1];
            int enterVarIdxRow = this.getEnterVarPhase1()[0];
            this.pivot(enterVarIdxCol, enterVarIdxRow);
        }
        // replace top row by original objective function
        System.arraycopy(this.originalObjective, 0, tableau[0], 0, this.originalObjective.length);
    }

    public void initializeTableau(LinearProgram lp) {
        tableau = new double[numConstraints + 1][numVariables + numConstraints + 1 + countGeq]; // + 1 RHS + count surplus

        // get objective function - remember we need to negate this row if max
        // also handles initialization of 2phase method
        // we then have 2 objective functions, original objective AND min sum of artificial variables

        // IF NEED 2 PHASE SIMPLEX METHOD, TABLEAU INITIALIZED FOR 2 PHASE
        if (twoPhase) { // no need to initialize if not using 2phase method
            System.out.println("Problematic constraints found...");
            System.out.println("Initialized 2-phase Simplex Tableau");
            int k = 0; // k is used for checking if the prev constraint is GEQ -> account for surplus and artificial
            originalObjective = new double[numVariables + numConstraints + 1 + countGeq];
            for (int i = 0; i < objectiveCoefficients.length; i++) { // save original objective for phase2
                originalObjective[i] = lp.isMaximize() ? objectiveCoefficients[i] : -objectiveCoefficients[i];
            }
            double[] artificialObjective = new double[numVariables + numConstraints + 1 + countGeq]; // all surplus + artificial
            for (int i = 0; i < numConstraints; i++) { // 1 in index of artificial variables for Z row
                if (lp.getConstraints().get(i).getType() == 1) { // EQUAL constraint
                    artificialObjective[numVariables + i + k] = 1.0; // 1 for artificial variable
                    this.artificialVariablesIndex[i] = numVariables + i + k; // save artificial variables index
                } else if (lp.getConstraints().get(i).getType() == 2) {
                    artificialObjective[numVariables + i + k + 1] = 1.0; // count for surplus variable, add 2
                    this.artificialVariablesIndex[i] = numVariables + i + k + 1; // save artificial var
                }
                // here we need to fill the tableau with the right constraints and surplus, artificial, RHS ..
                Constraint constraint = lp.getConstraints().get(i);
                for (int j = 0; j < numVariables; j++) {
                    tableau[i + 1][j] = constraint.getCoefficients()[j]; // all elements of constraint
                }
                // insert RHS and surplus/slack/artificial
                // for EQ and LEQ, this is the same operation, for GEQ we need to vary it
                tableau[i + 1][tableau[0].length - 1] = constraint.getRHS(); // put RHS in last column of constraint
                if (constraint.getType() == 2) { // if geq, need -1 and 1 (surplus, artificial)
                    tableau[i + 1][numVariables + i + k] = -1; // -1 for surplus variable
                    tableau[i + 1][numVariables + i + k + 1] = 1; // 1 for artificial variable
                    k++; // increase by 1 so that next constraint will account for surplus too
                } else { // when we just have EQ or LEQ we just need slack
                    tableau[i + 1][numVariables + i + k] = 1; // if leq, just 1 artificial, if eq, just 1 artificial
                }
            }
            this.objectiveCoefficients = artificialObjective; // minimize sum of artificial variables first
        } else { // if tableau is just simplex, no 2phase needed
            for (int i = 0; i < objectiveCoefficients.length; i++) {
                tableau[0][i] = lp.isMaximize() ? objectiveCoefficients[i] : -objectiveCoefficients[i]; // if maximize, we negate
            }
            for (int i = 0; i < numConstraints; i++) {
                // get constraint in tableau[i + 1] because we need 1 row for objective function
                Constraint constraint = lp.getConstraints().get(i);
                for (int j = 0; j < numVariables; j++) {
                    tableau[i + 1][j] = constraint.getCoefficients()[j]; // fill constraint
                }
                tableau[i + 1][numVariables + i] = 1; // slack variable
                tableau[i + 1][tableau[0].length - 1] = constraint.getRHS(); // last column is RHS
            }
        }
    }
}
