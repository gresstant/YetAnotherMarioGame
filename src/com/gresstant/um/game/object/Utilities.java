package com.gresstant.um.game.object;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Utilities {
    /**
     * 判断实体1是否与实体2有重合部分
     */
    public static boolean intersect(IEntity obj1, IEntity obj2) {
        // TODO
        if (obj1.getRight() >= obj2.getLeft() || obj1.getLeft() <= obj2.getRight())
            if (obj1.getTop() <= obj2.getBottom() || obj1.getBottom() >= obj2.getTop())
                return true;
        if (obj1.getTop() <= obj2.getBottom() || obj1.getBottom() >= obj2.getTop())
            if (obj1.getLeft() >= obj2.getRight() || obj1.getRight() <= obj2.getLeft())
                return true;
        return false;
    }

    /**
     * 判断实体1是否与实体2相邻
     */
    public static boolean adjacent(IEntity obj1, IEntity obj2) {
        if (obj1.getRight() == obj2.getLeft() - 1 || obj1.getLeft() == obj2.getRight() + 1)
            if (obj1.getTop() <= obj2.getBottom() + 1 || obj1.getBottom() >= obj2.getTop() - 1)
                return true;
        if (obj1.getTop() == obj2.getBottom() + 1 || obj1.getBottom() == obj2.getTop() - 1)
            if (obj1.getLeft() <= obj2.getRight() - 1 || obj1.getRight() >= obj2.getLeft() - 1)
                return true;
        return false;
    }

    /**
     * 判断按当前运动速度，下一帧是否会产生碰撞
     * @param speedX 两物体间横向相对速度，向右为正方向
     * @param speedY 两物体间纵向相对速度，向下为正方向
     */
    public static boolean collide(IEntity obj1, IEntity obj2, double speedX, double speedY) {
        if (intersect(obj1, obj2))
            return true;
        if (intersect(new EntityHelper(obj1, speedX, speedY), obj2))
            return true;
        return false;
    }

    /**
     * 只用于协助判断 collide
     */
    private static class EntityHelper implements IEntity {
        double left, right, top, bottom;

        EntityHelper(double left, double right, double top, double bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        EntityHelper(IEntity src, double speedX, double speedY) {
            this(src.getLeft() + speedX,
                    src.getRight() + speedX,
                    src.getTop() + speedY,
                    src.getBottom() + speedY);
        }

        @Override public double getLeft() {
            return left;
        }

        @Override public double getRight() {
            return right;
        }

        @Override public double getTop() {
            return top;
        }

        @Override public double getBottom() {
            return bottom;
        }

        @Override public double getWidth() {
            return right - left;
        }

        @Override public double getHeight() {
            return bottom - top;
        }

        @Override public BufferedImage getImage() {
            return null;
        }

        @Override public double getImgOffsetX() {
            return 0;
        }

        @Override public double getImgOffsetY() {
            return 0;
        }

        @Override public EntityState getState() {
            return null;
        }

        @Override public void setState(EntityState state) {

        }
    }

    public static BufferedImage createVertFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
        return createTransformed(image, at);
    }

    public static BufferedImage createHorzFlipped(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(-1, 1));
        at.concatenate(AffineTransform.getTranslateInstance(-image.getWidth(), 0));
        return createTransformed(image, at);
    }

    private static BufferedImage createTransformed(BufferedImage image, AffineTransform at) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.transform(at);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}
