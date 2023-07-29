package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

@Data
@AllArgsConstructor
public class FeedEvent {
    int eventId;

    long timestamp;

    int userId;

    FeedEventType eventType;

    FeedOperation operation;

    int entityId;
}
