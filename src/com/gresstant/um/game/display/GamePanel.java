package com.gresstant.um.game.display;

import com.gresstant.um.game.Context;
import com.gresstant.um.game.map.MapBase;
import com.gresstant.um.game.map.MapReader;
import com.gresstant.um.game.map.MapReaderContext;
import com.gresstant.um.game.map.Map_0_0;
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
    public final Context context;
    /**
     * 按键检测相关。
     * 由父级写入数据。
     */
    public boolean[] pressedKeys = new boolean[500];
    /**
     * 游戏是否处于暂停状态
     */
    public boolean paused = false;

    // 用于避免 ConcurrentModificationException
    // 在 updateGame 的最后部分调用
    private List<Runnable> invokeLater = new LinkedList<>();

    /*
     * 关卡相关
     */

    private MapBase currentMap;
    private int chkpointID = -1; // TODO checkpoint 待实装
//    /**
//     * 舞台外、左侧的实体
//     */
//    public LinkedList<IEntity> pastEntities = new LinkedList<>(); // 后来我发现这些实体直接销毁就好了那么麻烦干什么
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
    public final int stageWidth = 400, stageHeight = 300;
    public int mapWidth = 440;
    public int winWidth; // TODO 胜利待实装
    public long winTimer = -1;
    public Color bgColor;
    public Mario player;
    public int playerLife = 3;
    public final Runnable marioDieCallback = () -> { playerLife--; setState(GameState.LIFE_SPLASH); };

    public GamePanel(Context context) {
        this.context = context;
        setPreferredSize(new Dimension(800, 600));
    }

    /**
     * 根据地图和复活点信息初始化舞台。
     * 调用此函数要求地图不为空。
     */
    private void init() {
        if (currentMap == null)
            throw new RuntimeException("current map is null");
        if (currentMap.getStructVerMajor() != 0 || currentMap.getStructVerMinor() > 0 || !(currentMap instanceof Map_0_0))
            throw new RuntimeException("unsupported map structure version\n" +
                    "expected structure version: 0, 0\n" +
                    "read structure version: " + currentMap.getStructVerMajor() + ", " + currentMap.getStructVerMinor());

        stageX = 0;
        winTimer = -1;

        Map_0_0 mapRef = (Map_0_0) currentMap;

        mapWidth = mapRef.getWidth();
        winWidth = mapRef.winX;
        bgColor = mapRef.bgColor;

        double playerX = chkpointID == -1 ? mapRef.marioX : mapRef.checkpointXs[chkpointID];
        double playerY = chkpointID == -1 ? mapRef.marioY : mapRef.checkpointYs[chkpointID];
        player = new Mario(context, playerX, playerY, marioDieCallback);
//        player.setGrowth(Mario.GrowthState.SMALL);
        player.activate();

//        pastEntities.clear();
        onStageEntities.clear();
        comingEntities.clear();

        for (IEntity entity : mapRef.getBlocks())
            comingEntities.add(entity.copy());
        for (IEntity entity : mapRef.getEnemies())
            comingEntities.add(entity.copy());

        scanAndActivate();
        scanAndDispose();
    }

    /**
     * 在马里奥生命数耗尽时调用，用于重置关卡数据
     */
    private void gameOver() {
        chkpointID = -1;
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

        BufferedImage bi = null, bi2 = null;
        try {
            bi = ImageIO.read(new File("res\\whitek.png"));
            bi2 = ImageIO.read(new File("res\\startscreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // cache 用于储存不用每次重绘的画面，需要在第一帧时初始化
        BufferedImage[] cache = null;

        // 指示是否不需要重绘画面
        boolean delayPaint = false;

        // 初始化地图读取模块
        MapReaderContext MRC = new MapReaderContext();
        MRC.context = context;
        MRC.addEntityLater = (entity) -> invokeLater.add(() -> comingEntities.add(entity));
        MRC.lifeIncrement = () -> playerLife++;
        MRC.marioSupplier = () -> player; // 这样写可以保证得到的始终有效
        MapReader mapReader = new MapReader(MRC);

        // 游戏主循环
        mainLoop: while (true) {
            if (paused && state != GameState.EXITING) {
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
                        delayPaint = false;
                        if (context.skipLogoSplash)
                            frameElapsed += 100000;
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.WHITE);
                        g.setColor(Color.BLACK);
                        g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 48));
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
                        if (!context.imgResFuture.isDone() || !context.midiResFuture.isDone()) {
                            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                            g.drawString("Loading ...", 16, 584);
                            break;
                        }
                        try {
                            context.imgRes = context.imgResFuture.get();
                            context.midiRes = context.midiResFuture.get();
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

                        g.clearRect(0, 0, getWidth(), getHeight());
//                        g.setColor(Color.BLACK);
//                        g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 48));
//                        g.drawString("Ultimate Mario (Preview)", 16, 64);
//                        g.drawString("Press Enter to start!", 16, 488);
//                        g.drawString("Press F1 to edit options!", 16, 536);
//                        g.drawString("Press Escape to exit!", 16, 584);
//                        g.setFont(new Font(g.getFont().getName(), Font.PLAIN, 16));
//                        g.drawString("Proudly brought to you by Gresstant & HxPowerShare", 16, 88);
//                        g.drawString("For codes, check GitHub gresstant/YetAnotherMarioGame.", 16, 104);
//                        g.drawString("Visit http://um.gresstant.com/ to get details.", 16, 120);
                        g.drawImage(bi2, 0, 0, null);

                        delayPaint = false;
                        frameElapsed++;
                    } else {
                        delayPaint = true;
                    }

                    if (pressedKeys[KeyEvent.VK_ENTER]) {
                        for (int i = 0; i < 1; i++) {
                            try {
                                currentMap = mapReader.read(context.mapSupplier.get());
                                setState(GameState.LIFE_SPLASH);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                if (context.mapReadExceptionCallback != null && context.mapReadExceptionCallback.test(ex))
                                    i--; // 条件符合则再来一次循环，不符合则这轮循环结束就出去
                                pressedKeys[KeyEvent.VK_ENTER] = false;
                            }
                        }
                    } else if (pressedKeys[KeyEvent.VK_F12]) {
                        context.mapChooser.run();
                        pressedKeys[KeyEvent.VK_F12] = false;
                    } else if (pressedKeys[KeyEvent.VK_ESCAPE]) {
                        setState(GameState.EXITING);
                    }
                    break;
                }
                case LIFE_SPLASH: {
                    if (frameElapsed == 0) { // 第一帧，用于初始化
                        context.bgmPlayer.tryStop();
                        g.dispose();
                        g = screenBuffer.createGraphics();
                        g.setBackground(Color.BLACK);
                        g.clearRect(0, 0, getWidth(), getHeight());

                        BufferedImage small = new BufferedImage(stageWidth, stageHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D gs = small.createGraphics();
                        if (playerLife >= 0) {
                            gs.drawImage(context.imgRes.getResource("MARIO$SMALL$STAND")[0], 160, 150 - 8, null);
                            gs.setFont(new Font(g.getFont().getName(), Font.PLAIN, 16));
                            gs.drawString("X", 190, 150 + 7);
                            gs.drawString(String.valueOf(playerLife), 215, 150 + 7);
                        } else {
                            context.bgmPlayer.playOnce(context.midiRes.getResource("game-over")[0]);
                            gs.setFont(new Font(g.getFont().getName(), Font.PLAIN, 16));
                            gs.drawString("GAME OVER", 150, 150 + 7);
                        }

                        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g.drawImage(small, 0, 0, 800, 600, null);

                        delayPaint = false;
                    } else {
                        delayPaint = true;
                    }

                    int timeElapsed = frameElapsed * context.TARGET_TPF; // 单位为毫秒
                    if (timeElapsed < 1000) { // 0s - 1s
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                    } else if (playerLife >= 0) {
                        init();
//                        onStageEntities.clear();
//                        onStageEntities.add(new FragileBlock(context, 100, 100));
//                        onStageEntities.add(new FragileBlock(context, 200, 60));
//                        onStageEntities.add(new FragileBlock(context, 216, 60));
//                        onStageEntities.add(new FragileBlock(context, 250, 80));
//                        onStageEntities.add(new FragileBlock(context, 184, 150));
//                        onStageEntities.add(new FragileBlock(context, 200, 150));
//                        onStageEntities.add(new FragileBlock(context, 216, 150));
//                        onStageEntities.add(new GroundBlock(context, 50, 50, 2, 2));
//                        onStageEntities.add(new FragileBlock(context, 232, 150));
//                        onStageEntities.add(new QuestionBlock(context, 232, 110, (point) -> {
//                            IEntity output = new Flower(context, point.x, 0);
//                            output.setTop(point.y - output.getHeight());
//                            output.activate();
//                            return output;
//                        }, (entity) -> invokeLater.add(() -> onStageEntities.add(entity)),
//                                context.imgRes.getResource("QUESTION$NORMAL$STAND")));
//                        onStageEntities.add(new FragileBlock(context, 248, 150));
//                        onStageEntities.add(new FragileBlock(context, 264, 150));
//                        onStageEntities.add(new Goomba(context, 264, 120));
//                        onStageEntities.add(new Bullet(context, 264, 100, 16.0));
//                        for (IEntity entity : onStageEntities)
//                            entity.activate();
//                        player = new Mario(context, 100, 99, () -> {
//                            playerLife--;
//                            setState(GameState.LIFE_SPLASH);
//                        });
//                        player.activate();
//                        player.setGrowth(Mario.GrowthState.SMALL);
                        context.bgmPlayer.playLoop(context.midiRes.getResource("overworld")[0]);
                        setState(GameState.IN_GAME);
                        break;
                    } else if (timeElapsed > 10000) {
                        setState(GameState.START_SCREEN);
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
                        delayPaint = false;
                    }
                    g.clearRect(0, 0, getWidth(), getHeight());
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g.drawImage(updateGame(), 0, 0, 800, 600, null);
                    if (state == GameState.IN_GAME) frameElapsed++;
                    break;
                }
            }

            lasting = System.currentTimeMillis() - beginTimestamp;

            if (!delayPaint) {
                gLog.drawString("FPS: " + String.format("%.1f", 1 / (Math.max(lasting, context.TARGET_TPF) / 1000.0)), 0, 15);
                gLog.drawString("timeElapsed: " + (frameElapsed * context.TARGET_TPF), 0, 30);
                gLog.drawString("sleep: " + (context.TARGET_TPF - lasting), 0, 45);
                getGraphics().drawImage(screenBuffer, 0, 0, null);
            }

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
     * @return 返回当前游戏的画面。尺寸应当为stageWidth*stageHeight。
     */
    private BufferedImage updateGame() {
        BufferedImage output = new BufferedImage(stageWidth, stageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = output.createGraphics();
        g.setBackground(bgColor); // 这里的性能可以优化一下
        g.clearRect(0, 0, getWidth(), getHeight());

        long timestamp = System.currentTimeMillis();

        // 胜利判定
        if (player.getRight() >= winWidth && winTimer == -1) {
            winTimer = 0;
            context.bgmPlayer.playOnce(context.midiRes.getResource("level-complete")[0]); // MIDI似乎会变慢，原因不明
        }
        if (winTimer >= 7000) {
            winTimer = -1;
            context.bgmPlayer.tryStop();
            context.sePlayer.tryStop();
            player.dispose();
            setState(context.winCallback != null && context.winCallback.getAsBoolean() ? GameState.LIFE_SPLASH : GameState.START_SCREEN);
        } else if (winTimer >= 0) {
            player.speedX = 12.0;
            player.accX = 0.0;
            winTimer += context.TARGET_TPF;
        }

        if (player.getState() != EntityState.DEAD && player.getState() != EntityState.DISPOSED) {
            // 死亡判定 + 检查自杀按键
            if (player.getBottom() > 400.0 || pressedKeys[KeyEvent.VK_O]) {
                player.die();
            }
            if (winTimer == -1) {
                // 更新蹲的状态，这个值不会在 tick 中被重置
                player.trySquat(pressedKeys[KeyEvent.VK_DOWN]);
                // 检查移动+跳跃按键，注意这一部分需要放到检查蹲之后
                if (pressedKeys[KeyEvent.VK_RIGHT])
                    player.accelerate(1.0);
                if (pressedKeys[KeyEvent.VK_LEFT])
                    player.accelerate(-1.0);
                if (pressedKeys[KeyEvent.VK_Z])
                    invokeLater.add(() -> player.tryJump(timestamp));

                player.run = pressedKeys[KeyEvent.VK_X];
            }
        }

        // 计算出本帧玩家的大致位置
        double displaceX = player.speedX * context.TARGET_TPF / 1000.0;
        double displaceY = player.speedY * context.TARGET_TPF / 1000.0;
        double nextVC = player.getVertCenter() + displaceX;
        double nextHC = player.getHorzCenter() + displaceY;
        double playerRatio = player.getHeight() / player.getWidth();

        // 不能让玩家跑到屏幕外边
        if (player.getLeft() + displaceX <= stageX) {
            player.leftSuported = true;
            player.setLeft(stageX);
        }

        // 更新舞台位置
        stageX = Math.max(Math.min((int) player.getVertCenter() - stageWidth / 2, mapWidth - stageWidth), stageX);

        // 真·碰撞检测，顺便把实体画出来
        for (IEntity entity : onStageEntities) {
            EntityState entityState = entity.getState();
            if (entityState == EntityState.DISPOSED) {
                invokeLater.add(() -> onStageEntities.remove(entity));
                continue;
            }
            EntityState playerState = player.getState();
            if (!(playerState == EntityState.DEAD || playerState == EntityState.DISPOSED) &&
                    !(entityState == EntityState.FROZEN /*|| entityState == EntityState.DISPOSED*/ || entityState == EntityState.DEAD) /*&&
                    Utilities.collide(player, entity, player.speedX, player.speedY, context.TARGET_TPF)*/) {
                if (entity.getTop() > 500.0) {
                    entity.dispose();
                    continue;
                }
                Direction direction = collidedDirection(player, entity, displaceX, displaceY); //collidedDirection(nextVC, nextHC, playerRatio, entity);
                if (direction != null) switch (direction) {
                    case RIGHTWARDS:
                        if (player.speedX >= 0 && entity.collideRightwards(pressedKeys, player)) {
                            player.rightSuported = true;
                            player.setRight(entity.getLeft() - 0.01);
                            player.speedX = 0;
                            player.accX = 0;
                        }
                        break;
                    case LEFTWARDS:
                        if (player.speedX <= 0 && entity.collideLeftwards(pressedKeys, player)) {
                            player.leftSuported = true;
                            player.setLeft(entity.getRight() + 0.01);
                            player.speedX = 0;
                            player.accX = 0;
                        }
                        break;
                    case UPWARDS:
                        if (player.speedY <= 0 && entity.collideUpwards(pressedKeys, player)) {
                            player.topSupported = true;
                            player.speedY = Math.min(Math.abs(player.speedY), context.maxFallSpeed);
                            player.setTop(entity.getBottom() + 0.01);
                        }
                        break;
                    case DOWNWARDS:
                        if (player.speedY >= 0 && entity.collideDownwards(pressedKeys, player)) {
                            player.bottomSupported = true;
                            player.setBottom(entity.getTop() /*+ 0.01*/);
                        }
                        break;
                }
            }
            if (entity instanceof IEnemy) {
                double dx = ((IEnemy) entity).getSpeedX() * context.TARGET_TPF / 1000.0;
                double dy = ((IEnemy) entity).getSpeedY() * context.TARGET_TPF / 1000.0;
                for (IEntity e : onStageEntities) {
                    if (e == entity) continue;
                    EntityState eState = e.getState();
                    if (!(eState == EntityState.FROZEN || eState == EntityState.DISPOSED || eState == EntityState.DEAD) /*&&
                            Utilities.intersect(entity, e)*/) {
                        Direction d = collidedDirection(entity, e, dx, dy);
                        if (d == Direction.LEFTWARDS && e.collideLeftwards(pressedKeys, entity)) {
                            ((IEnemy) entity).barrierLeft();
                            entity.setLeft(e.getRight() + 0.01);
                        } else if (d == Direction.RIGHTWARDS && e.collideRightwards(pressedKeys, entity)) {
                            ((IEnemy) entity).barrierRight();
                            entity.setRight(e.getLeft() - 0.01);
                        } else if (d == Direction.UPWARDS && e.collideUpwards(pressedKeys, entity)) {
                            ((IEnemy) entity).barrierTop();
                            entity.setTop(e.getBottom() + 0.01);
                        } else if (d == Direction.DOWNWARDS && e.collideDownwards(pressedKeys, entity)) {
                            ((IEnemy) entity).barrierBottom();
                            entity.setBottom(e.getTop() /*- 0.01*/);
                        }// else continue
                    }
                }
            }
            if (player.getState() != EntityState.DEAD && entity.getState() != EntityState.FROZEN && entity.getState() != EntityState.DISPOSED)
                entity.tick(context.TARGET_TPF);
            BufferedImage eImg = entity.getImage();
            if (eImg != null) {
                g.drawImage(eImg, (int) (entity.getLeft() + entity.getImgOffsetX()) - stageX, (int) (entity.getTop() + entity.getImgOffsetY()), null);
            }
        }

        // 处理延迟的事务
        for (Runnable r : invokeLater)
            r.run();
        invokeLater.clear(); // 然后清空

        // 在列表间移动实体
        scanAndActivate();
        scanAndDispose();

        // 一切有效信息就绪后，命令 Mario 跳帧
        player.tick(context.TARGET_TPF);

        if (player.getState() != EntityState.DISPOSED)
            g.drawImage(player.getImage(), (int) (player.getLeft() + player.getImgOffsetX()) - stageX, (int) (player.getTop() + player.getImgOffsetY()), null);
        return output;
    }

    /**
     * 激活 comingEntities 中已经进入舞台的实体
     */
    private void scanAndActivate() {
        int stageRight = stageX + stageWidth;
        ListIterator<IEntity> iter = comingEntities.listIterator();
        while (iter.hasNext()) {
            IEntity got = iter.next();
            if (got.getLeft() <= stageRight) {
                iter.remove();
                onStageEntities.add(got);
                got.activate();
            }
        }
    }

    /**
     * 销毁 onStageEntites 中已经离开舞台的实体
     */
    private void scanAndDispose() {
        for (IEntity entity : onStageEntities) {
            if (entity.getRight() < stageX) {
                entity.dispose();
            }
        }
    }

    public Direction collidedDirection(IEntity e1, IEntity e2, double displaceX, double displaceY) {
        if (!Utilities.collide(e1, e2, displaceX, displaceY)) {
            if (Utilities.collide(e1, e2, displaceX, displaceY - 0.1)) {
                return Direction.DOWNWARDS;
            } else {
                return null;
            }
        }
        return collidedDirection(e1.getVertCenter() + displaceX,
                e1.getHorzCenter() + displaceY,
                e1.getHeight() / e1.getWidth(),
                e2);
    }

    public Direction collidedDirection(double nextVC, double nextHC, double playerRatio, IEntity entity) {
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
            direction = null;
        }
        //endregion
        return direction;
    }

    @Override public void update(Graphics g) {
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        paint(buffer.getGraphics());
        g.drawImage(buffer, 0, 0, null);
    }
}
