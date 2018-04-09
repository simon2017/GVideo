package gvideo.sgutierc.cl.util;

import java.util.concurrent.TimeUnit;

/**
 *
 */
public class Watch {
    private long initTime;
    private long pauseTime;
    private Watch auxWatch;

    public Watch() {

    }

    public void start() {
        initTime = System.nanoTime();
    }

    public void pause() {
        auxWatch = new Watch();
        auxWatch.start();
    }

    public void resume() {
        if (auxWatch == null) return;
        pauseTime = auxWatch.getElapsedTime(TimeUnit.MILLISECONDS);

        auxWatch = null;
    }

    private long getTime() {
        long markTime = System.nanoTime();
        return markTime - pauseTime - initTime;
    }

    public long getElapsedTime(TimeUnit unit) {
        return unit.convert(getTime(), TimeUnit.MILLISECONDS);
    }

}
