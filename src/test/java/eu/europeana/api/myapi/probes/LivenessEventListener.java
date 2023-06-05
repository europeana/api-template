package eu.europeana.api.myapi.probes;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.context.event.EventListener;

/**
 * Created by luthien on 05/06/2023.
 */
public class LivenessEventListener {
    @EventListener
    public void onEvent(AvailabilityChangeEvent<LivenessState> event) {
        switch (event.getState()) {
            case BROKEN:
                // notify others
                break;
            case CORRECT:
                // we're back
        }
    }
}
