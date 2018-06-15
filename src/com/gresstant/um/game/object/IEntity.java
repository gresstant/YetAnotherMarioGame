package com.gresstant.um.game.object;

import java.awt.image.BufferedImage;

public interface IEntity {
    double getLeft();
    double getRight();
    double getTop();
    double getBottom();
    void setLeft(double val);
    void setRight(double val);
    void setTop(double val);
    void setBottom(double val);
    double getWidth();
    double getHeight();
    EntityState getState();
    void setState(EntityState state);
    void activate();
    void dispose();

    BufferedImage getImage();

    /**
     * 绘制图像时，左上角的横坐标为 getLeft() + getImgOffsetX()
     */
    double getImgOffsetX();

    /**
     * 绘制图像时，左上角的纵坐标为 getTop() + getImgOffsetY()
     */
    double getImgOffsetY();
}
