package com.gresstant.um.game.object;

import java.awt.image.BufferedImage;

public class NullObject extends BackgroundTemplate {
    private EntityState state = EntityState.FROZEN;

    public NullObject() {
        horzAlign = HorzAlign.LEFT;
        vertAlign = VertAlign.TOP;
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
        return null;
    }

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideDownwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideLeftwards(boolean[] keyArray, IEntity entity) {
        return false;
    }

    @Override public boolean collideRightwards(boolean[] keyArray, IEntity entity) {
        return false;
    }
}
