package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.function.*;

public class QuestionBlock extends BlockTemplate {
    private EntityState state = EntityState.FROZEN;
    private BufferedImage[] imgBuffer;
    private Consumer<IEntity> callback;
    private Function<Point2D.Double, IEntity> factory;
    private String soundStr;

    private Context context;
    private boolean anime = false;
    private int animeTimer = 0;

    private boolean plundered = false;

    public QuestionBlock(Context context, int x, int y, Function<Point2D.Double, IEntity> factory,
                         Consumer<IEntity> callback, BufferedImage[] appearance, String soundString) {
        this.context = context;
        this.callback = callback;
        this.factory = factory;
        this.soundStr = soundString;

        horzAlign = HorzAlign.LEFT;
        vertAlign = VertAlign.TOP;
        width = 16.0;
        height = 16.0;
        setLeft(x);
        setTop(y);
        imgBuffer = appearance; // context.imgRes.getResource("QUESTION$NORMAL$STAND")[0];
    }

    @Override public void tick(int ms) {
        if (anime) {
            animeTimer += ms;
            if (animeTimer < 100) {
                imgOffsetAdjustY = -animeTimer / 25.0;
            } else if (animeTimer < 200){
                if (!plundered) {
                    plundered = true;
                    if (soundStr != null) context.oggPlayer.apply(soundStr);
                    imgBuffer = context.imgRes.getResource("QUESTION$NORMAL$WALK");
                    callback.accept(factory.apply(new Point2D.Double(getLeft(), getTop())));
                }
                imgOffsetAdjustY = (animeTimer - 200.0) / 25.0;
            } else {
                imgOffsetAdjustY = 0;
                anime = false;
            }
        }
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        if (!plundered && !anime && entity instanceof Mario) {
            anime = true;
        }
        return true;
    }

    @Override public void die(long timestamp, Runnable callback) {
        dispose();
    }

    @Override public BufferedImage getImage() {
        switch (state) {
            case FROZEN:
            case STILL:
            case DEAD:
                return imgBuffer[0];
            case DISPOSED:
                return null;
            default:
                throw new RuntimeException();
        }
    }
}
