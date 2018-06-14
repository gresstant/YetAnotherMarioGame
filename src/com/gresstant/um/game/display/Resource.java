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
        output.storage.put("MARIO$LARGE$STAND", new BufferedImage[] {img.getSubimage(48, 548, 16, 32)});
        output.storage.put("MARIO$LARGE$SQUAT", new BufferedImage[] {img.getSubimage(23, 548, 16, 32)});
        output.storage.put("MARIO$LARGE$TURN", new BufferedImage[] {img.getSubimage(71, 548, 16, 32)});
        output.storage.put("MARIO$LARGE$WALK", new BufferedImage[] {img.getSubimage(91, 547, 16, 32),
                img.getSubimage(113, 546, 16, 32),
                img.getSubimage(139, 546, 16, 32),
                img.getSubimage(113, 546, 16, 32)});
        output.storage.put("MARIO$LARGE$JUMP", new BufferedImage[] {img.getSubimage(167, 545, 16, 32)});

        // TODO 马里奥（火球）
        output.storage.put("MARIO$FIRE$STAND", new BufferedImage[] {img.getSubimage(118, 630, 16, 32)});
        output.storage.put("MARIO$FIRE$SQUAT", new BufferedImage[] {img.getSubimage(95, 630, 16, 32)});
        output.storage.put("MARIO$FIRE$TURN", new BufferedImage[] {img.getSubimage(141, 629, 16, 32)});
        output.storage.put("MARIO$FIRE$WALK", new BufferedImage[] {img.getSubimage(163, 629, 16, 32),
                img.getSubimage(185, 628, 16, 32),
                img.getSubimage(211, 630, 16, 32),
                img.getSubimage(185, 628, 16, 32)});
        output.storage.put("MARIO$FIRE$JUMP", new BufferedImage[] {img.getSubimage(238, 627, 16, 32)});

        // TODO 板栗仔
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(187, 893, 16, 16),
                img.getSubimage(207, 893, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(227, 898, 16, 16)});

        // TODO 乌龟（绿）
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(450, 807, 16, 32),
                img.getSubimage(496, 838, 16, 32)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(509, 822, 16, 16)});
        output.storage.put("MARIO$FIRE$SLIDE",new BufferedImage[] {img.getSubimage(539, 820, 16, 16)});

        // TODO 甲虫
        output.storage.put("MARIO$FIRE$LIFTWORK",new BufferedImage[] {img.getSubimage(16, 976, 16, 16),
                img.getSubimage(53, 992, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(55, 976, 16, 16)});
        output.storage.put("MARIO$FIRE$RIGHTWALK",new BufferedImage[] {img.getSubimage(74, 976, 16, 16),
                img.getSubimage(95, 976, 16, 16)});

        // TODO 刺猬
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(451, 762, 16, 16),
                img.getSubimage(472, 762, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(447, 742, 16, 16),
                img.getSubimage(464, 743, 16, 16)});

        // TODO 炮台
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(595, 798, 16, 32)});

        // TODO 炮弹
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(577, 782, 16, 32)});

        // TODO 各种蘑菇
        output.storage.put("MARIO$FIRE$GREEN",new BufferedImage[] {img.getSubimage(50, 42, 16, 16)});
        output.storage.put("MARIO$FIRE$RED",new BufferedImage[] {img.getSubimage(70, 42, 16, 16)});
        output.storage.put("MARIO$FIRE$BLUE",new BufferedImage[] {img.getSubimage(97, 42, 16, 16)});

        // TODO 各种砖块
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(373, 47, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(372, 64, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(372, 83, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(373, 102, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(372, 123, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(372, 141, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(371, 159, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(389, 159, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(407, 159, 16, 16)});

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
