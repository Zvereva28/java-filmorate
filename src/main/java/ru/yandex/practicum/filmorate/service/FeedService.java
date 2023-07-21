package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;

import java.util.List;

public interface FeedService {
    FeedEvent addToFeed(int userId, FeedEventType eventType, FeedOperation operation, int entityId);

    List<FeedEvent> getFeedByUserId(int id);
}