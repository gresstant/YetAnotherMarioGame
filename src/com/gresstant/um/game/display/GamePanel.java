package com.gresstant.um.game.display;

import com.gresstant.um.game.Context;
import com.gresstant.um.game.object.IEntity;
import com.gresstant.um.game.object.Mario;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class GamePanel extends JPanel {
    /**
     * 指示当前游戏状态。
     * 与其它变量有关联，务必使用 getter 和 setter。
     */
    private GameState state = GameState.CREATED;
    /**
     * 指示切换到当前 state 后，目前是第几个 frame。
     * 不保证一定更新，但至少会从0更新到1。
     * 使用 int 已经可以保证程序稳定运行497天。
     */
    private int frameElapsed = 0;
    /**
     * 程序的上下文设置（不包含关卡相关的信息）
     */
    private final Context context;
    /**
     * 图像资源库。完成异步加载功能后应当删除此变量。
     */
    private Resource<BufferedImage> res;
    /**
     * 从上级传来的按键列表
     * TODO 存在严重BUG，必须弃用
     * @link pressedKeys
     */
    @Deprecated public Queue<KeyEvent> keyQueue = new LinkedList<>();
    /**
     *
     */
    public boolean[] pressedKeys = new boolean[500];

    /*
     * 关卡相关
     */

    /**
     * 舞台外、左侧的实体
     * 内存占用过多时，可以考虑删除这些实体
     * 但是如果没实力的话还是不要这样干比较好
     */
    public LinkedList<IEntity> pastEntities;
    /**
     * 舞台内的实体
     * 这部分实体应当正常更新
     */
    public LinkedList<IEntity> onStageEntities;
    /**
     * 舞台外、右侧的实体
     * 这部分实体应当处于冻结状态，不予更新
     */
    public LinkedList<IEntity> comingEntities;
    public Mario player;

    public GamePanel(Context context, Resource<BufferedImage> res) {
        this.context = context;
        this.res = res; // 在进行异步加载部分的开发之前，暂时保留这个参数
        setPreferredSize(new Dimension(800, 600));
    }

    /**
     * 开始游戏主循环
     */
    public void start() {
        System.out.println("started!");
        long beginTimestamp, lasting;

        BufferedImage screenBuffer = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = screenBuffer.createGraphics();
        g.setBackground(Color.WHITE);
        Graphics2D gLog = screenBuffer.createGraphics();
        gLog.setColor(Color.RED);

        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File("res\\whitek.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // cache 用于储存不用每次重绘的画面，需要在第一帧时初始化
        BufferedImage[] cache = null;

        // 游戏主循环
        mainLoop: while (true) {
            beginTimestamp = System.currentTimeMillis(); // 记录本轮循环开始时的时间戳
            switch (state) {
                case EXITING: {
                    if (context.exitCallback != null)
                        context.exitCallback.run();
                    break mainLoop;
                }
                case CREATED: {
                    setState(GameState.LOGO_SPLASH);
                    break;
                }
                case LOGO_SPLASH: {
                    // 感觉这一部分可以包装成一个方法
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.WHITE);
                    }

                    BufferedImage splash = bi;
                    int timeElapsed = frameElapsed * context.TARGET_TPF; // 单位为毫秒
                    g.clearRect(0, 0, getWidth(), getHeight());
                    if (timeElapsed < 1250) { // 0.0s - 1.25s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, timeElapsed / 1000.0f / 1.25f));
                    } else if (timeElapsed < 1750) { // 1.25s - 1.75s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    } else if (timeElapsed < 3000) { // 1.75s - 3s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (timeElapsed / 1000.0f - 1.75f) / 1.25f));
                    } else {
                        if (!context.imgResFuture.isDone()) break;
                        try {
                            context.imgRes = context.imgResFuture.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                        setState(GameState.START_SCREEN);
                        break;
                    }
                    g.drawImage(splash, 0, 0, null);
                    frameElapsed++;
                    break;
                }
                case START_SCREEN: {
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.WHITE);
                        keyQueue.clear();
                        frameElapsed++;
                    }

                    g.clearRect(0, 0, getWidth(), getHeight());
                    //updateGame();
                    g.setColor(Color.BLACK);
                    while (!keyQueue.isEmpty()) {
                        if (keyQueue.poll().getKeyCode() == KeyEvent.VK_ENTER) {
                            // TODO 这里需要初始化游戏
                            setState(GameState.LIFE_SPLASH);
                        }
                    }
                    g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 48));
                    g.drawString("Press Enter to start!", 0, 600);
                    break;
                }
                case LIFE_SPLASH: {
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.BLACK);
                        g.clearRect(0, 0, getWidth(), getHeight());

                        BufferedImage small = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D gs = small.createGraphics();
                        gs.drawImage(res.getResource("MARIO$SMALL$STAND")[0], 160, 150 - 8, null);
                        gs.setFont(new Font(g.getFont().getName(), Font.PLAIN, 16));
                        gs.drawString("X", 190, 150 + 7);
                        gs.drawString("3", 215, 150 + 7);

                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g.drawImage(small, 0, 0, 800, 600, null);
                    }

                    int timeElapsed = frameElapsed * context.TARGET_TPF; // 单位为毫秒
                    if (timeElapsed < 1000) { // 0s - 1s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    } else {
                        // TODO 需要设定游戏数据到上一个 checkpoint
                        player = new Mario(context, 100, 100);
                        setState(GameState.IN_GAME);
                        break;
                    }
                    //g.drawImage(splash, 0, 0, null);
                    frameElapsed++;
                    break;
                }
                case IN_GAME: {
                    if (frameElapsed == 0) {
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.WHITE);
                    }
                    g.clearRect(0, 0, getWidth(), getHeight());
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g.drawImage(updateGame(), 0, 0, 800, 600, null);
                    frameElapsed++;
                    break;
                }
            }

            lasting = System.currentTimeMillis() - beginTimestamp;

            gLog.drawString("FPS: " + String.format("%.1f", 1 / (Math.max(lasting, context.TARGET_TPF) / 1000.0)), 0, 15);
            gLog.drawString("timeElapsed: " + (frameElapsed * context.TARGET_TPF), 0, 30);
            gLog.drawString("sleep: " + (context.TARGET_TPF - lasting), 0, 45);
            getGraphics().drawImage(screenBuffer, 0, 0, null);

            // 进行到这里时可能刚刚进行了状态切换
            // 所以不要把 frameElapsed++ 放到这里

            if (lasting <= context.TARGET_TPF) {
                try {
                    Thread.sleep(context.TARGET_TPF - lasting);
                } catch (Exception ex) {
                    ex.printStackTrace(); // 无视异常
                }
            } else {
                // 电脑太慢了，无法满足帧率设定
            }
        }
        System.out.println("loop ended ...");
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        frameElapsed = 0;
        this.state = state;
    }

    /**
     * 更新游戏，然后画出来
     * @return 返回当前游戏的画面。尺寸应当为400*300。
     */
    private BufferedImage updateGame() {
        BufferedImage output = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        while (!keyQueue.isEmpty()) {
            KeyEvent event = keyQueue.poll();
            switch (event.getKeyCode()) {
                case KeyEvent.VK_RIGHT:
                    player.acclerate(1.0);
                    break;
                case KeyEvent.VK_LEFT:
                    player.acclerate(-1.0);
                    break;
            }
        }
        player.tick(context.TARGET_TPF);
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(player.getImage(), (int) (player.getLeft() + player.getImgOffsetX()), (int) (player.getTop() + player.getImgOffsetY()), null);
        return output;
    }

    @Override public void update(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        paint(buffer.getGraphics());
        g.drawImage(buffer, 0, 0, null);
    }
}
