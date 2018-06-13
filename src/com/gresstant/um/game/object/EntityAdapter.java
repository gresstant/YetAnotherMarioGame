package com.gresstant.um.game.object;

import java.awt.image.BufferedImage;

public abstract class EntityAdapter implements IEntity {
    /**
     * 根据对其方式所设定的对齐点
     */
    protected double x, y;
    /**
     * 宽度、高度
     */
    protected double width, height;
    /**
     * 横向对齐方式
     */
    protected HorzAlign horzAlign;
    /**
     * 纵向对齐方式
     */
    protected VertAlign vertAlign;

    @Override public double getLeft() {
        switch (horzAlign) {
            case LEFT:
                return x;
            case RIGHT:
                return x - width;
            case CENTER:
                return x - width / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public double getRight() {
        switch (horzAlign) {
            case LEFT:
                return x + width;
            case RIGHT:
                return x;
            case CENTER:
                return x + width / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public double getTop() {
        switch (vertAlign) {
            case TOP:
                return y;
            case BOTTOM:
                return y + height;
            case CENTER:
                return y - height / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public double getBottom() {
        switch (vertAlign) {
            case TOP:
                return y - height;
            case BOTTOM:
                return y;
            case CENTER:
                return y + height / 2.0;
        }
        throw new RuntimeException();
    }

    @Override public double getWidth() {
        return width;
    }

    @Override public double getHeight() {
        return height;
    }

    protected double getImgOffsetX(int imgWidth) {
        switch (horzAlign) {
            case LEFT:
                return 0;
            case RIGHT:
                return width - imgWidth;
            case CENTER:
                return (width - imgWidth) / 2.0;
        }
        throw new RuntimeException();
    }

    protected double getImgOffsetY(int imgHeight) {
        switch (vertAlign) {
            case TOP:
                return 0;
            case BOTTOM:
                return height - imgHeight;
            case CENTER:
                return (height - imgHeight) / 2.0;
        }
        throw new RuntimeException();
    }

    /**
     * 默认实现要求实现 getImage()
     * 强烈建议在子类中重写
     */
    @Override public double getImgOffsetX() {
        return getImgOffsetX(getImage().getWidth());
    }

    /**
     * 默认实现要求实现 getImage()
     * 强烈建议在子类中重写
     */
    @Override public double getImgOffsetY() {
        return getImgOffsetY(getImage().getHeight());
    }
}
