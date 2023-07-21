package ru.yandex.practicum.filmorate.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.impl.dao.FeedDbStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedDbStorage feedDbStorage;

    @Override
    public FeedEvent addToFeed(int userId, FeedEventType eventType, FeedOperation operation, int entityId) {
        FeedEvent answer = feedDbStorage.addToFeedDb(userId, eventType, operation, entityId);
        log.info("+ addToFeed. userId: {}, eventType: {}, operation: {}, entityId {}", userId, eventType, operation, entityId);
        return answer;
    }

    @Override
    public List<FeedEvent> getFeedByUserId(int id) {
        List<FeedEvent> answer = feedDbStorage.getFeedByUserId(id);
        log.info("getFeedByUserId: {}", answer);
        return answer;
    }
}
