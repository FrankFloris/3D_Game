package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import models.RawModel;
import renderEngine.OBJloader;
import renderEngine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;
import org.lwjgl.opengl.Display;

public class MainGameLoop {

    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        StaticShader shader = new StaticShader();
        Renderer renderer = new Renderer(shader);

//        float[] vertices = {
//                -0.5f, 0.5f, 0f,    //V0
//                -0.5f, -0.5f, 0f,   //V1
//                0.5f, -0.5f, 0f,    //V2
//                0.5f, 0.5f, 0f      //V3
//        };
//
//        int[] indices = {
//                0,1,3, //Top left triangle (V0, V1, V3)
//                3,1,2 //Bottom right triangle (V3, V1, V2)
//        };
//
//        float[] textureCoords = {
//                0,0,    //V0
//                0,1,    //V1
//                1,1,    //V2
//                1,0     //V3
//        };


//        RawModel model = loader.loadToVAO(vertices, textureCoords, indices);
//        ModelTexture texture = new ModelTexture(loader.loadTexture("Mushroom"));
//        TexturedModel texturedModel = new TexturedModel(model, texture);
        RawModel model = OBJloader.loadObjModel("dragon", loader);
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("ColoredDragon")));
        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        Entity entity = new Entity(staticModel, new Vector3f(0,0,-50),0,0,0,1);
        Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));

        Camera camera = new Camera();

        while(!Display.isCloseRequested()){
//            entity.increasePosition(0, 0,-0.1f);
            entity.increaseRotation(0,0.25f,0);
            camera.move();
            renderer.prepare();
            shader.start();
            shader.loadLight(light);
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }

        shader.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
