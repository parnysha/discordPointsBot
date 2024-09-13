package org.example.springbotdiscord.events;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import lombok.RequiredArgsConstructor;
import org.example.springbotdiscord.dto.Points;
import org.example.springbotdiscord.repository.PointsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class VoiceStateUpdateServiceImpl implements EventListener<VoiceStateUpdateEvent>{
    private final PointsRepository pointsRepository;

    @Override
    public Class<VoiceStateUpdateEvent> getEventType() {
        return VoiceStateUpdateEvent.class;
    }

    @Override
    public Mono<Message> execute(VoiceStateUpdateEvent event) {
        VoiceState voiceState = event.getCurrent();
        if (event.isJoinEvent()&&pointsRepository.findByUserId(voiceState.getUserId().asLong())!=null) {
            Points points = pointsRepository.findByUserId(voiceState.getUserId().asLong());
            points.setPointsStart(new Date().getTime()/1000);
            pointsRepository.save(points);
        }
        if (event.isJoinEvent()&&pointsRepository.findByUserId(voiceState.getUserId().asLong())==null&&!voiceState.getUser().block().isBot()){
            Points points = new Points();
            points.setUserId(voiceState.getUserId().asLong());
            points.setUsername(voiceState.getUser().block().getUsername());
            points.setPointsBalance(0L);
            points.setPointsStart(new Date().getTime()/1000);
            pointsRepository.save(points);
        }
        if (event.isLeaveEvent()&&pointsRepository.findByUserId(voiceState.getUserId().asLong())!=null){
            Points points = pointsRepository.findByUserId(voiceState.getUserId().asLong());
            points.setPointsBalance(points.getPointsBalance()+new Date().getTime()/1000-points.getPointsStart());
            points.setPointsStart(new Date().getTime()/1000);
            pointsRepository.save(points);
        }

        return handleError();
    }
}
