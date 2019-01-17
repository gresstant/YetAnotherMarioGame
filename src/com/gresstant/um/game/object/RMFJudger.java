package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 这个类用于辅助判断应该添加红蘑菇还是花
 *
 * 其实感觉还可以进一步抽象
 * 但是现在没有这个需求所以还是这样吧
 */
public class RMFJudger extends EnemyTemplate {
    Supplier<Mario> marioSupplier;
    Consumer<IEntity> addEntityLater;

    public RMFJudger(Context context, double x, double y, Supplier<Mario> marioSupplier, Consumer<IEntity> addEntityLater) {
        super(context, 0);

        this.marioSupplier = marioSupplier;
        this.addEntityLater = addEntityLater;

        width = 16.0;
        height = 16.0;

        horzAlign = HorzAlign.LEFT;
        vertAlign = VertAlign.TOP;
        setLeft(x);
        setTop(y);
    }

    @Override public void activate() {
        Mario mario = marioSupplier.get();

        switch (mario.getGrowth()) {
            case SMALL:
                addEntityLater.accept(new RedMushroom(context, true, getLeft(), getTop()));
                break;
            default:
                addEntityLater.accept(new Flower(context, getLeft(), getTop()));
                break;
        }

        dispose();
    }

    @Override public void dispose() {
        setState(EntityState.DISPOSED);
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
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

    @Override public BufferedImage getImage() {
        return null;
    }
}
