/**
 * @author Halldór Örn Kristjásson
 * @author Ólafur Daði Jónsson
 */

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

/**
 * This is the "main" class that "plays" and runs the game.
 * @version 1.0
 */
public class First3D_Core implements ApplicationListener, InputProcessor
{
    //The eye of the player
    Camera cam;

    //Variables regarding to the size of the map/maze
    private float mapsize; //power of cellsize
    private float wallheight = 4;
    private float cellsize = 8;  //power of two
    private int cellsperside;
    private Cell [][] cells;

    // text
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private OrthographicCamera secondCamera;

    //3D objects
    private FloatBuffer cubeBuffer;
    private FloatBuffer diamondBuffer;
    private FloatBuffer cubeTexBuffer;

    //cheatmode
    private boolean flightmode = false;

    //diamond rotation
    private int angle = 0;

    //countdown between levels
    private boolean countdown = false;
    private long time;

    //Different textures for different 3D objects
    private Texture floortexture;
    private Texture walltexture;
    private Texture diamondtexture;

    @Override
    /**
     * This function is run when the application starts
     * It does some initializing and first time settings
     */
    public void create() {
        //initial size of the map - will be multiplied by 1.5 before the game starts.
        cellsperside = 3;

        //second camera used to print text on the screen
        this.secondCamera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();

        //handles keyboard input
        Gdx.input.setInputProcessor(this);

        //Lights
        Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

        //3D cube
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

        //3D diamond
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

        //Texture buffer
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
        
        //camera
        cam = new Camera(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f));

        //assign images to the textures
        walltexture = new Texture("graphics/red-brick.png");
        floortexture = new Texture("graphics/yellow-brick.png");
        diamondtexture = new Texture("graphics/diamond.png");

