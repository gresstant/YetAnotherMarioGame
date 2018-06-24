package com.gresstant.um;

import com.gresstant.um.game.*;
import com.gresstant.um.game.display.*;
import com.gresstant.um.game.midi.*;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Launcher {
    public static void main(String[] args) throws Exception {
        JFrame f = new JFrame();
        Context context = new Context();
        context.imgResFuture = context.threadPool.submit(() -> Resource.loadImg(new File("res\\obj.png")));
        context.midiResFuture = context.threadPool.submit(() -> {
            context.bgmPlayer = new MIDIPlayer();
            context.sePlayer = new MIDIPlayer();
            context.midiSynthesizer = MidiSystem.getSynthesizer();
            for (MidiChannel channel : context.midiSynthesizer.getChannels()) {
                channel.setMute(false);
                channel.controlChange(7, 127);
            }
            List<File> fileList = new ArrayList<>();
            for (File file : new File("res").listFiles()) {
                if (file.isFile() && file.getName().endsWith(".mid"))
                    fileList.add(file);
            }
            return Resource.loadMidi(fileList);
        });
        context.exitCallback = () -> {
            f.setTitle("EXITING ...");
            context.threadPool.shutdown();
            context.bgmPlayer.dispose();
            context.sePlayer.dispose();
            f.dispose();
            System.exit(0);
        };
        GamePanel game = new GamePanel(context);
        f.setContentPane(game);
        f.setLocationByPlatform(true);
        f.setTitle("Ultimate Mario");
        f.setVisible(true);
        f.setResizable(false);
        f.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
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
                game.paused = true;
            }
        });
        f.pack();
        game.start();
    }
}
