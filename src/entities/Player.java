package entities;

import com.sun.tools.javac.Main;
import engineTester.MainGameLoop;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import terrains.Terrain;

public class Player extends Entity {

    private static final float RUN_SPEED = 120;
    private static final float TURN_SPEED = 120;
    private static final float GRAVITY = -50;
    private static final float JUMP_POWER = 30;

//    private static final float TERRAIN_HEIGHT = 0;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;

    private boolean isInAir = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    private void jump(){
        if(!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    public void move(Terrain terrain){
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);

        try {
            float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);

            if (super.getPosition().y < terrainHeight) {
                upwardsSpeed = 0;
                isInAir = false;
                super.getPosition().y = terrainHeight;
            }
        } catch (Exception e) {
            System.out.println("Mustn't fall off the world!");
            upwardsSpeed = 0;
        }
    }

    private void checkInputs(){
        if(Keyboard.isKeyDown(Keyboard.KEY_W)){
            this.currentSpeed = RUN_SPEED;
        } else if(Keyboard.isKeyDown(Keyboard.KEY_S)){
            this.currentSpeed = -RUN_SPEED;
        } else {
            this.currentSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_A)){
            this.currentTurnSpeed = TURN_SPEED;
        }
        else if(Keyboard.isKeyDown(Keyboard.KEY_D)){
            this.currentTurnSpeed = -TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
        }

    }

    public void checkMoves(Terrain[][] terrains) {
        int gridX = (int)(this.getPosition().x / Terrain.SIZE+1);
        int gridZ = (int)(this.getPosition().z / Terrain.SIZE+1);

        if (!(gridX >= MainGameLoop.getMAPWIDTH() || this.getPosition().x < -800 || gridZ >= MainGameLoop.getMAPDEPTH() || this.getPosition().z < -800)) {
            this.move(terrains[gridX][gridZ]);
        } else if(this.getPosition().x < -800 || this.getPosition().z < -800 ) {
            this.decreasePosition(-1, 0, -1);
            System.out.println("Don't fall off the world!");
        } else if(gridX >= MainGameLoop.getMAPWIDTH() || gridZ >= MainGameLoop.getMAPDEPTH()){
            this.decreasePosition(1, 0, 1);
            System.out.println("Don't fall off the world!");
        }
    }
}
