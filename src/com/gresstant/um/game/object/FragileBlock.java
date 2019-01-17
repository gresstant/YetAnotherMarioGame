package com.gresstant.um.game.object;

// TODO 这只是个临时的实现

import com.gresstant.um.game.Context;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FragileBlock extends BlockTemplate {
    private Context context;
    private EntityState state = EntityState.FROZEN;
    /**
     * 0 for no animation
     * 1 for up animation
     * 2 for die animation
     */
    int anime = 0, animeTimer = 0;

    public FragileBlock(Context context, double x, double y) {
        this.context = context;

        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.TOP;
        height = 16.0;
        width = 16.0;

        setLeft(x);
        setTop(y);

        imgBuffer = context.imgRes.getResource("PALEBLOCK$NORMAL$STAND")[0];
    }

    @Override public EntityState getState() {
        return state;
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    protected BufferedImage imgBuffer;
    @Override public BufferedImage getImage() {
        switch (getState()) {
            case FROZEN:
            case STILL:
                return imgBuffer;
            case DISPOSED:
                return null;
            case DEAD:
                return getDyingImage();
            default:
                throw new RuntimeException();
        }
    }

    private BufferedImage[] dieImgBuffer;
    private BufferedImage getDyingImage() {
        if (anime != 2) return null;
        if (animeTimer > 1000) { // 动画总计1秒
            setAnimation(0);
            setState(EntityState.DISPOSED);
            System.out.println("DISPOSE");
            return getImage();
        }
        if (dieImgBuffer == null) {
            dieImgBuffer = new BufferedImage[4];
            dieImgBuffer[0] = imgBuffer.getSubimage(0, 0, (int) getWidth() / 2, (int) getHeight() / 2);
            dieImgBuffer[1] = imgBuffer.getSubimage((int) getWidth() / 2, 0, (int) getWidth() / 2, (int) getHeight() / 2);
            dieImgBuffer[2] = imgBuffer.getSubimage(0, (int) getHeight() / 2, (int) getWidth() / 2, (int) getHeight() / 2);
            dieImgBuffer[3] = imgBuffer.getSubimage((int) getWidth() / 2, (int) getHeight() / 2, (int) getWidth() / 2, (int) getHeight() / 2);
        }
        double progress = animeTimer / 1000.0;

        double toAX = -progress * 16.0;
        double toAY = context.marioJumpSpeed * progress + context.gravity * progress * progress / 2.0;
        double toBX = -toAX;
        double toBY = toAY;
        double toCX = toAX * 1.1;
        double toCY = context.marioJumpSpeed * progress*1.1 + context.gravity * progress * progress*1.21 / 2.0;
        double toDX = -toCX;
        double toDY = toCY;

        double tX = -Math.min(toAX, Math.min(toBX, Math.min(toCX, toDX)));
        double tY = -Math.min(toAY, toCY);
        double lenY = Math.abs(toAY - toCY)/*-Math.min(toAY, Math.min(toBY, Math.min(toCY, toDY)))*/;

        imgOffsetAdjustY = -tY;

        double oriAX = tX;
        double oriAY = tY;
        double oriBX = tX + getWidth() / 2;
        double oriBY = tY;
        double oriCX = tX;
        double oriCY = tY + getHeight() / 2;
        double oriDX = tX + getWidth() / 2;
        double oriDY = tY + getHeight() / 2;

        double translateAX = oriAX + toAX;
        double translateAY = oriAY + toAY;
        double translateBX = oriBX + toBX;
        double translateBY = oriBY + toBY;
        double translateCX = oriCX + toCX;
        double translateCY = oriCY + toCY;
        double translateDX = oriDX + toDX;
        double translateDY = oriDY + toDY;

        BufferedImage output = new BufferedImage((int) tX * 2 + (int) getWidth(), (int) lenY + (int) getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();

        AffineTransform a = new AffineTransform();
        a.rotate(-Math.PI * (progress / 4.0), translateAX, translateAY); // 旋转角度以弧度为单位
        a.translate(translateAX, translateAY);

        AffineTransform b = new AffineTransform();
        b.rotate(Math.PI * (progress / 4.0), translateBX, translateBY); // 旋转角度以弧度为单位
        b.translate(translateBX, translateBY);

        AffineTransform c = new AffineTransform();
        c.rotate(-Math.PI * (progress*1.1 / 4.0), translateCX, translateCY); // 旋转角度以弧度为单位
        c.translate(translateCX, translateCY);

        AffineTransform d = new AffineTransform();
        d.rotate(Math.PI * (progress*1.1 / 4.0), translateDX, translateDY); // 旋转角度以弧度为单位
        d.translate(translateDX, translateDY);

        g.drawImage(dieImgBuffer[0], a, null);
        g.drawImage(dieImgBuffer[1], b, null);
        g.drawImage(dieImgBuffer[2], c, null);
        g.drawImage(dieImgBuffer[3], d, null);

        return output;
    }

    @Override public boolean collideUpwards(boolean[] keyArray, IEntity entity) {
        if (entity instanceof Mario) {
            if (((Mario) entity).getGrowth() == Mario.GrowthState.SMALL) {
                setAnimation(1);
            } else {
                context.oggPlayer.apply("brockbreak.ogg");
                die(System.currentTimeMillis(), null);
            }
        }
        return true;
    }

    @Override public void die(long timestamp, Runnable callback) {
        setAnimation(2);
        setState(EntityState.DEAD);
        System.out.println("DYING");
    }

    @Override public void tick(int ms) {
        if (anime != 0) animeTimer += ms;
        if (anime == 1) {
            if (animeTimer < 100) {
                imgOffsetAdjustY = -animeTimer / 25.0;
            } else if (animeTimer < 200){
                imgOffsetAdjustY = (animeTimer - 200.0) / 25.0;
            } else {
                imgOffsetAdjustY = 0;
                setAnimation(0);
            }
        }
    }

    private void setAnimation(int val) {
        System.out.println("SET TO " + val);
        if (anime == val) return;
        anime = val;
        animeTimer = 0;
    }
}