        //get shit done!
        initialize();
    }

    /**
     * Does some basic initializing
     */
    private void initialize(){
        //set the initial position of the eye
        cam.eye.x = cam.eye.y = cam.eye.z = 2;

        //let the maze grow by a factor of 1.5 on each side
        cellsperside = (int)(cellsperside*1.5);

        //calculate and store the mapsize
        mapsize = cellsize*cellsperside;

        this.initializeMaze();
    }

    /**
     * Generates and initializes a new maze
     */
	private void initializeMaze()
	{
        //represents each cell in the maze
		cells = new Cell[cellsperside][cellsperside];

        //generate a new maze
        Maze maze = new Maze(cellsperside * cellsperside);
        Queue<Edge> edgelist = (Queue<Edge>) maze.getEdges();

		//populate all the cells with no walls
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

        //put up walls where they should be
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
    /**
     * called when the application is closed
     */
    public void dispose() {
        floortexture.dispose();
        walltexture.dispose();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    /**
     * Updates all positions in the maze
     */
    private void update() {
        int hyperspeed;
        if (flightmode) hyperspeed = 4;
        else hyperspeed = 1;

        //gets the difference in time since the last update
        float deltaTime = Gdx.graphics.getDeltaTime();

        //the following functions all update the camera position depending on the key

        //turn to the left
        if(Gdx.input.isKeyPressed(Input.Keys.A))
            cam.yaw(-120.0f * deltaTime);

        //turn to the right
        if(Gdx.input.isKeyPressed(Input.Keys.D))
            cam.yaw(120.0f * deltaTime);

        //slide forward
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.slide(0.0f, 0.0f, -10.0f * deltaTime * hyperspeed);
        }

        //slide backward
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            cam.slide(0.0f, 0.0f, 10.0f * deltaTime * hyperspeed);
        }

        //slide to the left
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
            cam.slide(-10.0f * deltaTime * hyperspeed, 0.0f, 0.0f);
        }

        //slide to the right
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.slide(10.0f * deltaTime * hyperspeed, 0.0f, 0.0f);
        }
        movementcheck();

        //cheatmode
        if (flightmode){
            //slide up
            if(Gdx.input.isKeyPressed(Input.Keys.R))
                cam.slide(0.0f, 10.0f * deltaTime * hyperspeed, 0.0f);

            //slide down
            if(Gdx.input.isKeyPressed(Input.Keys.F))
                cam.slide(0.0f, -10.0f * deltaTime * hyperspeed, 0.0f);
        }

        //increase the angle on the diamond (effectively rotating it)
        angle++;

        //check for victory :)
        if (victory()){
            countdown = true;
            time = System.currentTimeMillis();
        }
    }

    /**
     * Checks if the move just made has put in an invalid position.
     * And if it has, then returns the camera to the nearest allowed position.
     */
    private void movementcheck(){
        //in flightmode you should be able to go anywhere
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

    /**
     * Checks if the camera is in a victory position
     * i.e. the camera is in the nearest vicinity of the diamond
     * @return true if you have reached victory and false if not.
     */
    private boolean victory(){
        if (flightmode) return false;

        if (cam.eye.x < mapsize-cellsize/2+2f && cam.eye.x > mapsize-cellsize/2-2f){
            if (cam.eye.z < mapsize-cellsize/2+2f && cam.eye.z > mapsize-cellsize/2-2f){
                return true;
            }
        }
        return false;
    }

    /**
     * Moves the camera to the nearest allowed x coordinate
     */
    private void rollbackX(){
        if (cam.eye.x%cellsize <= 1.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + 1.5f;
        if (cam.eye.x%cellsize >= cellsize-1.5f) cam.eye.x = (int)(cam.eye.x/cellsize)*cellsize + cellsize-1.5f;
    }

    /**
     * Moves the camera to the nearest allowed z coordinate
     */
    private void rollbackZ(){
        if (cam.eye.z%cellsize <= 1.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + 1.5f;
        if (cam.eye.z%cellsize >= cellsize-1.5f) cam.eye.z = (int)(cam.eye.z/cellsize)*cellsize + cellsize-1.5f;
    }

    /**
     * This function checks if you are running into the corners of outstanding walls and corners
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

        Gdx.gl11.glDisable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    }

    private void drawFloor() {
        for (int i = 0 ; i < cellsperside ; i++){
            for (int j = 0 ; j < cellsperside; j++)
            drawBox(cellsize,0.1f,cellsize,i*cellsize + cellsize/2,0,j*cellsize + cellsize/2, floortexture);
        }
    }

    private void drawmazeframe(){
        for (int i = 0; i < cellsperside ; i++){
            drawBox(0.1f, wallheight, cellsize, mapsize, wallheight/2, cellsize/2 + i*cellsize, walltexture);
            drawBox(0.1f, wallheight, cellsize, 0f, wallheight / 2, cellsize/2 + i*cellsize, walltexture);
            drawBox(cellsize, wallheight, 0.1f, cellsize/2 + i*cellsize, wallheight/2, mapsize, walltexture);
            drawBox(cellsize, wallheight, 0.1f, cellsize/2 + i*cellsize, wallheight/2, 0, walltexture);
        }
    }

    private void drawcells(){
        for (int i = 0; i < cellsperside; i++){
            for (int j = 0; j < cellsperside; j++){
                //cell[i][j]
                if (!cells[i][j].eastpath){
                    drawBox(cellsize,wallheight,0.1f,cellsize*i+cellsize/2, wallheight/2, cellsize*j+cellsize, walltexture);
                }
                if (!cells[i][j].northpath){
                    drawBox(0.1f,wallheight,cellsize,cellsize*i+cellsize, wallheight/2, cellsize*j+cellsize/2, walltexture);
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

        Gdx.gl11.glDisable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    }

    private void display() {
        Gdx.gl11.glClearColor(0f, 0f, 0f, 1.0f);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);


        cam.setModelViewMatrix();

        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();
        Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.0f, 1.0f, 300f);

        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);

        Gdx.gl11.glEnable(GL11.GL_LIGHT1);

	    // Configure light 1

	    float[] lightDiffuse2 = {0.1f, 0.1f, 0.1f, 1.0f};
	    Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_SPECULAR, lightDiffuse2, 0);

	    float[] lightPosition2 = {cam.eye.x  , cam.eye.y + 2f, cam.eye.z , 1.0f};

	    Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition2, 0);

        // Set material on the cubes.
        float[] cubeMaterialDiffuse = {0.1f, 0.1f, 0.1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, cubeMaterialDiffuse, 0);
        Gdx.gl11.glMaterialf(GL11.GL_FRONT_AND_BACK, GL11.GL_SHININESS, 2);

        // Draw floor!
        drawFloor();

        //draw the outer walls
        drawmazeframe();

        //draw the maze
        drawcells();

        // Set the material on the diamond
        float[] diamondMaterialDiffuse = {1f, 1f, 1f, 1.0f};
        Gdx.gl11.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, diamondMaterialDiffuse, 0);
        Gdx.gl11.glMaterialf(GL11.GL_FRONT_AND_BACK, GL11.GL_SHININESS, 14);

        //draw the diamond
        drawdiamond();

        if (flightmode){
            Gdx.gl11.glDisable(GL11.GL_LIGHTING);
            this.spriteBatch.setProjectionMatrix(this.secondCamera.combined);
            secondCamera.update();
            this.spriteBatch.begin();
            font.setColor(1f,1f,1f,1f);
            font.draw(this.spriteBatch, String.format("Camera position: (%.2f, %.2f, %.2f)",this.cam.eye.x, this.cam.eye.y, this.cam.eye.z), -400, -280);
            font.draw(this.spriteBatch, String.format("Current cell: (%d, %d)",(int)(cam.eye.x/cellsize), (int)(cam.eye.z/cellsize)), -400, -300);
            this.spriteBatch.end();
            Gdx.gl11.glEnable(GL11.GL_LIGHTING);
        }
    }

    public void countdownscreen(){
        long count = (System.currentTimeMillis()-time) / 1000;

        // Clear the screen.
        Gdx.gl11.glClearColor(0.7f, 0.3f, 0f, 1);
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        Gdx.gl11.glDisable(GL11.GL_LIGHTING);

        // Draw the congratulations text on the screen
        this.spriteBatch.begin();
        font.setColor(1f, 1f, 1f, 1f);
        font.draw(this.spriteBatch, String.format("CONGRATULATIONS!"), -80,100);
        font.draw(this.spriteBatch, String.format("You found the Diamond !"), -90, 50);
        font.draw(this.spriteBatch, String.format("Next level starting in %d seconds", 5-count), -120, 0);
        this.spriteBatch.end();

        Gdx.gl11.glEnable(GL11.GL_LIGHTING);

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