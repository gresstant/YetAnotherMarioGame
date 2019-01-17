package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Goomba extends EnemyTemplate {
    public Goomba(Context context, double x, double y) {
        super(context, 16.0);
        height = 16.0;
        width = 16.0;
        imgBuffer = context.imgRes.getResource("GOOMBA$NORMAL$WALK");
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
        setLeft(x);
        setTop(y);
    }

    @Override public void activate() {
        setState(EntityState.RUN);
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
    }

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            ((Mario) entity).hurt();
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario) {
            context.oggPlayer.apply("humi.ogg");
            die(0, null);
            ((Mario) entity).bottomSupported = true;
            ((Mario) entity).tinyJump(System.currentTimeMillis());
        }
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario) {
            ((Mario) entity).hurt();
        }
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario) {
            ((Mario) entity).hurt();
        }
        return false;
    }

    private BufferedImage[] imgBuffer;
    @Override public BufferedImage getImage() {
        switch (getState()) {
            case DISPOSED:
                return null;
            case STILL:
            case DEAD:
            case STAND:
                return context.imgRes.getResource("GOOMBA$NORMAL$OVER")[0];
            case FROZEN:
            case RUN:
            case JUMP:
                return imgBuffer[Math.abs((int) x / 4 % 2)];
        }
        return null;
    }
}
