package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Bullet extends EnemyTemplate {
    public Bullet(Context context, double x, double y, double speed) {
        super(context, speed);
        height = 16.0;
        width = 16.0;
        imgBuffer = context.imgRes.getResource("BULLET$NORMAL$WALK");
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
        setLeft(x);
        setTop(y);
        _x = x;
        _y = y;
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
        if (getState() == EntityState.DISPOSED)
            return null;
        return imgBuffer[0];
    }

    private double _x, _y;
    @Override public void tick(int ms) {
        double time = ms / 1000.0;
        if (y > 500.0) setState(EntityState.DISPOSED);
        _x += speedX * time;
        x = _x;
        y = _y;
    }
}
