package de.verbavoice.stagecapservice;

import de.verbavoice.stagecapservice.domain.Project;
import de.verbavoice.stagecapservice.domain.Subtitle;
import de.verbavoice.stagecapservice.domain.enumeration.Language;
import de.verbavoice.stagecapservice.player.SubtitlePlayer;
import de.verbavoice.stagecapservice.player.SubtitlePlayerListener;
import de.verbavoice.stagecapservice.player.SubtitleUpdateDTO;
import de.verbavoice.stagecapservice.repository.AudioDescriptionRepository;
import de.verbavoice.stagecapservice.repository.ProjectRepository;
import de.verbavoice.stagecapservice.repository.SubtitleRepository;
import org.elasticsearch.common.io.ByteStreams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by crocop on 09.05.16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = StageCapServiceApp.class)
//@WebAppConfiguration
public class SubParserTest {

    @Inject
    private SubtitleRepository subtitleRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private AudioDescriptionRepository audioDescriptionRepository;

    @Test
    @Transactional
    public void createSubtitle() throws Exception {
        Project project = new Project();
        project.setTitle("Yoin");
        project.setDescription("Yoin Jugendsendung by abm");
        Resource resource = new ClassPathResource("yoin.jpg");
        InputStream is = resource.getInputStream();
        project.setImage(ByteStreams.toByteArray(is));
        project.setUser("admin");
        Project p = projectRepository.save(project);
       // project.se
        Subtitle subtitle = new Subtitle();
        subtitle.setProject(project);
        subtitle.setLanguage(Language.GERMAN);
        subtitle.setFileContentType("application/x-subrip");
        is = new ClassPathResource("yoin1504.srt").getInputStream();
        subtitle.setFile(ByteStreams.toByteArray(is));
        subtitleRepository.save(subtitle);
      //  projectRepository.save()
        List<Subtitle> subs = subtitleRepository.findByProjectId(p.getId());

        SubtitlePlayer player = new SubtitlePlayer();
        player.load(subs);
        player.add(new SubtitlePlayerListener() {
            @Override
            public void onUpdate(SubtitleUpdateDTO subtitleUpdateDTO, boolean change) {
                if(change)System.out.println(subtitleUpdateDTO.get(Language.GERMAN));
            }

            @Override
            public void onUpdate(Long time) {

            }

            @Override
            public void onStop() {
                System.out.println("---  stop ---");
            }

            @Override
            public void onPause() {
                System.out.println("---  pause ---");
            }

            @Override
            public void onPlay() {
                System.out.println("---  play ---");
            }
        });
        player.play();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                player.stop();
            }
        },30000);
        Thread.sleep(50000);
    }
}
