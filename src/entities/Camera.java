package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 0;


    private Vector3f position = new Vector3f(100,35,50);
    private float pitch = 10;
    private float yaw;
    private float roll;

    private Player player;

    public Camera(Player player){
        this.player = player;
    }

    public void move(){
        calculateZoom();
        calculatePitchAndAngle();
//        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance + 4; //TODO hier verder aan werken
//        System.out.println(verticalDistance);

    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.035f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer < 20){
            distanceFromPlayer = 20;
        }
        if (distanceFromPlayer > 120){
            distanceFromPlayer = 120;
        }
    }

    private void calculatePitchAndAngle(){
        if(Mouse.isButtonDown(0) || Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            float anglechange = Mouse.getDX() * 0.3f;
            pitch -= pitchChange;
            if (pitch < 0.1f){
                pitch = 0.1f;
            }
            angleAroundPlayer -= anglechange;
        }
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }

//    private void calculateAngleAroundPlayer(){
//        if (Mouse.isButtonDown(0)){
//            float angleChange = Mouse.getDX() * 0.3f;
//            angleAroundPlayer -= angleChange;
//        }
//    }
}
