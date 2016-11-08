package de.verbavoice.stagecapservice.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by crocop on 26.04.16.
 */
public class PlayerImpl extends Player{

    private long seeker;
    private long startTime;
    private long endTime;

    private Timer timer;

    private final static Logger log = LoggerFactory.getLogger(PlayerImpl.class);

    private List<PlayerListener> listenerList = new ArrayList<>();

    @Override
    public void play() {
        super.play();
        startTime = Instant.now().toEpochMilli()-seeker;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                seeker = Instant.now().toEpochMilli() - startTime;
                fireUpdate();
            }
        }, 0, 40);
        firePlay();
    }

    @Override
    public void stop() {
        super.stop();
        timer.cancel();
        seeker = 0;
        fireStop();
    }

    @Override
    public void pause() {
        super.pause();
        timer.cancel();
        firePause();
    }

    private void fireUpdate(){
        listenerList.parallelStream().forEach( l -> l.onUpdate(seeker) );
    }
    private void fireStop(){
        listenerList.parallelStream().forEach( l -> l.onStop() );
    }
    private void firePause(){
        listenerList.parallelStream().forEach( l -> l.onPause() );
    }
    private void firePlay(){
        listenerList.parallelStream().forEach( l -> l.onPlay() );
    }

    public boolean addListener(PlayerListener playerListener) {
        return listenerList.add(playerListener);
    }

    public boolean removeListener(Object o) {
        return listenerList.remove(o);
    }

    public long getSeeker() {
        return seeker;
    }

    public void setSeeker(long seeker) {
        startTime = Instant.now().toEpochMilli() - seeker;
        this.seeker = seeker;
    }
}
