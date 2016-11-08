package de.verbavoice.stagecapservice.player;

/**
 * Created by crocop on 09.05.16.
 */
public interface SubtitlePlayerListener extends PlayerListener {
    void onUpdate(SubtitleUpdateDTO subtitleUpdateDTO, boolean change);
}
