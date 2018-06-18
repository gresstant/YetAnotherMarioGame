package com.gresstant.um.game;

import com.gresstant.um.game.display.Resource;
import com.gresstant.um.game.midi.MIDIPlayer;
import org.newdawn.easyogg.OggClip;

import javax.sound.midi.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class Context {
    /**
     * 每帧预期停留时间，target time per frame。
     * 默认50帧/秒
     */
    public final int TARGET_TPF = 1000 / 50;

    /**
     * 重力加速度。
     * 单位为 px/s² (像素每平方秒)
     */
    public final double gravity = 384.0;

    public final double maxFallSpeed = 192.0;

    public Runnable exitCallback;

    /**
     * 获取图像资源库的 Future 对象。
     */
    public Future<Resource<BufferedImage>> imgResFuture;

    /**
     * 图像资源库。在游戏加载完毕后，值不应当为 null
     */
    public Resource<BufferedImage> imgRes = null;

    public Future<Resource<Sequence>> midiResFuture;
    public Resource<Sequence> midiRes = null;
    public MIDIPlayer bgmPlayer;
    public MIDIPlayer sePlayer;
    public Synthesizer midiSynthesizer;

    private List<OggClip> oggClips = new LinkedList<>();
    public Function<String, OggClip> oggPlayer = (oggName) -> {
        try {
            File file = new File("res", oggName);
            FileInputStream fis = new FileInputStream(file);
            OggClip oggClip = new OggClip(fis);
            oggClip.play();

            ListIterator<OggClip> oggIter = oggClips.listIterator();
            while (oggIter.hasNext()) {
                OggClip got = oggIter.next();
                if (got.stopped()) {
                    got.close();
                    oggIter.remove();
                }
            }

            oggClips.add(oggClip);
            return oggClip;
        } catch (Exception ignore) {}
        return null;
    };

    public ExecutorService threadPool = Executors.newFixedThreadPool(16);

    /**
     * 马里奥的加速度
     */
    public double marioAcclerate = 192.0;

    /**
     * 马里奥的走路最快速度
     */
    public double marioMaxWalkSpeed = 96.0;

    /**
     * 马里奥的跑步最快速度
     */
    public double marioMaxRunSpeed = 128.0;

    public double marioJumpSpeed = -128.0;

    /**
     * 马里奥的基准摩擦力
     */
    public double marioFraction = 128.0;

    /**
     * 是否跳过 logo splash
     */
    public boolean skipLogoSplash = true;

    public File mapFile = new File("D:\\Downloads\\Bz162\\Bz162\\A.MAP");
}
