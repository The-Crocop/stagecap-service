package de.verbavoice.stagecapservice.player;

import de.verbavoice.stagecapservice.domain.Subtitle;
import de.verbavoice.stagecapservice.domain.enumeration.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by crocop on 26.04.16.
 */
public class SubtitlePlayer implements PlayerListener{

    private List<SubtitlePlayerListener> subtitlePlayerListener = new ArrayList<>();

    private List<SeekableSubtitle> seekableSubtitles;
    private PlayerImpl player = new PlayerImpl();
    private long length = 0;


    public SubtitlePlayer() {
        player.addListener(this);
    }

    public void load(List<Subtitle> subtitles){
        seekableSubtitles = subtitles.stream().map(SubtitleConverter::subtitleToSeekableSubtitle).collect(Collectors.toList());
        length = seekableSubtitles.stream().mapToLong(s -> {
          int count = s.getSubtitles().size();
            return s.getSubtitles().get(count-1).endTimeMillis();
        }).max().getAsLong();
    }

    /**
     *
     * @return true if something changed
     */
    private boolean update(){
        boolean change = false;//switch this if some subtitle has changed
        for(SeekableSubtitle seekableSubtitle:seekableSubtitles){
            long timestamp = player.getSeeker();
            int position = seekableSubtitle.getPositionForTimestamp(timestamp);
            if (seekableSubtitle.getPosition() != position){
                change = true;
                seekableSubtitle.setPosition(position);
            }
        };
      return change;
    };

    //delegated Methods from player
    public void play() {
        if(seekableSubtitles == null || seekableSubtitles.isEmpty())throw new NullPointerException("you have to load subtitles before calling play!");
        player.play();
    }

    public void stop() {
        player.stop();
    }

    public void pause() {
        player.pause();
    }

    public long getSeeker() {
        return player.getSeeker();
    }

    public void setSeeker(long seeker) {
        player.setSeeker(seeker);
        onUpdate(seeker);
    }

    public void error() {
        player.error();
    }

    public PlayerState getState() {
        return player.getState();
    }

    private SubtitleUpdateDTO generateDTO(){
        SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
        subtitleUpdateDTO.setTimestamp(player.getSeeker());
        subtitleUpdateDTO.setState(player.getState());
        subtitleUpdateDTO.setSubtitles(seekableSubtitles.parallelStream().collect(Collectors.toMap(SeekableSubtitle::getLanguage,SeekableSubtitle::getCurrentSubtitle)));
        return  subtitleUpdateDTO;
    };
    public Map<Language,String> getCurrentSubtitles(){
        return seekableSubtitles.parallelStream().collect(Collectors.toMap(SeekableSubtitle::getLanguage,SeekableSubtitle::getCurrentSubtitle));
    }
    //add listeners

    public boolean add(SubtitlePlayerListener subtitlePlayerListener) {
        return this.subtitlePlayerListener.add(subtitlePlayerListener);
    }

    public boolean remove(Object o) {
        return subtitlePlayerListener.remove(o);
    }

    //listener Methods
    @Override
    public void onUpdate(Long time) {
            if(time >= length)stop();
            else {
                boolean somethingChanged = update();//update the Subtitles and check if any changes occur
                SubtitleUpdateDTO subtitleUpdateDTO = generateDTO();
                subtitleUpdateDTO.setPlayerInstruction(somethingChanged? PlayerInstruction.UPDATE_TEXT:PlayerInstruction.UPDATE_TIME);
                fireOnUpdate(subtitleUpdateDTO,somethingChanged);
            }
    }

    @Override
    public void onStop() {
        fireOnStop();
    }

    @Override
    public void onPause() {
        fireOnPause();
    }

    @Override
    public void onPlay() {
        fireOnPlay();
    }

    //fire updates to listeners
    private void fireOnUpdate(SubtitleUpdateDTO subtitleUpdateDTO,boolean change){
        subtitlePlayerListener.parallelStream().forEach(e -> e.onUpdate(subtitleUpdateDTO,change));
    };
    private void fireOnPause(){
        subtitlePlayerListener.parallelStream().forEach(PlayerListener::onPause);
    }
    private void fireOnPlay(){
        subtitlePlayerListener.parallelStream().forEach(PlayerListener::onPlay);
    }
    private void fireOnStop(){
        subtitlePlayerListener.parallelStream().forEach(PlayerListener::onStop);
    }

}
