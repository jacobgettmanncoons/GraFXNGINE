/*
 * Copyright (c) 2016 Jacob J. Jones
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package jacobgc.grafx.grafxngine.display;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import jacobgc.grafx.grafxngine.render_engine.Loader;
import jacobgc.grafx.grafxngine.render_engine.Renderer;
import jacobgc.grafx.grafxngine.render_engine.RawModel;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by JacobGC on 5/24/2016.
 */
public class Display implements Runnable {

    public static Thread thread;
    public static boolean running = true;
    private static long window;
    private static int width = 1200, height = 800;
    private static double fps;
    private static String title = "GraFX NGINE";

    static Loader loader = new Loader();
    static Renderer renderer = new Renderer();

    public void setFPS(double fpsField) {
        fps = fpsField;
    }

    public void start() {
        running = true;
        thread = new Thread(this, "EndlessRunner");
        thread.start();
    }

    public static void init() {
        if (GLFW.glfwInit() != GL11.GL_TRUE) {
            System.err.println("GLFW initialziation failed!");
        }

        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        window = GLFW.glfwCreateWindow(width, height, title, NULL, NULL);

        if (window == NULL) {
            System.err.println("Could not create the window!");
        }

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        GLFW.glfwShowWindow(window);
    }

    public static void update() {
        GLFW.glfwPollEvents();
    }

    public static void triangle(){

    }

    public static void render() {
        GLFW.glfwSwapBuffers(window);
        float[] vertices = {
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
        };
        RawModel model = loader.loadToVAO(vertices);
        renderer.prepare();
        renderer.render(model);
    }

    @Override
    public void run() {

        init();

        double frame_cap = 1.0/fps;
        double time = Timer.getTime();
        double unprocessed = 0;

        double frame_time = 0;
        int frames = 0;

        while (running) {

            boolean can_render = false;

            double time_2 = Timer.getTime();
            double passed = time_2 - time;
            unprocessed += passed;
            frame_time += passed;

            time = time_2;

            while(unprocessed >= frame_cap){
                can_render = true;

                unprocessed -= frame_cap;
                if(frame_time >= 1.0){
                    frame_time = 0;
                    System.out.print(frames + "\n");
                    frames = 0;
                }
            }

            if(can_render){
                render();
                frames++;
            }

            update();

            if (GLFW.glfwWindowShouldClose(window) == GL11.GL_TRUE) {
                running = false;
                loader.cleanUP();
            }
        }
    }
}
