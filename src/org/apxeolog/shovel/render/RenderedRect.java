package org.apxeolog.shovel.render;

import haven.*;

import javax.media.opengl.GL2;
import java.awt.*;

/**
 * Created by APXEOLOG on 07.12.2015.
 */
public class RenderedRect implements Rendered {
    private VertexBuf.VertexArray verts;
    private VertexBuf.ColorArray cols;
    private Coord topLeft, bottomRight;
    private Color color;

    public RenderedRect(Coord tl, Coord br, Color clr) {
        topLeft = tl;
        bottomRight = br;
        color = clr;
        update();
    }

    private void update() {
        verts = new VertexBuf.VertexArray(Utils.bufcp(new float[] {
                topLeft.x, topLeft.y, 1,
                bottomRight.x, topLeft.y, 1,
                bottomRight.x, bottomRight.y, 1,
                topLeft.x, bottomRight.y, 1,
        }));
        cols = new VertexBuf.ColorArray(Utils.bufcp(new float[] {
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f,
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f,
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f,
                color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f
        }));
    }

    @Override
    public boolean setup(RenderList r) {
        r.prepo(States.ndepthtest);
        r.prepo(last);
        r.prepo(States.vertexcolor);
        return true;
    }

    @Override
    public void draw(GOut g) {
        BGL gl = g.gl;
        g.apply();
        verts.bind(g, false);
        cols.bind(g, false);
        gl.glDrawArrays(GL2.GL_QUADS, 0, 4);
        verts.unbind(g);
        cols.unbind(g);
    }
}
