package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Flower extends EnemyAdapter {
    private BufferedImage imgBuffer;

    public Flower(Context context, double x, double y) {
        super(context, 0.0);
        imgBuffer = context.imgRes.getResource("FLOWER$NORMAL$STILL")[0];

        width = 16.0;
        height = 16.0;
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
            marioCollide((Mario) entity);
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide((Mario) entity);
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide((Mario) entity);
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide((Mario) entity);
        return false;
    }

    private void marioCollide(Mario player) {
        if (player.getGrowth() == Mario.GrowthState.SMALL ||
                player.getGrowth() == Mario.GrowthState.BIG)
            player.setGrowth(Mario.GrowthState.BULLET);
        dispose();
    }

    @Override public BufferedImage getImage() {
        if (getState() != EntityState.DISPOSED)
            return imgBuffer;
        return null;
    }
}
