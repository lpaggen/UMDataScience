package engineTester;

import org.lwjgl.glfw.GLFW;
import renderEngine.DisplayManager;
import static renderEngine.DisplayManager.window;

public class MainGameLoop {

    public static void main(String[] args) {

        DisplayManager.createDisplay();

        // while not pressing exit, game continues to run
        while (!GLFW.glfwWindowShouldClose(window)) {

            DisplayManager.updateDisplay();

        }

        DisplayManager.closeDisplay();
    }
}
