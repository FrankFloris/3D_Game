package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.util.vector.Vector2f;
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
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader), fernTextureAtlas);
//        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader), fernTextureAtlas,
//                new ModelTexture(loader.loadTexture("fern")));
        TexturedModel bobble = new TexturedModel(OBJloader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel lamp = new TexturedModel(OBJloader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));

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

        TexturedModel testModel = new TexturedModel(OBJloader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("ColoredDragon")));
        ModelTexture texture = tree.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);

        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(20000,40000,2000), new Vector3f(0.4f,0.4f,0.4f));
        lights.add(sun);
        lights.add(new Light(new Vector3f(150,10,-200), new Vector3f(2,0,0), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(200,10,-20), new Vector3f(0,0,2), new Vector3f(1, 0.01f, 0.002f)));
        lights.add(new Light(new Vector3f(300,50,-100), new Vector3f(0,2,0), new Vector3f(1, 0.01f, 0.002f)));




        Terrain terrain = new Terrain(-1,-1,loader, texturePack, blendMap, "heightmap");
        //terrain 2 werkt nog niet, moet uitvogelen op welk terrein player staat
        Terrain terrain2 = new Terrain(0,-1, loader, texturePack, blendMap, "heightmap");

        Terrain[][] terrains;
        terrains = new Terrain[2][2];
        terrains[0][0] = terrain;
        terrains[1][0] = terrain2;
//        Terrain currentTerrain = terrains[(int) Math.floor((player.getPosition().x%800)/800+1)][(int) Math.floor((player.getPosition().z%800)/800+1)];
//        terrains[0][1] = terrain3;
//        terrains[1][1] = terrain4;


        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(650000);
        for(int i=0;i<400;i++) {
            if (i % 7 == 0) {
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1.8f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
                entities.add(new Entity(flower, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.3f));
            }
            if (i % 3 == 0) {
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() *1 + 4));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(bobble, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f)); //random after fern is random fern texture
            }
        }


        entities.add(new Entity(lamp, new Vector3f(150, -4.7f, -200), 0 ,0 ,0 ,1));

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

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        GuiTexture gui2 = new GuiTexture(loader.loadTexture("Creeper"), new Vector2f(0.3f, 0.3f), new Vector2f(0.2f, 0.2f));
//        guis.add(gui);
//        guis.add(gui2); //rendered after, so on top of other image(s)

        GuiRenderer guiRenderer = new GuiRenderer(loader);


        while(!Display.isCloseRequested()){
//            entity.increaseRotation(0,0.25f,0);
            camera.move();
            //die 800 zou terrain.size moeten zijn...
            int gridX = (int) (player.getPosition().x/800);
            int gridZ = (int) (player.getPosition().z/800);
//            player.move(terrains[gridX][gridZ]);
            player.move(terrains[(int) Math.floor((player.getPosition().x%800)/800+1)][(int) Math.floor((player.getPosition().z%800)/800+1)]);
//            System.out.println(Math.floor((player.getPosition().x%800)/800+1) + " and " + Math.floor((player.getPosition().z%800)/800+1));
//            player.move(terrain);
            System.out.println(DisplayManager.getFPS());
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
            renderer.render(lights, camera);
            guiRenderer.render(guis);
            DisplayManager.updateDisplay();
        }

        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

}
