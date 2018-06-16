package com.gresstant.um.game.display;

import com.gresstant.um.game.Context;
import com.gresstant.um.game.object.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
     * 按键检测相关。
     * 由父级写入数据。
     */
    public boolean[] pressedKeys = new boolean[500];
    /**
     * 游戏是否处于暂停状态
     */
    public boolean paused = false;

    /*
     * 关卡相关
     */

    /**
     * 舞台外、左侧的实体
     * 内存占用过多时，可以考虑删除这些实体
     * 但是如果没实力的话还是不要这样干比较好
     */
    public LinkedList<IEntity> pastEntities = new LinkedList<>();
    /**
     * 舞台内的实体
     * 这部分实体应当正常更新
     */
    public LinkedList<IEntity> onStageEntities = new LinkedList<>();
    /**
     * 舞台外、右侧的实体
     * 这部分实体应当处于冻结状态，不予更新
     */
    public LinkedList<IEntity> comingEntities = new LinkedList<>();
    /**
     * 当前舞台的最左侧
     */
    public int stageX = 0;
    public Mario player;
    public int playerLife = 3;

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
            if (paused) {
                try { Thread.sleep(100); } catch (Exception ignore) {}
                continue mainLoop;
            }
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
                        if (context.skipLogoSplash)
                            frameElapsed += 100000;
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
                    if (state == GameState.LOGO_SPLASH) frameElapsed++;
                    break;
                }
                case START_SCREEN: {
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.WHITE);
                        frameElapsed++;
                    }

                    g.clearRect(0, 0, getWidth(), getHeight());
                    //updateGame();
                    g.setColor(Color.BLACK);
                    if (pressedKeys[KeyEvent.VK_ENTER]) {
                        // TODO 这里需要初始化游戏
                        setState(GameState.LIFE_SPLASH);
                    }
                    g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 48));
                    g.drawString("Press Enter to start!", 0, 600);
                    break;
                }
                case LIFE_SPLASH: {
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        System.out.println("LIFE INIT");
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.BLACK);
                        g.clearRect(0, 0, getWidth(), getHeight());

                        BufferedImage small = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D gs = small.createGraphics();
                        gs.drawImage(res.getResource("MARIO$SMALL$STAND")[0], 160, 150 - 8, null);
                        gs.setFont(new Font(g.getFont().getName(), Font.PLAIN, 16));
                        gs.drawString("X", 190, 150 + 7);
                        gs.drawString(String.valueOf(playerLife), 215, 150 + 7);

                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g.drawImage(small, 0, 0, 800, 600, null);
                    }
                    System.out.println("LIFE " + frameElapsed);

                    int timeElapsed = frameElapsed * context.TARGET_TPF; // 单位为毫秒
                    if (timeElapsed < 1000) { // 0s - 1s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    } else {
                        // TODO 需要设定游戏数据到上一个 checkpoint
                        onStageEntities.clear();
                        onStageEntities.add(new Block(context, 100, 100));
                        onStageEntities.add(new Block(context, 200, 60));
                        onStageEntities.add(new Block(context, 216, 60));
                        onStageEntities.add(new Block(context, 250, 80));
                        onStageEntities.add(new Block(context, 184, 150));
                        onStageEntities.add(new Block(context, 200, 150));
                        onStageEntities.add(new Block(context, 216, 150));
                        onStageEntities.add(new Block(context, 232, 150));
                        onStageEntities.add(new Block(context, 248, 150));
                        onStageEntities.add(new Block(context, 264, 150));
                        player = new Mario(context, 100, 99);
                        player.activate();
                        player.setGrowth(Mario.GrowthState.BIG);
                        setState(GameState.IN_GAME);
                        break;
                    }
                    //g.drawImage(splash, 0, 0, null);
                    if (state == GameState.LIFE_SPLASH) frameElapsed++;
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
                    if (state == GameState.IN_GAME) frameElapsed++;
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
        System.out.println("change to " + state);
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
        g.clearRect(0, 0, getWidth(), getHeight());

        List<Runnable> invokeLater = new LinkedList<>();
        long timestamp = System.currentTimeMillis();


        if (player.getState() != EntityState.DEAD && player.getState() != EntityState.DISPOSED) {
            // 死亡判定 + 检查自杀按键
            if (player.getTop() > 200.0 || pressedKeys[KeyEvent.VK_O]) {
                player.die(timestamp, () -> {
                    playerLife--;
                    setState(GameState.LIFE_SPLASH);
                });
            }
            // 检查移动+跳跃按键
            if (pressedKeys[KeyEvent.VK_RIGHT])
                player.accelerate(1.0);
            if (pressedKeys[KeyEvent.VK_LEFT])
                player.accelerate(-1.0);
            if (pressedKeys[KeyEvent.VK_Z])
                invokeLater.add(() -> player.tryJump(timestamp));

            player.run = pressedKeys[KeyEvent.VK_X];
        }

        // 计算出本帧玩家的大致位置
        double displaceX = player.speedX * context.TARGET_TPF / 1000.0;
        double displaceY = player.speedY * context.TARGET_TPF / 1000.0;
        double nextVC = player.getVertCenter() + displaceX;
        double nextHC = player.getHorzCenter() + displaceY;
        double playerRatio = player.getHeight() / player.getWidth();

        // 真·碰撞检测，顺便把实体画出来
        for (IEntity entity : onStageEntities) {
            EntityState playerState = player.getState();
            EntityState entityState = entity.getState();
            if (!(playerState == EntityState.DEAD || playerState == EntityState.DISPOSED) &&
                    !(entityState == EntityState.FROZEN || entityState == EntityState.DISPOSED || entityState == EntityState.DEAD) &&
                    Utilities.collide(player, entity, player.speedX, player.speedY, context.TARGET_TPF)) {
                // 后面要用很多遍，所以先放到这里
                double eL = entity.getLeft(), eR = entity.getRight();
                double eT = entity.getTop(), eB = entity.getBottom();
                Direction direction; // 0 for ->, 1 for <-, 2 for ^, 3 for v, 4 for unknown
                //region detect direction
                //  **********
                //  *1\* 2*/3*
                //  **********
                //  *4 * 5* 6* (5 will be ignored)
                //  **********
                //  *7/* 8*\9*
                //  **********
                if (eT - nextHC > 0) { // 1, 2, 3
                    if (eL - nextVC > 0) { // 1
                        // ****
                        // *\v*
                        // *>\*
                        // ****
                        if ((eT - nextHC) / (eL - nextVC) < playerRatio) {
                            direction = Direction.RIGHTWARDS; // >
                        } else {
                            direction = Direction.DOWNWARDS; // v
                        }
                    } else if (nextVC - eR > 0) { // 3
                        // ****
                        // *v/*
                        // */<*
                        // ****
                        if ((eT - nextHC) / (nextVC - eR) < playerRatio) {
                            direction = Direction.LEFTWARDS; // <
                        } else {
                            direction = Direction.DOWNWARDS; // v
                        }
                    } else { // 2
                        direction = Direction.DOWNWARDS; // v
                    }
                } else if (nextHC - eB > 0) { // 7, 8, 9
                    if (eL - nextVC > 0) { // 7
                        // ****
                        // *>/*
                        // */^*
                        // ****
                        if ((nextHC - eB) / (eL - nextVC) < playerRatio) {
                            direction = Direction.RIGHTWARDS; // >
                        } else {
                            direction = Direction.UPWARDS; // ^
                        }
                    } else if (nextVC - eR > 0) { // 9
                        // ****
                        // *\<*
                        // *^\*
                        // ****
                        if ((nextHC - eB) / (nextVC - eR) < playerRatio) {
                            direction = Direction.LEFTWARDS; // <
                        } else {
                            direction = Direction.UPWARDS; // ^
                        }
                    } else { // 8
                        direction = Direction.UPWARDS; // ^
                    }
                } else if (eL - nextVC > 0) { // 4
                    direction = Direction.RIGHTWARDS; // >
                } else if (nextVC - eR > 0) { // 6
                    direction = Direction.LEFTWARDS; // <
                } else { // 5
//                    System.out.println("impossible");
                    direction = null;
                }
                //endregion
                if (direction != null) switch (direction) {
                    case RIGHTWARDS:
                        if (player.speedX >= 0 && entity.collideRightwards(pressedKeys, player)) {
                            player.setRight(eL - 0.01);
                            player.speedX = 0;
                            player.accX = 0;
                        }
                        break;
                    case LEFTWARDS:
                        if (player.speedX <= 0 && entity.collideLeftwards(pressedKeys, player)) {
                            player.setLeft(eR + 0.01);
                            player.speedX = 0;
                            player.accX = 0;
                        }
                        break;
                    case UPWARDS:
                        if (player.speedY <= 0 && entity.collideUpwards(pressedKeys, player)) {
                            player.topSupported = true;
                            player.speedY = Math.min(Math.abs(player.speedY), context.maxFallSpeed);
                            player.setTop(eB + 0.01);
                        }
                        break;
                    case DOWNWARDS:
                        if (player.speedY >= 0 && entity.collideDownwards(pressedKeys, player)) {
                            player.bottomSupported = true;
                            player.setBottom(eT + 0.01);
                        }
                        break;
                }
            }
            BufferedImage eImg = entity.getImage();
            if (eImg != null)
                g.drawImage(eImg, (int) (entity.getLeft() + entity.getImgOffsetX()) - stageX, (int) (entity.getTop() + entity.getImgOffsetY()), null);
        }

        // 一切有效信息就绪后，命令 Mario 跳帧
        for (Runnable r : invokeLater)
            r.run(); // 在跳帧之前，先处理延迟的事务
        player.tick(context.TARGET_TPF);

        // TODO 把右边的对象放到画面 list 中
        // TODO 把移出画面的对象移出 list


        // TODO 实装碰撞检测后，删除这一部分
        g.setColor(Color.YELLOW);
        g.drawLine(0, 100, 400, 100);
        g.setColor(Color.WHITE);
        g.drawLine((int) player.getLeft(), 0, (int) player.getLeft(), 300);
        g.drawLine(0, (int) player.getTop(), 400, (int) player.getTop());
        g.drawLine((int) player.getRight(), 0, (int) player.getRight(), 300);
        g.drawLine(0, (int) player.getBottom(), 400, (int) player.getBottom());
        g.setColor(Color.MAGENTA);
        g.drawString("Top: " + player.getTop(), 0, (int) player.getTop());
        g.drawString("Bottom: " + player.getBottom(), 0, (int) player.getBottom());
        g.drawString("Left: " + player.getLeft(), (int) player.getLeft(), 280);
        g.drawString("Right: " + player.getRight(), (int) player.getRight(), 300);

        if (player.getState() != EntityState.DISPOSED)
            g.drawImage(player.getImage(), (int) (player.getLeft() + player.getImgOffsetX()), (int) (player.getTop() + player.getImgOffsetY()), null);
        return output;
    }

    public void playerDie() {
        Runnable callback = () -> playerLife--;

    }

    @Override public void update(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        paint(buffer.getGraphics());
        g.drawImage(buffer, 0, 0, null);
    }
}
