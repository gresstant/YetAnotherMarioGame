package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

public abstract class EnemyAdapter extends EntityAdapter implements IEnemy {
    protected EntityState state = EntityState.FROZEN;

    protected boolean topSupported = false;
    protected boolean bottomSupported = false;
    protected boolean leftSupported = false;
    protected boolean rightSupported = false;

    protected double speedX, speedY;
    protected Context context;

    protected EnemyAdapter(Context context, double speedX) {
        this.context = context;
        this.speedX = -Math.abs(speedX);
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public void barrierBottom() {
        bottomSupported = true;
    }

    @Override public void barrierTop() {
        topSupported = true;
    }

    @Override public void barrierLeft() {
        leftSupported = true;
    }

    @Override public void barrierRight() {
        rightSupported = true;
    }

    @Override public void tick(int ms) {
        double time = ms / 1000.0;

        if (speedX < 0.0 && leftSupported || speedX > 0.0 && rightSupported)
            speedX = -speedX;
        if (topSupported)
            speedY = Math.min(Math.abs(speedY), context.maxFallSpeed);
        if (bottomSupported) {
            speedY = 0;
        } else if (speedY < context.maxFallSpeed) {
            speedY += context.gravity * time;
        }

        x += speedX * time;
        y += speedY * time;

        topSupported = false;
        bottomSupported = false;
        leftSupported = false;
        rightSupported = false;

        if (y > 500.0) setState(EntityState.DISPOSED);
    }

    @Override public double getSpeedX() {
        return speedX;
    }

    @Override public double getSpeedY() {
        return speedY;
    }
}
