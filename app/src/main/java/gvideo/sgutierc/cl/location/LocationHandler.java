package gvideo.sgutierc.cl.location;

import android.location.Location;

public interface LocationHandler {

    public enum Event {
        UPDATE, START, STOP
    }

    void handleLocation(Location location, Event event);

}
