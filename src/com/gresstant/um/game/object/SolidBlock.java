package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SolidBlock extends BlockTemplate {
    private EntityState state = EntityState.FROZEN;
    private BufferedImage imgBuffer;

    public SolidBlock(Context context, double x, double y, int w, int h) {
        horzAlign = HorzAlign.LEFT;
        vertAlign = VertAlign.TOP;
        width = w * 16.0;
        height = h * 16.0;
        setLeft(x);
        setTop(y);

        BufferedImage single = context.imgRes.getResource("GRID$NORMAL$STAND")[0];
        imgBuffer = new BufferedImage(w * 16, h * 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imgBuffer.createGraphics();
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                g.drawImage(single, i * 16, j * 16, null);
            }
        }
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
    }

    @Override public BufferedImage getImage() {
        if (getState() != EntityState.DISPOSED)
            return imgBuffer;
        return null;
    }
}
