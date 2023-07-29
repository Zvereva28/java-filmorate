package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.dbExceptions.DbException;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class FeedDbStorage implements FeedStorage {
    private static final String GET_FEED_EVENT_BY_USER_ID = "SELECT EVENT_ID, TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID FROM FEED WHERE USER_ID = ";
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addToFeedDb(Integer userId, FeedEventType eventType, FeedOperation operation, Integer entityId) {
        log.info("+ addToFeedDb : userId = " + userId + ", eventType = " + eventType + ", operation = " + operation + ", entityId = " + entityId);
        String time = String.valueOf(LocalDateTime.now());
        SimpleJdbcInsert simpleJdbcInsert;
        try {
           simpleJdbcInsert= new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                    .withTableName("feed")
                    .usingGeneratedKeyColumns("event_id");
        } catch (NullPointerException e){
            throw new FilmException("Событие " + eventType.toString() + "не создано");
        }


        Map<String, String> params = Map.of("timestamp", time, "user_id", userId.toString(),
                "event_type", eventType.toString(), "operation", operation.toString(), "entity_id", entityId.toString());

        simpleJdbcInsert.executeAndReturnKey(params);
        log.info("- addToFeedDb");
    }

    @Override
    public List<FeedEvent> getFeedByUserId(int id) {
        log.info("+ getFeedByUserId : id = {}", id);
        List<FeedEvent> answer = getEvents(GET_FEED_EVENT_BY_USER_ID + id);
        log.info("- getFeedByUserId : {}", answer);

        return answer;
    }

    private FeedEvent createEvent(ResultSet rs) {
        try {
            return new FeedEvent(
                    rs.getInt("event_id"), rs.getTimestamp("timeStamp").getTime(),
                    rs.getInt("user_id"), FeedEventType.valueOf(rs.getString("event_type")),
                    FeedOperation.valueOf(rs.getString("operation")), rs.getInt("entity_id")
            );
        } catch (SQLException e) {
            throw new DbException("Ошибка при создании события в БД. " + e.getMessage());
        }
    }

    private List<FeedEvent> getEvents(String sql) {
        List<FeedEvent> events = new ArrayList<>();
        Optional<List<FeedEvent>> reviewsOpt = jdbcTemplate.query(sql, (rs, rowNum) -> {
            do {
                events.add(createEvent(rs));
            } while (rs.next());
            events.sort(Comparator.comparingInt(FeedEvent::getEventId));
            return events;
        }).stream().findFirst();

        return reviewsOpt.orElse(new ArrayList<>());
    }
}
