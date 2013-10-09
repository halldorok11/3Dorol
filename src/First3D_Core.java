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
import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_PARAMS;


public class First3D_Core implements ApplicationListener, InputProcessor
{
    Camera cam;
    private boolean ligthBulbState = true;
    private float mapsize; //power of cellsize
    private float wallheight = 4;
    private float cellsize = 8;  //power of two
    private float cellsperside;
    private Cell [][] cells;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    private FloatBuffer cubeBuffer;
    private FloatBuffer diamondBuffer;

    private boolean flightmode = false;

    private int angle = 0;
    private boolean countdown = false;
    private long time;



    @Override
    public void create() {
        
        cellsperside = 3;

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

        this.cubeBuffer = BufferUtils.newFloatBuffer(72);
        this.cubeBuffer.put(new float[] {-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
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
        this.cubeBuffer.rewind();

        this.diamondBuffer = BufferUtils.newFloatBuffer(72);
        //base = point a, b, c and d
        //top point = e
        //bottom point = f
        this.diamondBuffer.put(new float[] {
                0.5f,0f,0f,    0f,0f,0.5f,    0f,1f,0f,     //points abe
                0.5f,0f,0f,    0f,0f,0.5f,    0f,-1f,0f,    //points abf
                0f,0f,0.5f,    -0.5f,0f,0f,   0f,1f,0f,     //points bce
                0f,0f,0.5f,    -0.5f,0f,0f,   0f,-1f,0f,     //points bcf
                -0.5f,0f,0f,   0f,0f,-0.5f,   0f,1f,0f,     //points cde
                -0.5f,0f,0f,   0f,0f,-0.5f,   0f,-1f,0f,     //points cdf
                0f,0f,-0.5f,   0.5f,0f,0f,    0f,1f,0f,     //points dae
                0f,0f,-0.5f,   0.5f,0f,0f,    0f,-1f,0f,     //points daf
        });
        this.diamondBuffer.rewind();
        

        cam = new Camera(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f));

        initialize();
    }

    private void initialize(){
        cam.eye.x = cam.eye.y = cam.eye.z = 2;
        cellsperside = (int)(cellsperside*1.5);
        mapsize = cellsize*cellsperside;

        cells = new Cell[(int)cellsperside][(int)cellsperside];//represent each cell in the maze

        //populate the walls in the maze
        for (int i = 0; i < (int)cellsperside; i++){
            for (int j = 0; j < (int)cellsperside; j++){
                cells[i][j] = new Cell(false, true);
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
            cam.yaw(-120.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.D))
            cam.yaw(120.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.slide(0.0f, 0.0f, -10.0f * deltaTime);
            if (collisionX())
                rollbackX();
            if (collisionZ())
                rollbackZ();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            cam.slide(0.0f, 0.0f, 10.0f * deltaTime);
            if (collisionX())
                rollbackX();
            if (collisionZ())
                rollbackZ();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.slide(-10.0f * deltaTime, 0.0f, 0.0f);
            if (collisionX())
                rollbackX();
            if (collisionZ())
                rollbackZ();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.slide(10.0f * deltaTime, 0.0f, 0.0f);
            if (collisionX())
                rollbackX();
            if (collisionZ())
                rollbackZ();
        }

        if (flightmode){
            if(Gdx.input.isKeyPressed(Input.Keys.R))
                cam.slide(0.0f, 10.0f * deltaTime, 0.0f);

                if(Gdx.input.isKeyPressed(Input.Keys.F))
                cam.slide(0.0f, -10.0f * deltaTime, 0.0f);
        }

        angle++;

        if (victory()){
            countdown = true;
            time = System.currentTimeMillis();
        }
    }

    private boolean victory(){
        if (cam.eye.x < mapsize-cellsize/2+2f && cam.eye.x > mapsize-cellsize/2-2f){
            if (cam.eye.z < mapsize-cellsize/2+2f && cam.eye.z > mapsize-cellsize/2-2f){
                return true;
            }
        }
        return false;
    }

    private void rollbackX(){
        if (cam.eye.x%cellsize < 1.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + 1.5f;
        if (cam.eye.x%cellsize > 6.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + 6.5f;
    }

    private void rollbackZ(){
        if (cam.eye.z%cellsize < 1.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + 1.5f;
        if (cam.eye.z%cellsize > 6.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + 6.5f;
    }

    private boolean collisionX(){
        if (flightmode) return false;

        int x = (int)(cam.eye.x/cellsize);
        int z =(int)(cam.eye.z/cellsize);

        if (x == cellsperside-1){
            if (cam.eye.x%cellsize > 6.5f) return true;
        }
        else if (cells[x][z].north){
            if(cam.eye.x%cellsize > 6.5f) return true;
        }

        if (x == 0){
            if (cam.eye.x%cellsize < 1.5) return true;
        }
        else if (cells[x-1][z].north){
            if(cam.eye.x%cellsize < 1.5f) return true;
        }

        return false;
    }

    private boolean collisionZ(){
        if (flightmode) return false;

        int x = (int)(cam.eye.x/cellsize);
        int z =(int)(cam.eye.z/cellsize);

        if (z == cellsperside-1){
            if (cam.eye.z%cellsize > 6.5f) return true;
        }
        else if (cells[x][z].east){
            if(cam.eye.z%cellsize > 6.5f) return true;
        }

        if (z == 0){
            if (cam.eye.z%cellsize < 1.5) return true;
        }
        else if (cells[x][z-1].east){
            if(cam.eye.z%cellsize < 1.5f) return true;
        }

        return false;
    }

    private void drawBox(float length, float height, float width, float x, float y, float z) {
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.cubeBuffer);

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
                    drawBox(cellsize,height,0.1f,cellwidth*i+cellwidth/2, height/2, cellwidth*j+cellwidth);
                }
                if (cells[i][j].north){
                    drawBox(0.1f,height,cellsize,cellwidth*i+cellwidth, height/2, cellwidth*j+cellwidth/2);
                }

            }
        }
    }

    private void drawdiamond(){
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.diamondBuffer);

        Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(mapsize-cellsize/2 ,2, mapsize-cellsize/2);
        Gdx.gl11.glScalef(2f, 2f, 2f);
        Gdx.gl11.glRotatef(angle,0,1,0);

        Gdx.gl11.glNormal3f(0.5f, 0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        Gdx.gl11.glNormal3f(0.5f, -0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 3, 3);
        Gdx.gl11.glNormal3f(-0.5f, 0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 6, 3);
        Gdx.gl11.glNormal3f(-0.5f, -0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 9, 3);
        Gdx.gl11.glNormal3f(-0.5f, 0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 12, 3);
        Gdx.gl11.glNormal3f(-0.5f, -0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 15, 3);
        Gdx.gl11.glNormal3f(0.5f, 0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 18, 3);
        Gdx.gl11.glNormal3f(0.5f, -0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 21, 3);

        Gdx.gl11.glPopMatrix();
    }

    private void display() {

        // Draw some text on the screen
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.cubeBuffer);

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

        //draw the diamond
        drawdiamond();

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);

        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();

        this.spriteBatch.begin();
        font.setColor(1f,1f,1f,1f);
        font.draw(this.spriteBatch, String.format("Camera position: (%.2f, %.2f, %.2f)",this.cam.eye.x, this.cam.eye.y, this.cam.eye.z), -400, -280);
        font.draw(this.spriteBatch, String.format("Current cell: (%d, %d)",(int)(cam.eye.x/cellsize), (int)(cam.eye.z/cellsize)), -400, -300);
        this.spriteBatch.end();
    }

    public void countdownscreen(){
        long count = (System.currentTimeMillis()-time) / 1000;
        System.out.println(5-count);
        if (5-count < 0.1f) {
            countdown = false;
            initialize();
        }
    }

    @Override
    public void render() {
        if (countdown){
            countdownscreen();
        }
        else{
            update();
            display();
        }
    }

    @Override
    public void resize(int arg0, int arg1) {
    }

    @Override
    public void resume() {
    }

    @Override
    public boolean keyDown(int arg0) {
        if (arg0 == Input.Keys.P){
            if (flightmode){
                flightmode = false;

                //if the eye is outside the maze throw it inside.
                if (cam.eye.x < 0) cam.eye.x = 0.1f;
                if (cam.eye.x > mapsize) cam.eye.x = mapsize-0.1f;
                if (cam.eye.z < 0) cam.eye.z = 0.1f;
                if (cam.eye.z > mapsize) cam.eye.z = mapsize-0.1f;

                // now place the eye in the dead center of the current box
                cam.eye.y = wallheight/2;
                cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + cellsize/2;
                cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + cellsize/2;

            }
            else {
                flightmode = true;
            }
        }
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