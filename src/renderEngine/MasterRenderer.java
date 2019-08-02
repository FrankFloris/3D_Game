package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

    public void render(Light sun, Camera camera){
        renderer.prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);

        shader.stop();
        entities.clear();
    }




    public void cleanUp(){
        shader.cleanUp();
    }
}
