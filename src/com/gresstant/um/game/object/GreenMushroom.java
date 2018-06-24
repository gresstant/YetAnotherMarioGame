package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class GreenMushroom extends EnemyAdapter {
    private BufferedImage imgBuffer;
    private Runnable callback;

    public GreenMushroom(Context context, boolean movable, double x, double y, Runnable callback) {
        super(context, movable ? 16.0 : 0.0);
        imgBuffer = context.imgRes.getResource("MUSHROOM$GREEN$WALK")[0];
        this.callback = callback;

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
            marioCollide();
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide();
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide();
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario)
            marioCollide();
        return false;
    }

    private void marioCollide() {
        context.sePlayer.playOnce(context.midiRes.getResource("1up")[0]);
        callback.run();
        dispose();
    }

    @Override public BufferedImage getImage() {
        if (getState() != EntityState.DISPOSED)
            return imgBuffer;
        return null;
    }
}
