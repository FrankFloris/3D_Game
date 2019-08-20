package engineTester;

import entities.*;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.*;
import terrains.Terrain;
import textures.ModelTexture;
import org.lwjgl.opengl.Display;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import water.WaterFrameBuffer;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    private static final int MAPWIDTH = 4;
    private static final int MAPDEPTH = 4;
    private static Loader loader = new Loader();
    private static Terrain[][] terrains = new Terrain[MAPWIDTH][MAPDEPTH];
    private static Random random = new Random(650000);
    private static List<Entity> entities = new ArrayList<>();
    private static List<WaterTile> waters = new ArrayList<>();
    private static List<Light> lights = new ArrayList<>();
    private static List<GuiTexture> guiTextures = new ArrayList<>();
    private static Player player;
    private static boolean addLights = false;

    public static void main(String[] args){

        DisplayManager.createDisplay();

        MasterRenderer renderer = new MasterRenderer(loader);
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        WaterFrameBuffer waterFrameBuffer = new WaterFrameBuffer();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFrameBuffer);

        generateWorld();


        //TODO add bunnies? :)

        Camera camera = new Camera(player);

        TexturedModel testModel = new TexturedModel(OBJloader.loadObjModel("dragon", loader), new ModelTexture(loader.loadTexture("ColoredDragon")));
        Entity dragon = new Entity(testModel, new Vector3f(0, 0, -150), 3 , 3 , 0f, 4f);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        while(!Display.isCloseRequested()){
            player.checkMoves(terrains);
            camera.move();
            picker.update();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            Vector3f terrainPoint = picker.getCurrentTerrainPoint();

            while (Keyboard.next() && terrainPoint != null){
                float y = MousePicker.getTerrainPointY(terrainPoint.x, terrainPoint.z, terrains[0][0]);
                if (Keyboard.getEventKey() == Keyboard.KEY_LCONTROL && Keyboard.getEventKeyState()) {
                    generatePineTrees(1, new Vector3f(terrainPoint.x, y, terrainPoint.z));
                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
//                    if (Keyboard.getEventKeyState()) {
//                        picker.moveEntity(new Vector3f(terrainPoint.x, y, terrainPoint.z), entities);
//                    }
//                }
                if (Keyboard.getEventKey() == Keyboard.KEY_Z && Keyboard.getEventKeyState()) {
                        waters.add(new WaterTile(terrainPoint.x, terrainPoint.z, 0));
                }
            }

//
//            //TODO precisie weghalen en terugplaatsen verhogen!
            if (terrainPoint != null && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                float y = MousePicker.getTerrainPointY(terrainPoint.x, terrainPoint.z, terrains[0][0]);
                picker.moveEntity(new Vector3f(terrainPoint.x, y, terrainPoint.z), entities);
            }


            lights.sort(new LightComparator(player));

            waterFrameBuffer.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, terrains, lights, camera, player, new Vector4f(0, 1, 0, -waters.get(0).getHeight()+0.25f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            waterFrameBuffer.bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrains, lights, camera, player, new Vector4f(0, -1, 0, waters.get(0).getHeight()));

            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            waterFrameBuffer.unbindCurrentFrameBuffer();
            renderer.processEntity(dragon);
            dragon.increaseRotation(0,0.25f, 0);
            renderer.renderScene(entities, terrains, lights, camera, player, new Vector4f(0, 1, 0, 1000)); //change w: to height of clipping
            waterRenderer.render(waters, camera, lights.get(0));
            guiRenderer.render(guiTextures); //wordt nu nog niet gebruikt
            DisplayManager.updateDisplay();
        }


        waterFrameBuffer.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }

    private static void generateWorld() {
        generateTerrains();
        generateEntityTextures();
        generateWaterTiles();
        generateLight();
        generateGuis();
        generatePlayer();
    }


    private static void generatePlayer() {
        TexturedModel playerModel = new TexturedModel(OBJloader.loadObjModel("person", loader),
                new ModelTexture(loader.loadTexture("playerTexture2")));
        player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 180, 0, 0.6f);
        player.getModel().getTexture().setReflectivity(0.1f);
        player.getModel().getTexture().setShineDamper(0.5f);
        player.getModel().getTexture().setUseFakeLighting(true);
    }

    private static void generateGuis() {
        //GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        GuiTexture gui2 = new GuiTexture(loader.loadTexture("Creeper"), new Vector2f(0.3f, 0.3f), new Vector2f(0.2f, 0.2f));
//        guis.add(gui);
//        guis.add(gui2); //rendered after, so on top of other image(s)
    }


    private static void generateTerrains() {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        for (int i = 0; i < MAPWIDTH; i++) {
            for (int j = 0; j < MAPDEPTH; j++) {
                terrains[i][j] = new Terrain(i-1,j-1, loader, texturePack, blendMap, "heightmap");
            }
        }
    }

    private static void generateEntityTextures() {
        generateGrassTextures(60, new Vector3f(0.0f, 0.0f, 0.0f));
        generateFlowers(60, new Vector3f(0.0f, 0.0f, 0.0f));
        generatePineTrees(135, new Vector3f(0.0f, 0.0f, 0.0f));
        generateBobbleTrees(135, new Vector3f(0.0f, 0.0f, 0.0f));
        generateFerns(135, new Vector3f(0.0f, 0.0f, 0.0f));
        generateLampLights(18, new Vector3f(0.0f, 0.0f, 0.0f));
    }

    private static void generateGrassTextures(int numberOfEntities, Vector3f vector) {
        TexturedModel grass = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        generateEntities(grass, numberOfEntities, 1.8f, vector);
    }

    private static void generateFlowers(int numberOfEntities, Vector3f vector) {
        TexturedModel flower = new TexturedModel(OBJloader.loadObjModel("grassModel", loader),
                new ModelTexture(loader.loadTexture("flower")));
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUseFakeLighting(true);
        generateEntities(flower, numberOfEntities, 2.3f, vector);
    }

    private static void generatePineTrees(int numberOfEntities, Vector3f vector) {
        TexturedModel pine = new TexturedModel(OBJloader.loadObjModel("pine", loader),
                new ModelTexture(loader.loadTexture("pine")));
        generateEntities(pine, numberOfEntities, random.nextFloat() * 1 + 0.5f, vector);
    }

    private static void generateBobbleTrees(int numberOfEntities, Vector3f vector) {
        TexturedModel bobble = new TexturedModel(OBJloader.loadObjModel("lowPolyTree", loader),
                new ModelTexture(loader.loadTexture("lowPolyTree")));
        generateEntities(bobble, numberOfEntities, random.nextFloat() * 0.1f + 0.6f, vector);
    }

    private static void generateFerns(int numberOfEntities, Vector3f vector) {
        ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
        fernTextureAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJloader.loadObjModel("fern", loader), fernTextureAtlas);
        fern.getTexture().setHasTransparency(true);
        generateEntities(fern, numberOfEntities, 0.9f, vector);
    }

    private static void generateLampLights(int numberOfEntities, Vector3f vector) {
        TexturedModel lamp = new TexturedModel(OBJloader.loadObjModel("lamp", loader),
                new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUseFakeLighting(true);
        addLights = true;
        generateEntities(lamp, numberOfEntities, 1f, vector);
    }

    private static void generateEntities(TexturedModel model, int numberOfEntities, float scale, Vector3f vector) {
        if (numberOfEntities != 1){
            generateMultipleEntities(model, numberOfEntities, scale);
        } else {
            entities.add(new Entity(model, vector, 0, random.nextFloat() * 360, 0, scale));
        }
        addLights = false;
    }

    private static void generateMultipleEntities(TexturedModel model, int numberOfEntities, float scale) {
        for (int i = 0; i < MAPWIDTH; i++) {
            for (int j = 0; j < MAPDEPTH; j++) {
                for (int k = 0; k < numberOfEntities; k++) {
                    float x = random.nextFloat() * -Terrain.SIZE;
                    float z = random.nextFloat() * -Terrain.SIZE;
                    float y = terrains[0][0].getHeightOfTerrain(x, z);
                    if (y > -5) {
                        entities.add(new Entity(model, new Vector3f(x + Terrain.SIZE * i, y, z + Terrain.SIZE * j), 0, random.nextFloat() * 360, 0, scale));
                        if (addLights) {
                            lights.add(new Light(new Vector3f(x + Terrain.SIZE * i, y + 15, z + Terrain.SIZE * j), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));
                        }
                    }

                }
            }
        }
        addLights = false;
    }


    private static void generateWaterTiles() {
        for (int i = 0; i < (MAPWIDTH*Terrain.SIZE/WaterTile.TILE_SIZE)-1; i++) {
            for (int j = 0; j < (MAPDEPTH*Terrain.SIZE/WaterTile.TILE_SIZE)-1; j++) {
                waters.add(new WaterTile(-700 + (WaterTile.TILE_SIZE * i), -700 + (WaterTile.TILE_SIZE * j), -5));
            }
        }
    }

    private static void generateLight() {
        Light sun = new Light(new Vector3f(100, 2220, -50), new Vector3f(1f, 1f, 1f));
        lights.add(sun);
        Light moon = new Light(new Vector3f(0,1000,-7000), new Vector3f(0.4f,0.4f,0.4f), new Vector3f(1,0,0));
        lights.add(moon);
    }

    public static int getMAPWIDTH() {
        return MAPWIDTH;
    }

    public static int getMAPDEPTH() {
        return MAPDEPTH;
    }
}
