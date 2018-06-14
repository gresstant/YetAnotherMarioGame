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
        output.storage.put("MARIO$LARGE$SQUAT", new BufferedImage[] {img.getSubimage(39, 580, 16, 32)});
        output.storage.put("MARIO$LARGE$TURN", new BufferedImage[] {img.getSubimage(87, 580, 16, 32)});
        output.storage.put("MARIO$LARGE$WALK", new BufferedImage[] {img.getSubimage(107, 579, 16, 32),
                img.getSubimage(129, 578, 16, 32),
                img.getSubimage(155, 578, 16, 32),
                img.getSubimage(129, 578, 16, 32)});
        output.storage.put("MARIO$LARGE$JUMP", new BufferedImage[] {img.getSubimage(183, 577, 16, 32)});

        // TODO 马里奥（火球）
        output.storage.put("MARIO$FIRE$STAND", new BufferedImage[] {img.getSubimage(134, 662, 16, 32)});
        output.storage.put("MARIO$FIRE$SQUAT", new BufferedImage[] {img.getSubimage(111, 662, 16, 32)});
        output.storage.put("MARIO$FIRE$TURN", new BufferedImage[] {img.getSubimage(157, 661, 16, 32)});
        output.storage.put("MARIO$FIRE$WALK", new BufferedImage[] {img.getSubimage(179, 661, 16, 32),
                img.getSubimage(201, 660, 16, 32),
                img.getSubimage(227, 662, 16, 32),
                img.getSubimage(201, 660, 16, 32)});
        output.storage.put("MARIO$FIRE$JUMP", new BufferedImage[] {img.getSubimage(254, 659, 16, 32)});

        // TODO 板栗仔
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(203, 909, 16, 16),
                img.getSubimage(223, 909, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(243, 908, 16, 16)});

        // TODO 乌龟（绿）
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(466, 839, 16, 32),
                img.getSubimage(496, 838, 16, 32)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(525, 838, 16, 16)});
        output.storage.put("MARIO$FIRE$SLIDE",new BufferedImage[] {img.getSubimage(555, 836, 16, 16)});

        // TODO 甲虫
        output.storage.put("MARIO$FIRE$LIFTWORK",new BufferedImage[] {img.getSubimage(32, 992, 16, 16),
                img.getSubimage(53, 992, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(71, 992, 16, 16)});
        output.storage.put("MARIO$FIRE$RIGHTWALK",new BufferedImage[] {img.getSubimage(90, 992, 16, 16),
                img.getSubimage(111, 992, 16, 16)});

        // TODO 刺猬
        output.storage.put("MARIO$FIRE$WALK",new BufferedImage[] {img.getSubimage(467, 777, 16, 16),
                img.getSubimage(488, 777, 16, 16)});
        output.storage.put("MARIO$FIRE$OVER",new BufferedImage[] {img.getSubimage(463, 758, 16, 16),
                img.getSubimage(480, 759, 16, 16)});

        // TODO 炮台
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(611, 830, 16, 32)});

        // TODO 炮弹
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(593, 814, 16, 32)});

        // TODO 各种蘑菇
        output.storage.put("MARIO$FIRE$GREEN",new BufferedImage[] {img.getSubimage(66, 58, 16, 16)});
        output.storage.put("MARIO$FIRE$RED",new BufferedImage[] {img.getSubimage(86, 58, 16, 16)});
        output.storage.put("MARIO$FIRE$BLUE",new BufferedImage[] {img.getSubimage(113, 58, 16, 16)});

        // TODO 各种砖块
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(389, 63, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(388, 80, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(388, 99, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(389, 118, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(388, 139, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(388, 157, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(387, 175, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(405, 175, 16, 16)});
        output.storage.put("MARIO$FIRE$TODO",new BufferedImage[] {img.getSubimage(423, 175, 16, 16)});

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
