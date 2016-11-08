package de.verbavoice.stagecapservice.player;

/**
 * Created by crocop on 26.04.16.
 */
public interface PlayerListener {
    void onUpdate(Long time);
    void onStop();
    void onPause();
    void onPlay();
}
