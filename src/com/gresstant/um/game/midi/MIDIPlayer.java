package com.gresstant.um.game.midi;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public class MIDIPlayer {
    Sequence midiSequence;
    Sequencer midiSequencer;
    Thread midiLoopChecker;
    Runnable midiLoopCheckProcedure = () -> {
        while (midiLoopChecker != null) {
            try {
                if (!midiSequencer.isRunning()) {
                    midiSequencer.stop();
                    midiSequencer.setSequence((Sequence) null);
                    midiSequencer.setSequence(midiSequence);
                    midiSequencer.start();
                }
                Thread.sleep(100);
            } catch (Exception ignore) {}
        }
    };
    boolean disposed = false;

    public MIDIPlayer() throws Exception {
        midiSequencer = MidiSystem.getSequencer(true);
        midiSequencer.open();
    }

    public void playOnce(Sequence sequence) {
        tryStop();
        this.midiSequence = sequence;
        try { midiSequencer.setSequence(sequence); } catch (Exception ignore) {}
        midiSequencer.start();
    }

    public void playLoop(Sequence sequence) {
        tryStop();
        this.midiSequence = sequence;
        midiLoopChecker = new Thread(midiLoopCheckProcedure);
        midiLoopChecker.start();
    }

    public void tryStop() {
        if (midiLoopChecker != null) {
            midiLoopChecker.interrupt();
            midiLoopChecker = null;
        }
        if (isPlaying()) {
            midiSequencer.stop();
        }
        try { midiSequencer.setSequence((Sequence) null); } catch (Exception ignore) {}
    }

    public boolean isPlaying() {
        return midiSequencer.isOpen() && midiSequencer.isRunning();
    }

    public void dispose() {
        tryStop();
        midiSequencer.close();
        disposed = true;
    }

    @Override protected void finalize() throws Throwable {
        super.finalize();
        if (!disposed) dispose();
    }
}
