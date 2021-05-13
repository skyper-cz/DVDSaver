package cz.educanet;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;


import static cz.educanet.Main.*;


public class Window {
    public static void Okno() throws Exception {


        GLFW.glfwInit();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        long window = GLFW.glfwCreateWindow(W, H, "DVDSaver", 0, 0);
        if (window == 0) {
            GLFW.glfwTerminate();
            throw new Exception("Can't open window");
        }
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GL33.glViewport(0, 0, W, H);
        GLFW.glfwSetFramebufferSizeCallback(window, (win, w, h) -> GL33.glViewport(0, 0, w, h));
        Shaders.initShaders();
        Player player = new Player(0f, 0f, 0.25f);

        String[] filesplit = Maze.split("\n");
        for (String s : filesplit) {
            String[] filesplit2 = s.split(";");
            Player square = new Player(Float.parseFloat(filesplit2[0]), Float.parseFloat(filesplit2[1]), Float.parseFloat(filesplit2[2]));
            squares.add(square);
        }

        while (!GLFW.glfwWindowShouldClose(window)) {
            if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS)
                GLFW.glfwSetWindowShouldClose(window, true);
            boolean collision = false;
            GL33.glClearColor(0f, 0f, 0f, 1f);
            GL33.glClear(GL33.GL_COLOR_BUFFER_BIT);
            for (Player square : squares) {
                square.render();
            }
            for (Player square : squares) {
                if (contact(player, square))
                    collision = true;
            }
            player.update(window);
            player.render();
            if (collision) {
                player.colorred();
            } else {
                player.colorgreen();
            }

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
        GLFW.glfwTerminate();
    }

    public static boolean contact(Player a, Player b) {
        return a.gety() + a.getz() / 2 + a.getz() > b.gety() &&
                a.gety() + a.getz() / 2 < b.gety() + b.getz() &&
                a.getx() < b.getx() + b.getz() &&
                a.getx() + a.getz() > b.getx();
    }
}
