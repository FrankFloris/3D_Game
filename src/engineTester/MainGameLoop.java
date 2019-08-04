package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        RawModel model = OBJloader.loadObjModel("tree", loader);
        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("tree")));

        TexturedModel grass = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader),
                new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHasTransparency(true);
//        RawModel model = OBJloader.loadObjModel("dragon", loader);
//        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("ColoredDragon")));
//        TexturedModel cubeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("ColoredDragon")));
        TexturedModel testModel = new TexturedModel(OBJloader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("ColoredDragon")));

        ModelTexture texture = staticModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random();
        for(int i=0;i<500;i++){
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat()*800 - 400,0,random.nextFloat() * -600),0,0,0,3));
            entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 0.6f));
        }




//        Entity entity = new Entity(staticModel, new Vector3f(0,0,-50),0,0,0,1);
        Light light = new Light(new Vector3f(20000,20000,2000), new Vector3f(1,1,1));

        Terrain terrain = new Terrain(0,-1, loader, new ModelTexture(loader.loadTexture("grass")));
        Terrain terrain2 = new Terrain(-1,-1, loader, new ModelTexture(loader.loadTexture("grass")));
        Entity dragon = new Entity(testModel, new Vector3f(0, 0, -150), 3 , 3 , 0f, 3f);

        Camera camera = new Camera();

//        List<Entity> allCubes = new ArrayList<Entity>();
//        Random random = new Random();
//        for (int i = 0; i < 20; i++) {
//            float x = random.nextFloat() * 100 - 50;
//            float y = random.nextFloat() * 100 - 50;
//            float z = random.nextFloat() * -300;
//            allCubes.add(new Entity(cubeModel, new Vector3f(x, y, z), random.nextFloat() * 180f, random.nextFloat() * 180f, 0f, 1f));
//        }

//        List<Entity> allDragons = new ArrayList<Entity>();

        MasterRenderer renderer = new MasterRenderer();
//        Random random = new Random();
//        for (int i = 0; i < 10; i++) {
//            float x = i * 3;
//            float y = i * 3;
//            float z = i * -30;
//            allDragons.add(new Entity(testModel, new Vector3f(x, y , z), i , i , 0f, 1f));
//        }


        while(!Display.isCloseRequested()){
//            entity.increaseRotation(0,0.25f,0);
            camera.move();


//            for (Entity cube: allCubes){
//                renderer.processEntity(cube);
//                cube.increaseRotation(0,0.25f,0);
//            }
//            for (Entity dragon: allDragons){
//                renderer.processEntity(dragon);
//                dragon.increaseRotation(0,0.25f,0);
//            }

            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.processEntity(dragon);

            dragon.increaseRotation(0,0.25f, 0);
            for (Entity entity: entities){
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
}
