package de.verbavoice.stagecapservice.player;

import de.verbavoice.stagecapservice.domain.enumeration.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by crocop on 25.04.16.
 */
public class SubtitleUpdateDTO {

    private Long timestamp;
    private PlayerInstruction playerInstruction;
    private PlayerState state;
    private Map<Language,String> subtitles = new HashMap();

    public SubtitleUpdateDTO() {
    }

    public SubtitleUpdateDTO(Long timestamp, PlayerInstruction playerInstruction, PlayerState state) {
        this.timestamp = timestamp;
        this.playerInstruction = playerInstruction;
        this.state = state;
    }

    public SubtitleUpdateDTO(long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public PlayerInstruction getPlayerInstruction() {
        return playerInstruction;
    }

    public void setPlayerInstruction(PlayerInstruction playerInstruction) {
        this.playerInstruction = playerInstruction;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public Map<Language, String> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(Map<Language, String> subtitles) {
        this.subtitles = subtitles;
    }

    public String remove(Object key) {
        return subtitles.remove(key);
    }

    public String get(Object key) {
        return subtitles.get(key);
    }

    public void putAll(Map<? extends Language, ? extends String> m) {
        subtitles.putAll(m);
    }

    public String put(Language key, String value) {
        return subtitles.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubtitleUpdateDTO that = (SubtitleUpdateDTO) o;

        if (getTimestamp() != null ? !getTimestamp().equals(that.getTimestamp()) : that.getTimestamp() != null)
            return false;
        if (getPlayerInstruction() != that.getPlayerInstruction()) return false;
        return getState() == that.getState();

    }

    @Override
    public int hashCode() {
        int result = getTimestamp() != null ? getTimestamp().hashCode() : 0;
        result = 31 * result + (getPlayerInstruction() != null ? getPlayerInstruction().hashCode() : 0);
        result = 31 * result + (getState() != null ? getState().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubtitleUpdateDTO{" +
            "timestamp=" + timestamp +
            ", playerInstruction=" + playerInstruction +
            ", state=" + state +
            '}';
    }
}
