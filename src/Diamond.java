import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.FloatBuffer;

/**
 * This class holds all information necessary to draw and make a cube
 */
public class Diamond {
    float length;
    float height;
    float width;
    float x;
    float y;
    float z;
    Texture tex;
    FloatBuffer diamondBuffer;
    FloatBuffer diamondTexBuffer;

    public Diamond(float length, float height, float width, float x, float y, float z, Texture tex) {
        this.length = length;
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tex = tex;

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

        this.diamondTexBuffer = BufferUtils.newFloatBuffer(48);
        this.diamondTexBuffer.put(new float[] {
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f
        });
        this.diamondTexBuffer.rewind();
    }

    public void draw(float angle){
        Gdx.gl11.glVertexPointer(3, GL11.GL_FLOAT, 0, this.diamondBuffer);

        Gdx.gl11.glShadeModel(GL11.GL_SMOOTH);

        Gdx.gl11.glEnable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        this.tex.bind();

        Gdx.gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, diamondTexBuffer);


        Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this.x ,this.y, this.z);
        Gdx.gl11.glScalef(this.length, this.height, this.width);
        Gdx.gl11.glRotatef(angle,0,1,0);

        //TODO: Fix the normals!
        Gdx.gl11.glNormal3f(0.5f, 0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
        Gdx.gl11.glNormal3f(-0.5f, 0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 3, 3);
        Gdx.gl11.glNormal3f(0.5f, -0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 6, 3);
        Gdx.gl11.glNormal3f(-0.5f, -0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 9, 3);
        Gdx.gl11.glNormal3f(-0.5f, 0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 12, 3);
        Gdx.gl11.glNormal3f(0.5f, 0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 15, 3);
        Gdx.gl11.glNormal3f(-0.5f, -0.25f, 0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 18, 3);
        Gdx.gl11.glNormal3f(0.5f, -0.25f, -0.5f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLES, 21, 3);

        Gdx.gl11.glPopMatrix();

        Gdx.gl11.glDisable(GL11.GL_TEXTURE_2D);
        Gdx.gl11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
    }
}
