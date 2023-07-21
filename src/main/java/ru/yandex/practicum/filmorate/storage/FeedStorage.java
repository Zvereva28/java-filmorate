package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

import java.util.List;

public interface FeedStorage {
    FeedEvent addToFeedDb(Integer userId, FeedEventType eventType, FeedOperation operation, Integer entityId);

    FeedEvent getFeedEventById(Integer id);

    List<FeedEvent> getFeedByUserId(int id);
}
