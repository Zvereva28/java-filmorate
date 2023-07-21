package ru.yandex.practicum.filmorate.storage.impl.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"/schema.sql", "/test-data-reviews.sql"})
public class ReviewsDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    private final ReviewsDbStorage reviewsDbStorage;

    private static final int COUNT = 10;

    @Test
    void getAllReviewsWhenEmpty() {
        jdbcTemplate.execute("TRUNCATE TABLE reviews");
        assertEquals(0, reviewsDbStorage.getAllReviews(COUNT).size());
    }

    @Test
    void getAllReviewsWhenFourReviews() {
        assertEquals(4, reviewsDbStorage.getAllReviews(COUNT).size());
    }

    @Test
    void getAllReviewsWhenFourReviewsAndCountIsTwo() {
        assertEquals(2, reviewsDbStorage.getAllReviews(2).size());
    }

    @Test
    void addReview() {
        reviewsDbStorage.addReview(new Review(0, "New Content", true, 1, 3, 0));
        assertEquals(5, reviewsDbStorage.getAllReviews(COUNT).size());
    }

    @Test
    void updateReview() {
        reviewsDbStorage.updateReview(new Review(1, "New negative review from user 1 to film 1", false, 1, 1, 0));
        assertEquals(false, reviewsDbStorage.getReviewById(1).getIsPositive());
    }

    @Test
    void updateReviewWhenNoFilm() {
        assertThrows(FilmNotFoundException.class, () -> reviewsDbStorage.updateReview(
                new Review(1, "New negative review from user 1 to film 9", false,
                        1, 9, 0)));
    }

    @Test
    void updateReviewWhenNoUser() {
        assertThrows(UserNotFoundException.class, () -> reviewsDbStorage.updateReview(
                new Review(1, "New negative review from user 9 to film 1", false,
                        9, 1, 0)));
    }

    @Test
    void deleteReview() {
        reviewsDbStorage.deleteReview(1);
        assertEquals(3, reviewsDbStorage.getAllReviews(COUNT).size());
    }

    @Test
    void deleteReviewWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsDbStorage.deleteReview(9));
    }

    @Test
    void getReviewById() {
        assertEquals("Content for positive review 2 from user 1 to film 2", reviewsDbStorage.getReviewById(2).getContent());
    }

    @Test
    void getReviewByIdWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsDbStorage.getReviewById(9));
    }

    @Test
    void getReviewsByFilmId() {
        assertEquals(2, reviewsDbStorage.getReviewsByFilmId(1, COUNT).size());
    }

    @Test
    void getReviewsByFilmIdWhenNoFilm() {
        assertThrows(FilmNotFoundException.class, () -> reviewsDbStorage.getReviewsByFilmId(9, COUNT));
    }

    @Test
    void increaseUseful() {
        assertEquals(1, reviewsDbStorage.increaseUseful(1, 3).getUseful());
    }

    @Test
    void increaseUsefulWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsDbStorage.increaseUseful(9, 1));
    }

    @Test
    void increaseUsefulWhenNoUser() {
        assertThrows(UserNotFoundException.class, () -> reviewsDbStorage.increaseUseful(1, 9));
    }

    @Test
    void decreaseUseful() {
        assertEquals(-1, reviewsDbStorage.decreaseUseful(1, 3).getUseful());
    }

    @Test
    void decreaseUsefulWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsDbStorage.decreaseUseful(9, 1));
    }

    @Test
    void decreaseUsefulWhenNoUser() {
        assertThrows(UserNotFoundException.class, () -> reviewsDbStorage.decreaseUseful(1, 9));
    }
}
