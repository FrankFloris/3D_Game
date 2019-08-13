package toolbox;

import entities.Entity;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import renderEngine.DisplayManager;
import terrains.Terrain;
import entities.Camera;

import java.security.Key;
import java.util.List;

public class MousePicker {

    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;

    private Terrain[][] terrains;
    private Vector3f currentTerrainPoint;
    private Entity entityInHand;

    public MousePicker(Camera cam, Matrix4f projection, Terrain[][] terrains) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix(camera);
        this.terrains = terrains;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
            assert currentTerrainPoint != null;
            currentTerrainPoint.y = terrains[(int) Math.floor((currentTerrainPoint.x % Terrain.SIZE) / Terrain.SIZE + 1)]
                    [(int) Math.floor((currentTerrainPoint.z%Terrain.SIZE) / Terrain.SIZE+1)].getHeightOfTerrain(currentTerrainPoint.x, currentTerrainPoint.z);
        } else {
            currentTerrainPoint = null;
        }
    }

    private Vector3f calculateMouseRay() {
        float mouseX = Mouse.getX();
        float mouseY = Mouse.getY();
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        return toWorldCoords(eyeCoords);
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = Matrix4f.invert(viewMatrix, null);
        Vector4f rayWorld = Matrix4f.transform(invertedView, eyeCoords, null);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalise();
        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = Matrix4f.invert(projectionMatrix, null);
        Vector4f eyeCoords = Matrix4f.transform(invertedProjection, clipCoords, null);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Display.getWidth() - 1f;
        float y = (2.0f * mouseY) / Display.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    //**********************************************************

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return Vector3f.add(start, scaledRay, null);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Terrain terrain = getTerrain(endPoint.getX(), endPoint.getZ());
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        return !isUnderGround(startPoint) && isUnderGround(endPoint);
    }

    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.getX(), testPoint.getZ());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.getX(), testPoint.getZ());
        }
        return testPoint.y < height;
    }

    private Terrain getTerrain(float worldX, float worldZ) {
        int x = (int)(worldX / Terrain.SIZE);
        int z = (int)(worldZ / Terrain.SIZE);

        if (x < 0 || x > 1 ||z < 0 || z > 1){
            x = 0;
            z = 0;
        }

        return terrains[x][z];
    }

    public void grabEntity(Vector3f terrainPoint, List<Entity> entities) {
        float radius = 4f;
        if(terrainPoint != null){
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && entityInHand == null && Mouse.isButtonDown(0)){
                for (Entity entity: entities){
                    float entityX = entity.getPosition().x;
                    float entityZ = entity.getPosition().z;
                    if (entityX-radius < terrainPoint.x && entityX+radius > terrainPoint.x &&
                            entityZ-radius < terrainPoint.z && entityZ+radius > terrainPoint.z) {
                        entity.increasePosition(1000.0f, 1000.0f, 1000.0f);
                        entityInHand = entity;
                        entities.remove(entity);
                        break;
                    }
                }
            }
             else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && entityInHand != null && Mouse.isButtonDown(1)){
                entityInHand.setPosition(terrainPoint);
                entities.add(entityInHand);
                entityInHand = null;
            }

        }
    }
}
