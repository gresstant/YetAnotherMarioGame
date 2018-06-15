package com.gresstant.um;

import com.gresstant.um.game.Context;
import com.gresstant.um.game.display.GamePanel;
import com.gresstant.um.game.display.GameState;
import com.gresstant.um.game.display.Resource;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launcher {
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();
        Context context = new Context();
        context.imgResFuture = context.threadPool.submit(() -> Resource.loadImg(new File("res\\obj.png")));
        GamePanel game = new GamePanel(context, Resource.loadImg(new File("res\\obj.png")));
        f.setContentPane(game);
        f.setLocationByPlatform(true);
        f.setTitle("Ultimate Mario");
        f.setVisible(true);
        f.setResizable(false);
        f.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                f.setTitle("EXITING ...");
                context.exitCallback = f::dispose; // 这样可以保证游戏结束后再关闭窗口，避免 getGraphics 出错
                context.threadPool.shutdown();
                game.setState(GameState.EXITING);
            }
        });
        f.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() <= 0xFF) game.pressedKeys[e.getKeyCode()] = true;
            }

            @Override public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() <= 0xFF) game.pressedKeys[e.getKeyCode()] = false;
            }
        });
        f.addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) {
                game.paused = false;
            }

            @Override public void focusLost(FocusEvent e) {
                game.paused = true; // TODO BUG: 可能会导致一些动画方面的问题
            }
        });
        f.pack();
        game.start();
    }
}
