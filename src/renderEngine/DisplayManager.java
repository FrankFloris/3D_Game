package renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float delta;

    public static  void createDisplay(){

        ContextAttribs attribs = new ContextAttribs(3,2)
        .withForwardCompatible(true)
        .withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(8,8,8,8), attribs);
            GL11.glEnable(GL13.GL_MULTISAMPLE);
            GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
//            Display.create(new PixelFormat());
            Display.setTitle("3D Game!");
        } catch (LWJGLException e) {
            e.printStackTrace();
        }


        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFrameTime = getCurrentTime();

    }

    public static void updateDisplay(){

        Display.sync(FPS_CAP);
        Display.update();
        long currentFrameTime = getCurrentTime();
        delta = (currentFrameTime - lastFrameTime)/1000f;
        lastFrameTime = currentFrameTime;
    }

    public static float getFrameTimeSeconds(){
        return delta;
    }

    public static void closeDisplay(){

        Display.destroy();

    }

    private static long getCurrentTime(){
        return Sys.getTime()*1000/Sys.getTimerResolution();
    }

    public static float getFPS() {
        return 1f/delta;
    }
}
