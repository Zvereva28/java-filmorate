package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.reviewExceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.userExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.dto.ReviewsDTO;
import ru.yandex.practicum.filmorate.service.impl.ReviewsServiceImpl;
import ru.yandex.practicum.filmorate.storage.impl.dao.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.ReviewsDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.dao.UserDBStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"/schema.sql", "/test-data-reviews.sql"})
class ReviewsControllerTest {

    private static final int COUNT = 10;
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    private ReviewsController reviewsController;

    @BeforeEach
    public void setUp() {
        reviewsController = new ReviewsController(new ReviewsServiceImpl(new ReviewsDbStorage(jdbcTemplate), new FeedDbStorage(jdbcTemplate), new FilmDBStorage(jdbcTemplate), new UserDBStorage(jdbcTemplate)));

    }

    @Test
    void getAllReviewsWhenEmpty() {
        jdbcTemplate.execute("TRUNCATE TABLE reviews");
        assertEquals(0, reviewsController.getAllReviews(1, COUNT).size());
    }

    @Test
    void getAllReviewsWhenFourReviewsAndCountIsTwo() {
        assertEquals(2, reviewsController.getAllReviews(1, COUNT).size());
    }

    @Test
    void addReview() {
        reviewsController.addReview(new ReviewsDTO(0, "New Content", true, 1, 3, 0));
        assertEquals(2, reviewsController.getAllReviews(3, COUNT).size());
    }

    @Test
    void updateReview() {
        reviewsController.updateReview(new ReviewsDTO(1, "New negative review from user 1 to film 1", false, 1, 1, 0));
        assertEquals(false, reviewsController.getReview(1).getIsPositive());
    }

    @Test
    void updateReviewWhenNoFilm() {
        assertThrows(FilmNotFoundException.class, () -> reviewsController.updateReview(
                new ReviewsDTO(1, "New negative review from user 1 to film 9", false,
                        1, 9, 0)));
    }

    @Test
    void updateReviewWhenNoUser() {
        assertThrows(UserNotFoundException.class, () -> reviewsController.updateReview(
                new ReviewsDTO(1, "New negative review from user 9 to film 1", false,
                        9, 1, 0)));
    }

    @Test
    void deleteReview() {
        reviewsController.deleteReview(1);
        assertEquals(1, reviewsController.getAllReviews(1, COUNT).size());
    }

    @Test
    void deleteReviewWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsController.deleteReview(9));
    }

    @Test
    void getReviewById() {
        assertEquals("Content for positive review 2 from user 1 to film 2", reviewsController.getReview(2).getContent());
    }

    @Test
    void getReviewByIdWhenNoReview() {
        assertThrows(ReviewNotFoundException.class, () -> reviewsController.getReview(9));
    }

    @Test
    void addLike() {
        reviewsController.addLike(1, 1);
        assertEquals(1, reviewsController.getReview(1).getUseful());
    }

    @Test
    void addDislike() {
        reviewsController.addDislike(1, 1);
        assertEquals(-1, reviewsController.getReview(1).getUseful());
    }

    @Test
    void deleteLike() {
        reviewsController.deleteLike(1, 1);
        assertEquals(-1, reviewsController.getReview(1).getUseful());
    }

    @Test
    void deleteDislike() {
        reviewsController.deleteDislike(1, 1);
        assertEquals(1, reviewsController.getReview(1).getUseful());
    }

}
