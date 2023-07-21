package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

import java.util.List;

public interface FeedStorage {
    void addToFeedDb(Integer userId, FeedEventType eventType, FeedOperation operation, Integer entityId);

    List<FeedEvent> getFeedByUserId(int id);
}
