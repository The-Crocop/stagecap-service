package de.verbavoice.stagecapservice.web.websocket;

import de.verbavoice.stagecapservice.player.*;
import de.verbavoice.stagecapservice.repository.SubtitleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by crocop on 25.04.16.
 */

@Controller
public class ProjectTimestampService {

    private static final Logger log = LoggerFactory.getLogger(ProjectTimestampService.class);

    @Inject
    SimpMessageSendingOperations messagingTemplate;

    @Inject
    SubtitleRepository subtitleRepository;

    private Map<Long,SubtitlePlayer> playerMap = new HashMap<>();

    @SubscribeMapping("/topic/update/{id}")
    @SendTo("/topic/project/{id}")
    public SubtitleUpdateDTO sendUpdate(@DestinationVariable Long id, @Payload SubtitleUpdateDTO subtitleUpdateDTO){
        log.info("projectId: {}, subtitleUpdate: {}",id,subtitleUpdateDTO);

        SubtitlePlayer player = playerMap.get(id);

        switch (subtitleUpdateDTO.getPlayerInstruction()){
            case PLAY:
                if(player != null)player.play();
                else {
                    player = new SubtitlePlayer();
                    player.add(new MyPlayerListener(id));
                    player.load(subtitleRepository.findByProjectId(id));
                    playerMap.put(id,player);
                    player.play();
                }
                break;
            case STOP:
                if(player != null){
                    player.stop();
                    playerMap.remove(id);
                }
                break;
            case JUMP:
                if(player != null)player.setSeeker(subtitleUpdateDTO.getTimestamp());
                break;
            case PAUSE:
                if(player != null){
                    player.pause();
                }
                break;
            default:
                //do nothing
        }
        if(player != null)subtitleUpdateDTO.setTimestamp(player.getSeeker());
        subtitleUpdateDTO.setState(player.getState());
        return subtitleUpdateDTO;
    }

    @SubscribeMapping("/topic/project/{id}")
    public SubtitleUpdateDTO initialize(@DestinationVariable Long id){
        SubtitlePlayer player = playerMap.get(id);
        SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
        if(player != null){
            subtitleUpdateDTO.setState(player.getState());
            subtitleUpdateDTO.setTimestamp(player.getSeeker());
            subtitleUpdateDTO.setSubtitles(player.getCurrentSubtitles());
            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.INIT);
        }
        else {
            subtitleUpdateDTO.setState(PlayerState.STOPPED);
            subtitleUpdateDTO.setTimestamp(0L);
            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.INIT);
        }
        return  subtitleUpdateDTO;
    }



    private class MyPlayerListener implements SubtitlePlayerListener {

        private Long id;

        private String target;

        public MyPlayerListener(Long id) {
            this.id = id;
            target = "/topic/project/"+id;
        }

        @Override
        public void onUpdate(Long time) {
            SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
            subtitleUpdateDTO.setTimestamp(time);
            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.JUMP);
           // messagingTemplate.convertAndSend(target, subtitleUpdateDTO);
        }

        @Override
        public void onStop() {
            log.debug("onStop");
            SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
            subtitleUpdateDTO.setTimestamp(0L);
            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.STOP);
            messagingTemplate.convertAndSend(target,subtitleUpdateDTO);
        }

        @Override
        public void onPause() {
//            log.debug("onPause");
//            SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
//            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.PAUSE);
//            subtitleUpdateDTO.setState(PlayerState.PAUSED);
//            messagingTemplate.convertAndSend(target,subtitleUpdateDTO);
        }

        @Override
        public void onPlay() {
            log.debug("onPlay");
            SubtitleUpdateDTO subtitleUpdateDTO = new SubtitleUpdateDTO();
            subtitleUpdateDTO.setPlayerInstruction(PlayerInstruction.PLAY);
            messagingTemplate.convertAndSend(target,subtitleUpdateDTO);
        }

        @Override
        public void onUpdate(SubtitleUpdateDTO subtitleUpdateDTO, boolean change) {
            if(change){
                log.debug("onUpdate {}",subtitleUpdateDTO);
                messagingTemplate.convertAndSend(target,subtitleUpdateDTO);
            }else{
                messagingTemplate.convertAndSend(target,subtitleUpdateDTO);
            }
        }
    }
}
