package de.verbavoice.stagecapservice.player;

import de.verbavoice.parser.Subtitle;
import de.verbavoice.stagecapservice.domain.enumeration.Language;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by crocop on 09.05.16.
 */
public class SeekableSubtitle {
    private Long id;
    private int position = -1;
    private List<Subtitle> subtitles = new ArrayList<>();
    private Language language;


    public SeekableSubtitle() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    //utility funcitons
    public int getPositionForTimestamp(long timestamp){
        for(int i = 0;i < subtitles.size() ;i++){
            Subtitle sub = subtitles.get(i);
            if(timestamp >= sub.startTimeMillis() && timestamp <= sub.endTimeMillis()){
                return i;
            }
        }
        return -1;
    };

    public String getSubtitleForPosition(int position){
        if(position != -1 )return subtitles.get(position).text();
        else return "";
    };

    public String getSubtitleForTimestamp(long timestamp){
        int position = getPositionForTimestamp(timestamp);
        return position != -1 ? subtitles.get(position).text(): "";
    }

    public String getCurrentSubtitle(){
        return getSubtitleForPosition(position);
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeekableSubtitle that = (SeekableSubtitle) o;

        if (getPosition() != that.getPosition()) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getSubtitles() != null ? !getSubtitles().equals(that.getSubtitles()) : that.getSubtitles() != null)
            return false;
        return getLanguage() == that.getLanguage();

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getPosition();
        result = 31 * result + (getSubtitles() != null ? getSubtitles().hashCode() : 0);
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SeekableSubtitle{" +
            "id=" + id +
            ", position=" + position +
            ", subtitles=" + subtitles +
            ", language=" + language +
            '}';
    }
}
