package engineTester;

import entities.*;
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
import skybox.SkyboxRenderer;
import skybox.SkyboxShader;
import terrains.Terrain;
import textures.ModelTexture;
import org.lwjgl.opengl.Display;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;

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

        Terrain terrain = new Terrain(-1,-1,loader, texturePack, blendMap, "heightmap");
        Terrain terrain2 = new Terrain(0,-1, loader, texturePack, blendMap, "heightmap");
        Terrain terrain3 = new Terrain(-1,0,loader, texturePack, blendMap, "heightmap");
        Terrain terrain4 = new Terrain(0,0, loader, texturePack, blendMap, "heightmap");

//        Terrain[][] terrains;
//        terrains = new Terrain[2][2];
        Terrain[][] terrains = new Terrain[][]{{terrain, terrain3}, {terrain2, terrain4}};
//        terrains[0][0] = terrain;
//        terrains[1][0] = terrain2;
//        terrains[0][1] = terrain3;
//        terrains[1][1] = terrain4;



//        ModelData data = OBJFileLoader.loadOBJ("tree");
//        RawModel treeModel = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
//        TexturedModel newTree = new TexturedModel(OBJFileLoader.loadOBJ("tree"), new ModelTexture(loader.loadTexture("tree")));


//        RawModel model = OBJloader.loadObjModel("tree", loader);
        TexturedModel pine = new TexturedModel(OBJloader.loadObjModel("pine", loader),
                new ModelTexture(loader.loadTexture("pine")));
        TexturedModel grass = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel flower = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("flower")));
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader), fernTextureAtlas);
        TexturedModel bobble = new TexturedModel(OBJloader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel lamp = new TexturedModel(OBJloader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);

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
        ModelTexture texture = pine.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);


        List<Light> lights = new ArrayList<Light>();
        Light sun = new Light(new Vector3f(0,1000,-7000), new Vector3f(0.4f,0.4f,0.4f), new Vector3f(1,0,0));
        lights.add(sun);
        Light moon = new Light(new Vector3f(100, 220, -50), new Vector3f(1f, 1f, 1f));
        lights.add(moon);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(650000);
        for(int i=0;i<400;i++) {
            if (i % 7 == 0) {
                float x = random.nextFloat() * -800;
                float z = random.nextFloat() * -800;
                if (i >= 200){
                    x += 800;
                    z += 800;
                }
                System.out.println(z);
                float y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
                entities.add(new Entity(grass, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1.8f));
                x = random.nextFloat() * -800;
                z = random.nextFloat() * -800;
                if (i >= 200){
                    x += 800;
                    z += 800;
                }
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
                entities.add(new Entity(flower, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 2.3f));
            }
            if (i % 3 == 0) {
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(pine, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() *1 + 0.5f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(bobble, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                x = random.nextFloat() * 800 -400;
                z = random.nextFloat() * -800;
                y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f)); //random after fern is random fern texture
            }
            if (i % 25 == 0){
                float x = random.nextFloat() * 800 -400;
                float z = random.nextFloat() * -800;
                float y = terrains[(int) (Math.floor(x%800)/800+1)][(int) (Math.floor(z%800)/800+1)].getHeightOfTerrain(x, z);
                entities.add(new Entity(lamp, new Vector3f(x, y, z), 0, 0, 0, 1));
                if (i % 50 == 0){
                    lights.add(new Light(new Vector3f(x, y+15, z), new Vector3f(2,2,0), new Vector3f(1, 0.01f, 0.002f))); //yellow
                } else {
                    lights.add(new Light(new Vector3f(x, y + 15, z), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f))); //red
                }

            }
        }

        Entity dragon = new Entity(testModel, new Vector3f(0, 0, -150), 3 , 3 , 0f, 1f);

        MasterRenderer renderer = new MasterRenderer(loader);
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

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        while(!Display.isCloseRequested()){
//            int gridX = (int) (player.getPosition().x/Terrain.SIZE);
//            int gridZ = (int) (player.getPosition().z/Terrain.SIZE);
//            player.move(terrains[gridX][gridZ]);
            player.move(terrains[(int) Math.floor((player.getPosition().x % Terrain.SIZE) / Terrain.SIZE + 1)]
                    [(int) Math.floor((player.getPosition().z%Terrain.SIZE) / Terrain.SIZE+1)]);
//            System.out.println(Math.floor((player.getPosition().x%800)/800+1) + " and " + Math.floor((player.getPosition().z%800)/800+1));

//            System.out.println(DisplayManager.getFPS());
            camera.move();
            picker.update();
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (terrainPoint != null){
//                dragon.setPosition(terrainPoint);
                sun.setPosition(terrainPoint);
            }
//            System.out.println(picker.getCurrentRay());


            renderer.processEntity(player);

            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    renderer.processTerrain(terrains[i][j]);
                }

            }
//            renderer.processTerrain(terrain);
//            renderer.processTerrain(terrain2);
//            renderer.processTerrain(terrain3);
//            renderer.processTerrain(terrain4);
            renderer.processEntity(dragon);

            dragon.increaseRotation(0,0.25f, 0);
            for (Entity entity: entities){
                renderer.processEntity(entity);
            }

            lights.sort(new LightComparator(player));


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
