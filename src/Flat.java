import com.badlogic.gdx.graphics.Texture;
import org.lwjgl.opengl.GL11;

public class Flat {

    float width, length;
    float x, y, z;
    int repeats = 8;

    public Flat(float width, float length, float x, float y, float z, int repeats) {
        this.width = width;
        this.length = length;
        this.x = x;
        this.y = y;
        this.z = z;
        this.repeats = repeats;
    }

    public void render(Texture tex){
        tex.bind();

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(width,0, length);

        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(-width,0, length);

        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(-width,0,-length);

        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(width,0,-length);

        GL11.glEnd();
    }
}