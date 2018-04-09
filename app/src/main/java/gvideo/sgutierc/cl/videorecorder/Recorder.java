package gvideo.sgutierc.cl.videorecorder;

public interface Recorder
{
    /**
     *Recording methods
     */
    void startRecording();
    void stopRecording();
    void pauseRecording();
    void resumeRecording();

    /**
     * Previewing methods
     */
    void startPreview();
    void pausePreview();
    void resumePreview();
    void stopPreview();
}
