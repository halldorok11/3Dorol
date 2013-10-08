
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.badlogic.gdx.assets.loaders.TextureAtlasLoader;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;


public class First3D_Core implements ApplicationListener, InputProcessor
{
	Camera cam;
	private boolean ligthBulbState = true;
	private float count = 0;
    FloatBuffer boxBuffer;
    FloatBuffer floorBuffer;

    private Texture floortexture;
    private Texture walltexture;
    private TextureRegion region;
    private Flat floor;
		
	@Override
	public void create() {
		
		Gdx.input.setInputProcessor(this);
		
		Gdx.gl11.glEnable(GL11.GL_LIGHTING);
		
		Gdx.gl11.glEnable(GL11.GL_LIGHT1);
		Gdx.gl11.glEnable(GL11.GL_DEPTH_TEST);
		
		Gdx.gl11.glClearColor(0.34f, 0.8f, 0.92f, 1.0f); //Sky blue

		Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
		Gdx.gl11.glLoadIdentity();
		Gdx.glu.gluPerspective(Gdx.gl11, 90, 1.33333f, 1f, 300f);

		Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		boxBuffer = BufferUtils.newFloatBuffer(72);
		boxBuffer.put(new float[] {-0.5f, -0.5f, -0.5f, -0.5f, 0.5f, -0.5f,
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
		boxBuffer.rewind();

        floorBuffer = BufferUtils.newFloatBuffer(12);
        floorBuffer.put(new float[] {   0.5f, 0f, 0.5f,
                                        -0.5f, 0f, 0.5f,
                                        0.5f, 0f, -0.5f,
                                        -0.5f, 0f, -0.5f
                                    });
        floorBuffer.rewind();

		cam = new Camera(new Point3D(0.0f, 3.0f, 2.0f), new Point3D(2.0f, 3.0f, 3.0f), new Vector3D(0.0f, 1.0f, 0.0f));

        Gdx.gl11.glEnable(GL_TEXTURE_2D);
        floortexture = new Texture(Gdx.files.internal("graphics/grass-texture-2.jpg"));
        floor = new Flat(16,16,0,0,0,10);
    }

	@Override
	public void dispose() {
        floortexture.dispose();
		
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

		//if(Gdx.input.isKeyPressed(Input.Keys.UP))
		//	cam.pitch(-90.0f * deltaTime);
		
		//if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
		//	cam.pitch(90.0f * deltaTime);
		
		if(Gdx.input.isKeyPressed(Input.Keys.A))
			cam.yaw(-90.0f * deltaTime);
		
		if(Gdx.input.isKeyPressed(Input.Keys.D))
			cam.yaw(90.0f * deltaTime);
		
		if(Gdx.input.isKeyPressed(Input.Keys.W)) 
			cam.slide(0.0f, 0.0f, -10.0f * deltaTime);
		
		if(Gdx.input.isKeyPressed(Input.Keys.S)) 
			cam.slide(0.0f, 0.0f, 10.0f * deltaTime);
		
		//if(Gdx.input.isKeyPressed(Input.Keys.A))
		//	cam.slide(-10.0f * deltaTime, 0.0f, 0.0f);
		
		//if(Gdx.input.isKeyPressed(Input.Keys.D))
		//	cam.slide(10.0f * deltaTime, 0.0f, 0.0f);
		
		if(Gdx.input.isKeyPressed(Input.Keys.R)) 
			cam.slide(0.0f, 10.0f * deltaTime, 0.0f);
		
		if(Gdx.input.isKeyPressed(Input.Keys.F)) 
			cam.slide(0.0f, -10.0f * deltaTime, 0.0f);
	}
	
	private void drawBox() {
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, boxBuffer);

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
	}
	
	private void drawFloor(float size) {
		/*for(float fx = 0.0f; fx < size; fx += 1.0) {
			for(float fz = 0.0f; fz < size; fz += 1.0) {
				Gdx.gl11.glPushMatrix();
				Gdx.gl11.glTranslatef(fx, 1.0f, fz);
				Gdx.gl11.glScalef(0.95f, 0.95f, 0.95f);
				drawBox();
				Gdx.gl11.glPopMatrix();
			}
		}*/
        /*
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, floorBuffer);

        Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(0f, 0f, 0f);
        Gdx.gl11.glScalef(size, size, size);
        Gdx.gl11.glNormal3f(0f, 1f, 0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        //drawBox();
        Gdx.gl11.glPopMatrix();
        */

        floor.render(floortexture);
	}
	
	private void display() {
		Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
		cam.setModelViewMatrix();

		// Configure light 0
		float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, lightDiffuse, 0);

		float[] lightPosition = {0, 10, 0, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition, 0);
        /*
		// Configure light 1
		float[] lightDiffuse1 = {0.5f, 0.5f, 0.5f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_DIFFUSE, lightDiffuse1, 0);

		float[] lightPosition1 = {-5.0f, -10.0f, -15.0f, 1.0f};
		Gdx.gl11.glLightfv(GL11.GL_LIGHT1, GL11.GL_POSITION, lightPosition1, 0);
		 */
		// Set material on the cube.
		//float[] materialDiffuse = {1f, 0.3f, 0.6f, 1.0f};
		//Gdx.gl11.glMaterialfv(GL11.GL_FRONT_AND_BACK, GL11.GL_DIFFUSE, materialDiffuse, 0);

		// Draw floor!
		drawFloor(50);
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

		if(arg0 == Input.Keys.L){
			this.ligthBulbState = this.ligthBulbState ? false:true;
		}
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
