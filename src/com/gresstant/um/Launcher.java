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
        // TODO 严重BUG！按键输入不及时
        f.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                game.keyQueue.offer(e);
            }
        });
        f.pack();
        game.start();
    }
}
