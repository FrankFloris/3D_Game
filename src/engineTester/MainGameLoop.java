package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrains.Terrain;
import textures.ModelTexture;
import org.lwjgl.opengl.Display;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static void main(String[] args){

        DisplayManager.createDisplay();
        Loader loader = new Loader();

        //**********************TERRAIN TEXTURE STUFF*********//

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //****************************************************//


//        ModelData data = OBJFileLoader.loadOBJ("tree");
//        RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
//        TexturedModel newTree = new TexturedModel(OBJFileLoader.loadOBJ("tree"), new ModelTexture(loader.loadTexture("tree")));


//        RawModel model = OBJloader.loadObjModel("tree", loader);
        TexturedModel tree = new TexturedModel(OBJloader.loadObjModel("tree", loader),
                new ModelTexture(loader.loadTexture("tree")));
        TexturedModel grass = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel flower = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("flower")));
        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader),
                new ModelTexture(loader.loadTexture("fern")));
        TexturedModel bobble = new TexturedModel(OBJloader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));

        TexturedModel playerModel = new TexturedModel(OBJloader.loadObjModel("person", loader),
                new ModelTexture(loader.loadTexture("playerTexture2")));
        Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        player.getModel().getTexture().setReflectivity(0.1f);
        player.getModel().getTexture().setShineDamper(0.5f);
        Camera camera = new Camera(player);

        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUseFakeLighting(true);
        fern.getTexture().setHasTransparency(true);
//        RawModel model = OBJloader.loadObjModel("dragon", loader);
//        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("ColoredDragon")));
//        TexturedModel cubeModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("ColoredDragon")));
        TexturedModel testModel = new TexturedModel(OBJloader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("ColoredDragon")));
        ModelTexture texture = tree.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        Light light = new Light(new Vector3f(20000,40000,2000), new Vector3f(1,1,1));

        Terrain terrain = new Terrain(0,-1,loader, texturePack, blendMap, "heightmap");
        //terrain 2 werkt nog niet, moet uitvogelen op welk terrein player staat
        Terrain terrain2 = new Terrain(1,-1, loader, texturePack, blendMap, "heightmap");

        Terrain[][] terrains;
        terrains = new Terrain[2][2];
        terrains[0][0] = terrain;
        terrains[1][0] = terrain2;
        Terrain currentTerrain = terrains[(int) (player.getPosition().x/800)][(int) (player.getPosition().y/800)];
//        terrains[0][1] = terrain3;
//        terrains[1][1] = terrain4;


        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(650000);
        for(int i=0;i<400;i++) {
            if (i % 7 == 0) {
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1.8f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z);
                entities.add(new Entity(flower, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.3f));
            }
            if (i % 3 == 0) {
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() *1 + 4));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(bobble, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(fern, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
            }
        }




        Entity dragon = new Entity(testModel, new Vector3f(0, 0, -150), 3 , 3 , 0f, 3f);



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
            //die 800 zou terrain.size moeten zijn...
            int gridX = (int) (player.getPosition().x/800);
            int gridZ = (int) (player.getPosition().z/800);
            player.move(terrains[gridX][gridZ]);
//            player.move(currentTerrain);

//            player.move(terrain);
            renderer.processEntity(player);


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
