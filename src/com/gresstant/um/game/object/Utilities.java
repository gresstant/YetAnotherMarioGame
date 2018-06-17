package com.gresstant.um.game.object;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Utilities {
    public static boolean intersect(double left1, double right1, double top1, double bottom1,
                                    double left2, double right2, double top2, double bottom2) {
        return Math.abs((left1 + right1) / 2 - (left2 + right2) / 2) < ((right1 + right2 - left1 - left2) / 2) &&
                Math.abs((top1 + bottom1) / 2 - (top2 + bottom2) / 2) < ((bottom1 + bottom2 - top1 - top2) / 2);
    }
    
    /**
     * 判断实体1是否与实体2有重合部分
     */
    public static boolean intersect(IEntity obj1, IEntity obj2) {
        // TODO
        return intersect(obj1.getLeft(), obj1.getRight(), obj1.getTop(), obj1.getBottom(),
                         obj2.getLeft(), obj2.getRight(), obj2.getTop(), obj2.getBottom());
    }

    /**
     * 判断按当前运动速度，下一帧是否会产生碰撞
     * 注意不会判断当前帧是否会碰撞
     * @param displaceX 两物体间横向相对速度，向右为正方向
     * @param displaceY 两物体间纵向相对速度，向下为正方向
     */
    public static boolean collide(IEntity obj1, IEntity obj2, double displaceX, double displaceY) {
        boolean out = intersect(obj1.getLeft() + displaceX, obj1.getRight() + displaceX,
                obj1.getTop() + displaceY, obj1.getBottom() + displaceY,
                obj2.getLeft(), obj2.getRight(), obj2.getTop(), obj2.getBottom());
        if (!out) return false;
        return true;
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
