import java.nio.FloatBuffer;

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import mazepack.Edge;
import mazepack.Maze;
import mazepack.Queue;


public class First3D_Core implements ApplicationListener, InputProcessor
{
    Camera cam;
    private boolean ligthBulbState = true;
    private float mapsize; //power of cellsize
    private float wallheight = 4;
    private float cellsize = 8;  //power of two
    private int cellsperside;
    private Cell [][] cells;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    private FloatBuffer cubeBuffer;
    private FloatBuffer diamondBuffer;
    private FloatBuffer cubeTexBuffer;

    private boolean flightmode = false;

    private int angle = 0;
    private boolean countdown = false;
    private long time;

    private int windowheight;
    private int windowwidth;

	private Maze maze;
	private Queue<Edge> edgelist;

    private Texture floortexture;
    private Texture walltexture;
    private Texture diamondtexture;

    @Override
    public void create() {
        windowheight = Gdx.graphics.getHeight();
        windowwidth = Gdx.graphics.getWidth();
        
        cellsperside = 3;

        this.secondCamera = new OrthographicCamera(windowwidth,windowheight);
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();


        Gdx.input.setInputProcessor(this);

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        this.cubeBuffer = BufferUtils.newFloatBuffer(72);
        this.cubeBuffer.put(new float[]{-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
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
        this.diamondBuffer.put(new float[]{
		        0.5f, 0f, 0f, 0f, 0f, 0.5f, 0f, 1f, 0f,     //points abe
		        0.5f, 0f, 0f, 0f, 0f, 0.5f, 0f, -1f, 0f,    //points abf
		        0f, 0f, 0.5f, -0.5f, 0f, 0f, 0f, 1f, 0f,     //points bce
		        0f, 0f, 0.5f, -0.5f, 0f, 0f, 0f, -1f, 0f,     //points bcf
		        -0.5f, 0f, 0f, 0f, 0f, -0.5f, 0f, 1f, 0f,     //points cde
		        -0.5f, 0f, 0f, 0f, 0f, -0.5f, 0f, -1f, 0f,     //points cdf
		        0f, 0f, -0.5f, 0.5f, 0f, 0f, 0f, 1f, 0f,     //points dae
		        0f, 0f, -0.5f, 0.5f, 0f, 0f, 0f, -1f, 0f,     //points daf
        });
        this.diamondBuffer.rewind();

        this.cubeTexBuffer = BufferUtils.newFloatBuffer(48);
        this.cubeTexBuffer.put(new float[] {
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f
        });
        this.cubeTexBuffer.rewind();
        

        cam = new Camera(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f));

        walltexture = new Texture("graphics/red-brick.jpg");
        floortexture = new Texture("graphics/yellow-brick.png");
        diamondtexture = new Texture("graphics/diamond.png");

        initialize();
    }

    private void initialize(){
        cam.eye.x = cam.eye.y = cam.eye.z = 2;
        cellsperside = (int)(cellsperside*1.5);
        mapsize = cellsize*cellsperside;

        this.initializeMaze();
    }

	private void initializeMaze()
	{
		cells = new Cell[cellsperside][cellsperside];//represent each cell in the maze

		maze = new Maze(cellsperside*cellsperside);
		edgelist = (Queue<Edge>) maze.getEdges();

		//populate the walls in the maze
		for (int i = 0; i < cellsperside; i++){
			for (int j = 0; j < cellsperside; j++){
                cells[i][j] = new Cell(false, false);
                if (i == cellsperside-1){
                    cells[i][j].northpath = true;
                }
                if (j == cellsperside-1){
                    cells[i][j].eastpath = true;
                }
			}
		}

		for(Edge e : edgelist)
		{
			int a = e.either();
			int b = e.other(a);

			if((b - a) == 1)
				cells[a/cellsperside][a%cellsperside].eastpath = true;//true ? (b-a == 1) : false;

			if((b - a) > 1)
				cells[a/cellsperside][a%cellsperside].northpath = true;//true ? (b-a > 1) : false;

		}
	}

    @Override
    public void dispose() {
        floortexture.dispose();
        walltexture.dispose();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    private void update() {

        float deltaTime = Gdx.graphics.getDeltaTime();

        if(Gdx.input.isKeyPressed(Input.Keys.A))
            cam.yaw(-120.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.D))
            cam.yaw(120.0f * deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.slide(0.0f, 0.0f, -10.0f * deltaTime);
            movementcheck();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            cam.slide(0.0f, 0.0f, 10.0f * deltaTime);
            movementcheck();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.slide(-10.0f * deltaTime, 0.0f, 0.0f);
            movementcheck();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.slide(10.0f * deltaTime, 0.0f, 0.0f);
            movementcheck();
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

    private void movementcheck(){
        if (flightmode) return;
        if (collisionX())
            rollbackX();
        if (collisionZ())
            rollbackZ();
        int i = cornercollision();
        if (i == 1){
            rollbackX();
        }
        if (i == 2){
            rollbackZ();
        }
    }

    private boolean victory(){
        if (flightmode) return false;

        if (cam.eye.x < mapsize-cellsize/2+2f && cam.eye.x > mapsize-cellsize/2-2f){
            if (cam.eye.z < mapsize-cellsize/2+2f && cam.eye.z > mapsize-cellsize/2-2f){
                return true;
            }
        }
        return false;
    }

    private void rollbackX(){
        if (cam.eye.x%cellsize <= 1.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + 1.5f;
        if (cam.eye.x%cellsize >= cellsize-1.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + cellsize-1.5f;
    }

    private void rollbackZ(){
        if (cam.eye.z%cellsize <= 1.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + 1.5f;
        if (cam.eye.z%cellsize >= cellsize-1.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + cellsize-1.5f;
    }

    /**running into a corner
     *
      * @return 0 if no collision, 1 if he should rollback in the x direction and 2 if he should rollback in the z direction
     */
    private int cornercollision(){
        float x = cam.eye.x%cellsize;
        float z = cam.eye.z%cellsize;
        int current_x_cell = (int)(cam.eye.x/cellsize);
        int current_z_cell = (int)(cam.eye.z/cellsize);
        if (x > 1.5f && x < 6.5f){
            return 0;
        }
        if (z > 1.5f && z < 6.5f){
            return 0;
        }

        //case 1: going backward in both x and z
        if (current_x_cell > 0 && current_z_cell > 0){
            if (x < 1.5f && z < 1.5f){
                if (!cells[current_x_cell-1][current_z_cell-1].northpath || !cells[current_x_cell-1][current_z_cell-1].eastpath){
                    if (x > z) return 1;
                    else return 2;
                }
            }
        }

        //case 2: going backward in x but forward in z
        if (current_x_cell > 0 && current_z_cell < cellsperside-1){
            if (x < 1.5f && z > 6.5f){
                if (!cells[current_x_cell-1][current_z_cell+1].northpath || !cells[current_x_cell-1][current_z_cell].eastpath){
                    if (x+5 < z) return 1;
                    else return 2;
                }

            }
        }

        //case 3: going forward in x but backward in z
        if (current_x_cell < cellsperside-1 && current_z_cell > 0){
            if (x > 6.5f && z < 1.5f){
                if (!cells[current_x_cell][current_z_cell-1].northpath || !cells[current_x_cell+1][current_z_cell-1].eastpath){
                    if (x < z+5) return 1;
                    else return 2;
                }
            }
        }
        //case 4: going forward in both x and z
        if (current_x_cell < cellsperside-1 && current_z_cell < cellsperside-1){
            if (x > 6.5f && z > 6.5f){
                if (!cells[current_x_cell][current_z_cell+1].northpath || !cells[current_x_cell+1][current_z_cell].eastpath){
                    if (x < z) return 1;
                    else return 2;
                }
            }
        }

        return 0;
    }

    private boolean collisionX(){
        int x = (int)(cam.eye.x/cellsize);
        int z =(int)(cam.eye.z/cellsize);

        //if we are at the edge
        if (x == cellsperside-1){
            if (cam.eye.x%cellsize >= cellsize-1.5f) return true;
        }
        //check if there is a path north
        else if (!cells[x][z].northpath){
            if(cam.eye.x%cellsize >= cellsize-1.5f) return true;
        }

        //if we are at the edge
        if (x == 0){
            if (cam.eye.x%cellsize <= 1.5f) return true;
        }
        //if there is a path to the south
        else if (!cells[x-1][z].northpath){
            if(cam.eye.x%cellsize <= 1.5f) return true;
        }

        return false;
    }

    private boolean collisionZ(){
        int x = (int)(cam.eye.x/cellsize);
        int z =(int)(cam.eye.z/cellsize);

        if (z == cellsperside-1){
            if (cam.eye.z%cellsize >= cellsize-1.5f) return true;
        }
        else if (!cells[x][z].eastpath){
            if(cam.eye.z%cellsize >= cellsize-1.5f) return true;
        }

        if (z == 0){
            if (cam.eye.z%cellsize <= 1.5f) return true;
        }
        else if (!cells[x][z-1].eastpath){
            if(cam.eye.z%cellsize <= 1.5f) return true;
        }

        return false;
    }

    private void drawBox(float length, float height, float width, float x, float y, float z, Texture tex) {
        Gdx.gl11.glShadeModel(GL11.GL_SMOOTH);
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.cubeBuffer);

        Gdx.gl11.glEnable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        tex.bind();

        Gdx.gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, cubeTexBuffer);

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
        drawBox(size,0.1f,size,size/2,0,size/2, floortexture);
    }

    private void drawmazeframe(float width, float height){
        drawBox(0.1f, height, width, width, height/2, width/2, walltexture);

        drawBox(0.1f, height, width, 0f, height / 2, width / 2, walltexture);

        drawBox(width, height, 0.1f, width/2, height/2, width, walltexture);

        drawBox(width, height, 0.1f, width/2, height/2, 0, walltexture);
    }

    private void drawcells(float cellwidth, float height){
        for (int i = 0; i < cellsperside; i++){
            for (int j = 0; j < cellsperside; j++){
                //cell[i][j]
                if (!cells[i][j].eastpath){
                    drawBox(cellsize,height,0.1f,cellwidth*i+cellwidth/2, height/2, cellwidth*j+cellwidth, walltexture);
                }
                if (!cells[i][j].northpath){
                    drawBox(0.1f,height,cellsize,cellwidth*i+cellwidth, height/2, cellwidth*j+cellwidth/2, walltexture);
                }

            }
        }
    }

    private void drawdiamond(){
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.diamondBuffer);

        Gdx.gl11.glShadeModel(GL11.GL_SMOOTH);

        Gdx.gl11.glEnable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        diamondtexture.bind();

        Gdx.gl11.glTexParameteri(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        Gdx.gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, cubeTexBuffer);


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
        //Gdx.gl11.glClearColor(0.34f, 0.88f, 0.96f, 1.0f);
        Gdx.gl11.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        cam.setModelViewMatrix();

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();
        Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.0f, 1.0f, 300f);

        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);



        // Configure light 1
        /*float[] lightDiffuse1 = {1.0f, 1.0f, 1.0f, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse1, 0);

        float[] lightPosition1 = {mapsize,0.5f, mapsize, 1.0f};
        Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition1, 0);

	    Gdx.gl11.glEnable(GL11.GL_LIGHT0); */

	    // Configure light 2

	    float[] lightDiffuse2 = {0.4f, 0.4f, 0.4f, 1.0f};
	    Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse2, 0);

	    float[] lightPosition2 = {cam.eye.x  , cam.eye.y + 2, cam.eye.z , 1.0f};

	    Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition2, 0);
		/*Gdx.gl11.glLightf(GL10.GL_LIGHT1, GL11.GL_SPOT_CUTOFF, 60.0f);

	    float[] spot_direction = { cam.eye.x , cam.eye.y , cam.eye.z};
	    Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_SPOT_DIRECTION, ); */
	    Gdx.gl11.glEnable(GL11.GL_LIGHT1);

        // Set material on the floor.
        float[] floorMaterialDiffuse = {0.1f, 0.1f, 0.1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, floorMaterialDiffuse, 0);

        // Draw floor!
        drawFloor(mapsize);

        //Material for the maze walls
        float[] boxMaterialDiffuse = {0.1f, 0.1f, 0.1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, boxMaterialDiffuse, 0);

        //draw the outer walls
        drawmazeframe(mapsize, wallheight);

        //draw the maze
        drawcells(cellsize, wallheight);

        // Set the material on the diamond
        float[] diamondMaterialDiffuse = {1f, 1f, 1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT, GL11.GL_DIFFUSE, diamondMaterialDiffuse, 0);

        //draw the diamond
        drawdiamond();

        //Gdx.gl11.glDisable(GL11.GL_LIGHTING); WHY WAS THIS HERE?

        this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
        secondCamera.update();

        if (flightmode){
            this.spriteBatch.begin();
            font.setColor(1f,1f,1f,1f);
            font.draw(this.spriteBatch, String.format("Camera position: (%.2f, %.2f, %.2f)",this.cam.eye.x, this.cam.eye.y, this.cam.eye.z), -400, -280);
            font.draw(this.spriteBatch, String.format("Current cell: (%d, %d)",(int)(cam.eye.x/cellsize), (int)(cam.eye.z/cellsize)), -400, -300);
            this.spriteBatch.end();
        }

    }

    public void countdownscreen(){
        long count = (System.currentTimeMillis()-time) / 1000;

        // Clear the screen.
        Gdx.gl11.glClearColor(0.7f, 0.3f, 0f, 1);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);


        // Draw the congratulations text on the screen
        this.spriteBatch.begin();
        font.setColor(1, 1, 1, 1f);
        font.draw(this.spriteBatch, String.format("CONGRATULATIONS!"), -80,100);
        font.draw(this.spriteBatch, String.format("You found the Diamond !"), -90, 50);
        font.draw(this.spriteBatch, String.format("Next level starting in %d seconds", 5-count), -120, 0);
        this.spriteBatch.end();


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