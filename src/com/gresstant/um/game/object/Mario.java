package com.gresstant.um.game.object;

import com.gresstant.um.game.Context;

import java.awt.image.BufferedImage;

public class Mario extends EntityAdapter {
    private Context context;
    private EntityState state = EntityState.FROZEN;

    public double speedX = 0.0;
    public double accX = 0.0;

    /**
     * 是否面向右边
     */
    public boolean faceRight = true;
    /**
     * 是否正在转身
     */
    public boolean turning = false;
    /**
     * 是否在跑。
     * 针对此变量的一切赋值操作应当交由 GamePanel 完成。
     */
    public boolean run = false;

    public Mario(Context context, double x, double y) {
        this.context = context;
        this.x = x;
        this.y = y;
        width = 16.0;
        height = 16.0;
        horzAlign = HorzAlign.CENTER;
        vertAlign = VertAlign.BOTTOM;
    }

    private String resBufferKey = "";
    private BufferedImage[] resBufferImg;
    private BufferedImage[] resBufferFlip;
    @SuppressWarnings("null")
    private BufferedImage[] getResource(String key, boolean flip) {
        if (key.equals(resBufferKey)) {
            return flip ? resBufferFlip : resBufferImg;
        } else {
            BufferedImage[] got = context.imgRes.getResource(key);
            if (got == null) return null;
            resBufferKey = key;
            resBufferImg = got;
            resBufferFlip = new BufferedImage[resBufferImg.length];
            for (int i = 0; i < resBufferImg.length; i++)
                resBufferFlip[i] = Utilities.createHorzFlipped(resBufferImg[i]);
            return flip ? resBufferFlip : resBufferImg;
        }
    }

    @Override public BufferedImage getImage() {
        if (state == EntityState.FROZEN || state == EntityState.STAND) {
            return getResource("MARIO$SMALL$STAND", !faceRight)[0];
        } else if (state == EntityState.RUN) {
            if (turning) return getResource("MARIO$SMALL$TURN", !faceRight)[0];
            return getResource("MARIO$SMALL$WALK", !faceRight)[(int) x / 2 % 4];
        } else if (state == EntityState.JUMP) {
            return getResource("MARIO$SMALL$JUMP", !faceRight)[0];
        } else {
            throw new RuntimeException();
        }
    }

    public void tick(int ms) {
        // V=V0+at
        // s=V0t+(at^2)/2
        double second = ms / 1000.0;

        // 根据加速度调整速度和位置
        if (Math.abs(speedX) < context.marioMaxWalkSpeed) {
            x += speedX * second + accX * second * second / 2.0;
            speedX += accX * second;
        } else if (Math.signum(speedX) != Math.signum(accX)) {
            // 四倍加速度
            x += speedX * second + 2 * accX * second * second;
            speedX += 4 * accX * second;
        } else {
            x += speedX * second;
            speedX = Math.signum(speedX) * context.marioMaxWalkSpeed;
        }

        // 摩擦力
        if (state == EntityState.RUN) {
            accX = -Math.signum(speedX) * context.marioFraction; // TODO 这里以后应当乘以一个 ratio
            if (Math.abs(speedX) - Math.abs(accX * second) < 0.0) {
                accX = 0.0;
                speedX = 0.0;
            }
        } else {
            // 飞行或站立时没有摩擦
            accX = 0.0;
        }
        state = speedX == 0.0 ? EntityState.STAND : EntityState.RUN;
    }

    public void acclerate(double ratio) {
        accX = ratio * context.marioAcclerate;
        if (accX > +0.0)
            faceRight = true;
        else if (accX < -0.0)
            faceRight = false;
        turning = Math.signum(speedX) != Math.signum(accX);
    }

    @Override public void setState(EntityState state) {
        this.state = state;
    }

    @Override public EntityState getState() {
        return state;
    }
}
