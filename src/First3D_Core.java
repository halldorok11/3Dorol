import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;


public class First3D_Core implements ApplicationListener, InputProcessor
{
    Camera cam;
    private boolean ligthBulbState = true;
    private float mapsize = 128; //power of two
    private float wallheight = 4;
    private float cellsize = 8;  //power of two
    private float cellsperside = 128/8;
    private Cell [][] cells;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    private FloatBuffer vertexBuffer;


    @Override
    public void create() {

        this.secondCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();


        Gdx.input.setInputProcessor(this);

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);

        Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);

        Gdx.gl11.glClearColor(0.34f, 0.88f, 0.96f, 1.0f);

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        this.vertexBuffer = BufferUtils.newFloatBuffer(72);
        this.vertexBuffer.put(new float[] {-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f, 0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, -0.5f, 0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f});
        this.vertexBuffer.rewind();

        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.vertexBuffer);
        cam = new Camera(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f));

        initializemaze();
    }

    private void initializemaze(){
        cells = new Cell[(int)cellsperside][(int)cellsperside];//represent each cell in the maze

        //populate the walls in the maze
        for (int i = 0; i < (int)cellsperside; i++){
            for (int j = 0; j < (int)cellsperside; j++){
                System.out.println("i: " + i + " j: " + j);
                cells[i][j].east = true;
                cells[i][j].north = true;
            }
        }
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    private void update() {
        if(this.ligthBulbState)
            Gdx.gl11.glEnable(GL11.GL_LIGHT0);
        else
            Gdx.gl11.glDisable(GL11.GL_LIGHT0);

        float deltaTime = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            cam.yaw(-90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.D))
            cam.yaw(90.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.W))
            cam.slide(0.0f, 0.0f, -10.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.S))
            cam.slide(0.0f, 0.0f, 10.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.Q))
            cam.slide(-10.0f * deltaTime, 0.0f, 0.0f);

        if(Gdx.input.isKeyPressed(Input.Keys.E))
            cam.slide(10.0f * deltaTime, 0.0f, 0.0f);

        if(Gdx.input.isKeyPressed(Input.Keys.R))
            cam.slide(0.0f, 10.0f * deltaTime, 0.0f);

        if(Gdx.input.isKeyPressed(Input.Keys.F))
            cam.slide(0.0f, -10.0f * deltaTime, 0.0f);
    }

    private void drawBox(float length, float height, float width, float x, float y, float z) {
        Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(x, y, z);
        Gdx.gl11.glScalef(length, height, width);

        Gdx.gl11.glNormal3f(0.0f, 0.0f, -1.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        Gdx.gl11.glNormal3f(1.0f, 0.0f, 0.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 4, 4);
        Gdx.gl11.glNormal3f(0.0f, 0.0f, 1.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 8, 4);
        Gdx.gl11.glNormal3f(-1.0f, 0.0f, 0.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 12, 4);
        Gdx.gl11.glNormal3f(0.0f, 1.0f, 0.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 16, 4);
        Gdx.gl11.glNormal3f(0.0f, -1.0f, 0.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 20, 4);

        Gdx.gl11.glPopMatrix();
    }

    private void drawFloor(float size) {
        drawBox(size,0.1f,size,size/2,0,size/2);
    }

    private void drawmazeframe(float width, float height){
        drawBox(0.1f, height, width, width, height/2, width/2);

        drawBox(0.1f, height, width, 0f, height / 2, width / 2);

        drawBox(width, height, 0.1f, width/2, height/2, width);

        drawBox(width, height, 0.1f, width/2, height/2, 0);
    }

    private void drawcells(float cellwidth, float height){
        for (int i = 0; i < cellsperside; i++){
            for (int j = 0; j < cellsperside; j++){
                //cell[i][j]
                if (cells[i][j].east){
                    drawBox(cellsize,1f,1f,cellwidth*i+cellwidth/2, height/2, cellwidth*j+cellwidth/2);
                }
                if (cells[i][j].north){
                    //drawBox(1f,1f,1f,cellwidth*i+cellwidth/2, height/2, cellwidth*j+cellwidth/2);
                }

            }
        }
    }

    private void display() {

        // Draw some text on the screen
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.vertexBuffer);

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();
        Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.333333f, 1.0f, 300f);

        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
        cam.setModelViewMatrix();

        // Configure light 1
        float[] lightDiffuse1 = {0.5f, 0.5f, 0.5f, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse1, 0);

        float[] lightPosition1 = {5.0f, 10.0f, 15.0f, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition1, 0);

        // Set material on the floor.
        float[] floorMaterialDiffuse = {1f, .3f, 0.6f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT, floorMaterialDiffuse, 0);

        // Draw floor!
        drawFloor(mapsize);

        //Material for the maze walls
        float[] boxMaterialDiffuse = {0.3f, 1f, 0.6f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_AMBIENT, boxMaterialDiffuse, 0);

        //draw the outer walls
        drawmazeframe(mapsize, wallheight);

        //draw the maze
        drawcells(cellsize, wallheight);

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);

        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();

        this.spriteBatch.begin();
        font.setColor(1f,1f,1f,1f);
        font.draw(this.spriteBatch, String.format("Camera position (%.2f, %.2f, %.2f)",this.cam.eye.x, this.cam.eye.y, this.cam.eye.z), -400, -280);
        this.spriteBatch.end();
    }

    @Override
    public void render() {
        update();
        display();
    }

    @Override
    public void resize(int arg0, int arg1) {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        return false;
    }
}