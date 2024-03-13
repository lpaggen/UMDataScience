import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QLearner {

    private static final float ALPHA = 0.1f;

    public static void main(String[] args) {
        BlackJackEnv game = new BlackJackEnv(BlackJackEnv.NONE);
		//Init your QTable
		ArrayList<float[]> QTable = new ArrayList<float[]>(); // matrix form ?

		//Variables to measure and report average performance
		double totalReward = 0.0;
        int numberOfGames = 0;
        while (notDone()) {
        	// Make sure the playOneGame method returns the end-reward of the game
            totalReward += playOneGame(game,QTable);
            numberOfGames++;
            if ((numberOfGames % 10000) == 0)
                System.out.println("Avg reward after " + numberOfGames + " games = " + 
                						(totalReward / ++numberOfGames));
        }
        // Show the learned QTable
        outputQTable(QTable);
    }

    private static int iteration = 0; // this is for the Q table idx ?

    private static double playOneGame(BlackJackEnv game, ArrayList<float[]> QTable) {
        boolean GameOver = false; // defaults to false
        boolean exploration = true; // random moves until we get enough data
        int action = 0; // action for the game
        float gamma = 0.9f; // discount factor for the reward, close to 1
        List<Integer> indexes = new ArrayList<>(); // index to modify Q value

        float p_state; // containing the value of player and dealer
        float d_state;
        float q_action; // action per given state
        float q_val; // Q value given state (0 until update)

        ArrayList<String> gamestate;
        gamestate = game.reset(); // starts the game
        List<String> finalGameState = gamestate;

        // get this in a while loop
        while (!GameOver) {
            if (exploration) {
                Random random = new Random();
                action = random.nextInt(2);
            }

            gamestate = game.step(action);

            List<String> playerCards = BlackJackEnv.getPlayerCards(gamestate);
            int sumOfPlayerCards = BlackJackEnv.totalValue(playerCards);
            List<String> dealerCards = BlackJackEnv.getDealerCards(gamestate);
            int sumOfDealerCards = BlackJackEnv.totalValue(dealerCards);

            int reward = Integer.parseInt(gamestate.get(1)); // get the reward

            float[] row = new float[4];

            // now we update Q table
            p_state = (float) sumOfPlayerCards;
            d_state = (float) sumOfDealerCards;
            q_action = (float) action;
            q_val = (float) reward;

            row[0] = p_state;
            row[1] = d_state;
            row[2] = q_action;
            row[3] = q_val;

            QTable.add(row);

            indexes.add(iteration); // this makes a list for the discount factor to be applied

            iteration ++; // iteration increment for idx of Q val

            if (Boolean.parseBoolean(gamestate.getFirst())) { // check if game has ended
                GameOver = true;
                finalGameState = gamestate;

                Collections.reverse(indexes); // get idx in correct order for discounting

                float qValNoDiscount = QTable.get(indexes.getFirst())[3]; // this just gets the Q value

                // discount the Q values with gamma discount rate

                int loopIdx = 0;

                for (int x : indexes) {
                    float oldQValue = QTable.get(x)[3];
                    float newQValue = oldQValue + ALPHA * (reward + gamma * getMaxQValue(QTable, p_state, d_state) - oldQValue);
                    QTable.get(x)[3] = newQValue;
                }
            }
        }

        return Double.parseDouble(finalGameState.get(1));

    }

    private static float getMaxQValue(ArrayList<float[]> QTable, float p_state, float d_state) {
        float maxQValue = Float.NEGATIVE_INFINITY;

        for (float[] row : QTable) {
            if (row[0] == p_state && row[1] == d_state) {
                if (row[3] > maxQValue) {
                    maxQValue = row[3];
                }
            }
        }

        return maxQValue;
    }

    private static int getBestAction(ArrayList<float[]> QTable, float p_state, float d_state) {
        int bestAction = 0;
        float maxQValue = Float.NEGATIVE_INFINITY;

        for (float[] row : QTable) {
            if (row[0] == p_state && row[1] == d_state) {
                if (row[3] > maxQValue) {
                    maxQValue = row[3];
                    bestAction = (int) row[2];
                }
            }
        }

        return bestAction;
    }

	// Example stopping condition: fixed number of games
    private static int episodeCounter = 0;
    private static boolean notDone() {
        episodeCounter++;
        return (episodeCounter <= 1000);
    }

    private static void outputQTable(ArrayList<float[]> QTable) {
        System.out.println("PLAYER VALUE | DEALER VALUE | ACTION | Q VALUE");
        for (float[] row : QTable) {
            float pState = row[0];
            float dState = row[1];
            float action = row[2];
            float qval = row[3];
            System.out.println("    " + pState + "          " + dState + "         " + action + "     " + qval);
        }
        System.out.println("PLAYER VALUE | DEALER VALUE | ACTION | Q VALUE");
    }
}
