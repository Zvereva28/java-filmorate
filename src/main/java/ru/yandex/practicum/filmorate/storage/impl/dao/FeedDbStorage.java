package ru.yandex.practicum.filmorate.storage.impl.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DbException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.validators.FeedValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FeedValidator validator;
    private static final String GET_FEED_EVENT_BY_USER_ID = "SELECT event_id, timestamp, user_id, event_type, operation, entity_id FROM feed WHERE user_id = ";

    public FeedDbStorage(JdbcTemplate jdbcTemplate, FeedValidator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;
    }

    @Override
    public void addToFeedDb(Integer userId, FeedEventType eventType, FeedOperation operation, Integer entityId) {
        String time = String.valueOf(java.time.LocalDateTime.now());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(Objects.requireNonNull(jdbcTemplate.getDataSource()))
                .withTableName("feed")
                .usingGeneratedKeyColumns("event_id");

        Map<String, String> params = Map.of("timestamp", time, "user_id", userId.toString(),
                "event_type", eventType.toString(), "operation", operation.toString(), "entity_id", entityId.toString());

        simpleJdbcInsert.executeAndReturnKey(params);
    }

    @Override
    public List<FeedEvent> getFeedByUserId(int id) {
        validator.checkUser(id);
        return getEvents(GET_FEED_EVENT_BY_USER_ID + id);
    }

    private FeedEvent createEvent(ResultSet rs) {
        System.out.println("createEvent" + rs.toString());
        try {
            return new FeedEvent(
                    rs.getInt("event_id"), rs.getTimestamp("timeStamp").getTime(),
                    rs.getInt("user_id"),  FeedEventType.valueOf(rs.getString("event_type")),
                    FeedOperation.valueOf(rs.getString("operation")), rs.getInt("entity_id")
            );
        } catch (SQLException e) {
            throw new DbException("Ошибка в БД.");
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
