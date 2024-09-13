package org.example.springbotdiscord.events;

import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

public interface EventListener<T extends Event> {


    Class<T> getEventType();
    Mono<Message> execute(T event);

    default Mono<Message> handleError() {
        return Mono.empty();
    }
}
