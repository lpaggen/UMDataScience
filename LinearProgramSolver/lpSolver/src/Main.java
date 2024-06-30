public class Main {
    public static void main(String[] args) {
        double[] objectiveCoefficients = {30.0, 40.0, 20.0};
        boolean maximize = true;

        // can create an initial LP with objective function and maximize type
        LinearProgram lp = new LinearProgram(objectiveCoefficients, maximize);

        // creating and adding constraints like this
        lp.add("leq", 4.0, 1.0, 1.0, 2.0);

        lp.add("geq", 3.0, 2.0, -1.0, 6.0);

        lp.add("geq", 1.0, 8.0, -4.0, 4.0);

        lp.add("geq", 6.0, -1.0, 1.0, 2.0);

        lp.add("geq", 8.0, 4.0, 5.0, 10.0);

        // try displaying the original problem + augmented form tableau
        // everything should work exactly as expected
        lp.display();
        System.out.println();
        System.out.println("AUGMENTED FORM");
        lp.displayAugmentedForm();

        lp.solve();
    }
}

