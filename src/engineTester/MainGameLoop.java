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

    private final static int MAPWIDTH = 4;
    private final static int MAPDEPTH = 4;


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


        Terrain[][] terrains = new Terrain[MAPWIDTH][MAPDEPTH];
        for (int i = 0; i < MAPWIDTH; i++) {
            for (int j = 0; j < MAPDEPTH; j++) {
                terrains[i][j] = new Terrain(i-1,j-1, loader, texturePack, blendMap, "heightmap");
            }
        }



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
        player.getModel().getTexture().setUseFakeLighting(true);
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
        Light sun = new Light(new Vector3f(100, 2220, -50), new Vector3f(1f, 1f, 1f));
        lights.add(sun);
        Light moon = new Light(new Vector3f(0,1000,-7000), new Vector3f(0.4f,0.4f,0.4f), new Vector3f(1,0,0));
        lights.add(moon);

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(650000);

        for (int i = 0; i < MAPWIDTH; i++){
            for (int j = 0; j < MAPDEPTH; j++){
                for (int k = 0; k < 400; k++){
                    float x = random.nextFloat() * -Terrain.SIZE;
                    float z = random.nextFloat() * -Terrain.SIZE;
                    float y = terrains[0][0].getHeightOfTerrain(x, z);
                    //TODO check why this does not work
//                    float y = terrains[(int) (Math.floor(x%Terrain.SIZE)/Terrain.SIZE+1)]
//                            [(int) (Math.floor(z%Terrain.SIZE)/Terrain.SIZE+1)].getHeightOfTerrain(x, z);
                    /*DIT MOET GECHECKT WORDEN!!!!*/
                    x = random.nextFloat() * -Terrain.SIZE;
                    z = random.nextFloat() * -Terrain.SIZE;
                    y = terrains[0][0].getHeightOfTerrain(x, z);
                    if (k % 25 == 0) {
                        entities.add(new Entity(lamp, new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, 0, 0, 1));
//                        System.out.println(entities.get(entities.size()-1).getPosition());
                        lights.add(new Light(new Vector3f(x+Terrain.SIZE*i, y+15, z+Terrain.SIZE*j), new Vector3f(2,2,0), new Vector3f(1, 0.01f, 0.002f))); //yellow
                    } else if (k % 7 == 0){
                        entities.add(new Entity(grass, new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, random.nextFloat() * 360, 0, 1.8f));
                        x = random.nextFloat() * -Terrain.SIZE;
                        z = random.nextFloat() * -Terrain.SIZE;
                        y = terrains[0][0].getHeightOfTerrain(x, z);
                        entities.add(new Entity(flower, new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, random.nextFloat() * 360, 0, 2.3f));
                    } else if (k % 3 == 0){
                        entities.add(new Entity(pine, new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, random.nextFloat() * 360, 0, random.nextFloat() *1 + 0.5f));
                        x = random.nextFloat() * -Terrain.SIZE;
                        z = random.nextFloat() * -Terrain.SIZE;
                        y = terrains[0][0].getHeightOfTerrain(x, z);
                        entities.add(new Entity(bobble, new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
                        x = random.nextFloat() * -Terrain.SIZE;
                        z = random.nextFloat() * -Terrain.SIZE;
                        y = terrains[0][0].getHeightOfTerrain(x, z);
                        entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x+Terrain.SIZE*i, y, z+Terrain.SIZE*j), 0, random.nextFloat() * 360, 0, 0.9f)); //random after fern is random fern texture
                    }
                }
            }
        }

        Entity dragon = new Entity(testModel, new Vector3f(0, 0, -150), 3 , 3 , 0f, 1f);

        MasterRenderer renderer = new MasterRenderer(loader);

        List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
        GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        GuiTexture gui2 = new GuiTexture(loader.loadTexture("Creeper"), new Vector2f(0.3f, 0.3f), new Vector2f(0.2f, 0.2f));
//        guis.add(gui);
//        guis.add(gui2); //rendered after, so on top of other image(s)

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains);

        WaterFrameBuffer waterFrameBuffer = new WaterFrameBuffer();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFrameBuffer);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        waters.add(new WaterTile(150, -150, 0));
        waters.add(new WaterTile(75, -55, 0));


//        GuiTexture refraction= new GuiTexture(waterFrameBuffer.getRefractionTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        GuiTexture reflection= new GuiTexture(waterFrameBuffer.getReflectionTexture(), new Vector2f(-0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
//        guiTextures.add(refraction);
//        guiTextures.add(reflection);

        lights.add(new Light(new Vector3f(100, 20, -50), new Vector3f(2,2,0), new Vector3f(1, 0.01f, 0.002f)));
        lights.sort(new LightComparator(player));
//        for (Light light: lights){
//            System.out.println(light.getPosition());
//        }


        while(!Display.isCloseRequested()){
            player.checkMoves(terrains);
//            System.out.println(DisplayManager.getFPS());
//            System.out.println(player.getPosition());
            camera.move();
            picker.update();

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //TODO make this a keyPressed(KeyEvent e)
            Vector3f terrainPoint = picker.getCurrentTerrainPoint();

            while (Keyboard.next() && terrainPoint != null){
                float y = MousePicker.getTerrainPointY(terrainPoint.x, terrainPoint.z, terrains[0][0]);
                if (Keyboard.getEventKey() == Keyboard.KEY_LCONTROL) {
                    if (Keyboard.getEventKeyState()) {
                        entities.add(new Entity(pine, new Vector3f(terrainPoint.x, y, terrainPoint.z), 0, random.nextFloat() * 360, 0, random.nextFloat() *1 + 0.5f));
                    }
                }
//                if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) {
//                    if (Keyboard.getEventKeyState()) {
//                        picker.moveEntity(new Vector3f(terrainPoint.x, y, terrainPoint.z), entities);
//                    }
//                }
                if (Keyboard.getEventKey() == Keyboard.KEY_Z) {
                    if (Keyboard.getEventKeyState()) {
                        waters.add(new WaterTile(terrainPoint.x, terrainPoint.z, 0));
                    }
                }
            }

//
//            //TODO precisie weghalen en terugplaatsen verhogen!
            if (terrainPoint != null && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                float y = MousePicker.getTerrainPointY(terrainPoint.x, terrainPoint.z, terrains[0][0]);
                picker.moveEntity(new Vector3f(terrainPoint.x, y, terrainPoint.z), entities);
            }
            renderer.processEntity(dragon);
            dragon.increaseRotation(0,0.25f, 0);

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
            renderer.renderScene(entities, terrains, lights, camera, player, new Vector4f(0, 1, 0, 100000)); //change w: to height of clipping
            waterRenderer.render(waters, camera, sun);
            guiRenderer.render(guiTextures); //wordt nu nog niet gebruikt
            DisplayManager.updateDisplay();
//            System.out.println((player.getPosition().x % 800));
        }

        waterFrameBuffer.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }
    public static int getMAPWIDTH() {
        return MAPWIDTH;
    }

    public static int getMAPDEPTH() {
        return MAPDEPTH;
    }
}
