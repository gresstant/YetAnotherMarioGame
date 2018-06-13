package com.gresstant.um.game.display;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Resource<T> implements IResource<T> {
    private String currentStyle;

    private Map<String, T[]> storage = new HashMap<>();

    protected Resource() {}

    public static Resource<BufferedImage> loadImg(File f) throws IOException {
        Resource<BufferedImage> output = new Resource();
        BufferedImage img = ImageIO.read(f);

        // 马里奥（小）
        output.storage.put("MARIO$SMALL$STAND", new BufferedImage[] {img.getSubimage(22, 507, 16, 16)});
        output.storage.put("MARIO$SMALL$OVER", new BufferedImage[] {img.getSubimage(45, 507, 16, 16)});
        output.storage.put("MARIO$SMALL$TURN", new BufferedImage[] {img.getSubimage(66, 508, 16, 16)});
        output.storage.put("MARIO$SMALL$WALK", new BufferedImage[] {img.getSubimage(83, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16),
                img.getSubimage(117, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16)});
        output.storage.put("MARIO$SMALL$JUMP", new BufferedImage[] {img.getSubimage(140, 507, 16, 16)});

        // TODO 马里奥（大）
        output.storage.put("MARIO$LARGE$STAND", new BufferedImage[] {img.getSubimage(22, 507, 16, 16)});
        output.storage.put("MARIO$LARGE$SQUAT", new BufferedImage[] {img.getSubimage(22, 507, 16, 16)});
        output.storage.put("MARIO$LARGE$OVER", new BufferedImage[] {img.getSubimage(45, 507, 16, 16)});
        output.storage.put("MARIO$LARGE$TURN", new BufferedImage[] {img.getSubimage(66, 508, 16, 16)});
        output.storage.put("MARIO$LARGE$WALK", new BufferedImage[] {img.getSubimage(83, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16),
                img.getSubimage(117, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16)});
        output.storage.put("MARIO$LARGE$JUMP", new BufferedImage[] {img.getSubimage(140, 507, 16, 16)});

        // TODO 马里奥（火球）
        output.storage.put("MARIO$FIRE$STAND", new BufferedImage[] {img.getSubimage(22, 507, 16, 16)});
        output.storage.put("MARIO$FIRE$SQUAT", new BufferedImage[] {img.getSubimage(22, 507, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER", new BufferedImage[] {img.getSubimage(45, 507, 16, 16)});
        output.storage.put("MARIO$FIRE$TURN", new BufferedImage[] {img.getSubimage(66, 508, 16, 16)});
        output.storage.put("MARIO$FIRE$WALK", new BufferedImage[] {img.getSubimage(83, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16),
                img.getSubimage(117, 507, 16, 16),
                img.getSubimage(99, 507, 16, 16)});
        output.storage.put("MARIO$FIRE$JUMP", new BufferedImage[] {img.getSubimage(140, 507, 16, 16)});

        // TODO 板栗仔
        // TODO 乌龟（绿）
        // TODO 甲虫
        // TODO 刺猬
        // TODO 炮台
        // TODO 炮弹
        // TODO 各种蘑菇
        // TODO 各种砖块

        return output;
    }

    public static Resource<String> loadStr(File f) throws IOException {
        // TODO 加载字符串的逻辑
        return null;
    }

    @Override public void setStyle(String style) {
        currentStyle = style;
    }

    @Override public String getStyle() {
        return currentStyle;
    }

    @Override public T[] getResource(String key) {
        return storage.get(key);
    }
}
