package renderEngine;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.glfwGetMonitors;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final String TITLE = "Our First Display";
    public static long window;

    public static void createDisplay() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        GLFW.glfwDefaultWindowHints(); // optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable

        // Create the window
        long monitor = glfwGetMonitors().get(0);

        // set "monitor" field to monitor variable if we want full screen
        window = GLFW.glfwCreateWindow(WIDTH, HEIGHT, TITLE, 0, 0);

        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(window);

        // enable or disable v-sync
        GLFW.glfwSwapInterval(0);

        // show window
        GLFW.glfwShowWindow(window);

        // create openGL capabilities
        GL.createCapabilities();

        // set a color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public static void updateDisplay() {
        // Poll for window events. The key callback above will only be invoked during this call.
        GLFW.glfwPollEvents();

        // Swap the color buffers
        GLFW.glfwSwapBuffers(window);
    }

    public static void closeDisplay() {
        GLFW.glfwWindowShouldClose(window);
    }
}
