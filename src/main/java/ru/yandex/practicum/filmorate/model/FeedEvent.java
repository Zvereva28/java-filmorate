package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class FeedEvent {
    @NotNull(message = "eventId may not be null")
    int eventId;

    @NotNull(message = "timestamp may not be null")
    long timestamp;

    @NotNull(message = "userId may not be null")
    int userId;

    @NotNull(message = "eventType may not be null")
    FeedEventType eventType;

    @NotNull(message = "operation may not be null")
    FeedOperation operation;

    @NotNull(message = "entityId may not be null")
    int entityId;
}
