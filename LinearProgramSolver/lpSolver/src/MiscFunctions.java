public class MiscFunctions {
    public static int indexOfSmallest(double[] array) {
        int indexSmallest = 0;
        double smallestEntry = array[0]; // replace every time smaller found
        for (int i = 0; i < array.length; i++) {
            if (array[i] < smallestEntry) {
                indexSmallest = i;
            }
        }
        return indexSmallest;
    }
}
