package de.verbavoice.stagecapservice.player;

import de.verbavoice.parser.SRTParser;
import de.verbavoice.stagecapservice.domain.Subtitle;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by crocop on 09.05.16.
 */
public class SubtitleConverter {
    public static SeekableSubtitle subtitleToSeekableSubtitle(Subtitle subtitle){
        SeekableSubtitle seekableSubtitle =  new SeekableSubtitle();
        seekableSubtitle.setId(subtitle.getId());
        seekableSubtitle.setLanguage(subtitle.getLanguage());
        seekableSubtitle.setSubtitles(subtitlesFromBytes(subtitle.getFile()));
        return  seekableSubtitle;
    }

    private static  List<de.verbavoice.parser.Subtitle> subtitlesFromBytes(byte[] bytes){
        SRTParser parser = new SRTParser();
        return parser.parse(new ByteArrayInputStream(bytes)).subtitles();
    }
}
