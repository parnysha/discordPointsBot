package org.example.springbotdiscord.events;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.RequiredArgsConstructor;
import org.example.springbotdiscord.dto.Points;
import org.example.springbotdiscord.repository.PointsRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageCreateEventServiceImpl implements EventListener<MessageCreateEvent>{
    private final PointsRepository pointsRepository;

    @Override
    public Class<MessageCreateEvent> getEventType() {
        return MessageCreateEvent.class;
    }

    @Override
    public Mono<Message> execute(MessageCreateEvent event) {
        Message message = event.getMessage();

        if (message.getContent().equalsIgnoreCase("/top")) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Топ по баллам:\n");
            List<Points> pointsList = pointsRepository.findAllByOrderByPointsBalanceDesc();
            for (int i=0;i<pointsList.size();i++){
                stringBuilder.append(""+(i+1)+". "+pointsList.get(i).getUsername()+" баланс: "+pointsList.get(i).getPointsBalance()+"\n");
            }
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage(stringBuilder.toString()));
        }
        if (message.getAuthor().get().getId().asLong()==308519440717447169L&&message.getContent().substring(0,4).equalsIgnoreCase("/add")){
            String[] messageCommand = message.getContent().substring(4).trim().split(" ");
            long pointsAdd;
            try {
                pointsAdd = Long.parseLong(messageCommand[1]);
            } catch (NumberFormatException e){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage(messageCommand[1]+" некорректное число"));
            }
            String userName = messageCommand[0];
            Points points = pointsRepository.findByUsername(userName);
            if (points==null){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage(userName+" такого пользователя не существует"));
            }
            points.setPointsBalance(points.getPointsBalance()+pointsAdd);
            pointsRepository.save(points);
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage(userName+" успешно получил баллы в колличестве "+pointsAdd));
        }
        if (message.getContent().substring(0,5).equalsIgnoreCase("/gift")){
            String[] messageCommand = message.getContent().substring(5).trim().split(" ");
            long pointsToGift;
            try {
                pointsToGift = Long.parseLong(messageCommand[1]);
            } catch (NumberFormatException e){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage(messageCommand[1]+" некорректное число"));
            }
            String userName = messageCommand[0];
            Points pointsTo = pointsRepository.findByUsername(userName);
            Points pointsFrom = pointsRepository.findByUsername(message.getAuthor().get().getUsername());
            if (pointsTo==null){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage(userName+" такого пользователя не существует"));
            }
            if (pointsFrom==null){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage("Зайдите в голосовой канал"));
            }
            if(pointsFrom.getPointsBalance()<pointsToGift){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage(pointsFrom.getUsername()+" на вашем счете недостаточно средств"));
            }
            if (pointsToGift<0){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage("Нельзя отправлять отрицательные числа"));
            }
            if (pointsFrom.getUsername().equals(pointsTo.getUsername())){
                return message.getChannel()
                        .flatMap(channel -> channel.createMessage("Нельзя самому себе отправлять"));
            }
            pointsFrom.setPointsBalance(pointsFrom.getPointsBalance()-pointsToGift);
            pointsTo.setPointsBalance(pointsTo.getPointsBalance()+pointsToGift);
            pointsRepository.save(pointsFrom);
            pointsRepository.save(pointsTo);
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("Пользователь "+pointsFrom.getUsername()+" подарил "+pointsToGift+" баллов пользователю "+pointsTo.getUsername()));
        }
        if (message.getContent().equalsIgnoreCase("/balance")) {
            String username = message.getAuthor().get().getUsername();
            Points points = pointsRepository.findByUsername(username);
            String balance = ("Баланс пользователя "+username+": "+points.getPointsBalance());
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage(balance));
        }
        if(message.getContent().equalsIgnoreCase("!курица")){
            return message.getChannel()
                    .flatMap(channel -> channel.createMessage("*Описание команд*"));
        }
        return handleError();
    }
}
