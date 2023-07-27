package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.filmExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedOperation;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewsServiceImpl implements ReviewsService {
    private final ReviewsStorage reviewsStorage;
    private final FeedStorage feedStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewsServiceImpl(ReviewsStorage reviewsStorage, FeedStorage feedStorage, FilmStorage filmStorage, UserStorage userStorage) {
        this.reviewsStorage = reviewsStorage;
        this.feedStorage = feedStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        log.info("+ getAllReviews");
        List<Review> answer = reviewsStorage.getAllReviews(count);
        log.info("- getAllReviews : {}", answer);
        return answer;
    }

    @Override
    public Review addReview(Review review) {
        log.info("+ addReview: {}", review);
        filmStorage.getFilm(review.getFilmId());
        userStorage.getUser(review.getUserId());
        Review answer = reviewsStorage.addReview(review);
        log.info("- addReview: {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.ADD, answer.getReviewId());
        return answer;
    }

    @Override
    public Review updateReview(Review review) {
        log.info("+ updateReview : {}", review);
        filmStorage.getFilm(review.getFilmId());
        userStorage.getUser(review.getUserId());
        Review answer = reviewsStorage.updateReview(review);
        log.info("- updateReview : {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.UPDATE, answer.getReviewId());
        return answer;
    }

    @Override
    public Review deleteReview(int id) {
        log.info("+ deleteReview : id = {}", id);
        Review answer = reviewsStorage.deleteReview(id);
        log.info("- deleteReview : {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.REMOVE, answer.getReviewId());
        return answer;
    }

    @Override
    public Review getReview(int id) {
        log.info("+ getReview : {}", id);
        Review answer = reviewsStorage.getReviewById(id);
        log.info("- getReview : {}", answer);
        return answer;
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer id, int count) {
        log.info("+ getReviewsByFilmId: id{}, count: {}", id, count);
        if (id == null) {
            return getAllReviews(count);
        }
        if (id == 0) {
            throw new FilmNotFoundException("Фильма с id = 0 не может быть");
        }
        filmStorage.getFilm(id);
        List<Review> answer = reviewsStorage.getReviewsByFilmId(id, count);
        log.info("- getReviewsByFilmId : {}", answer);
        return answer;
    }

    @Override
    public Review addLike(int id, int userId) {
        log.info("+ addLikeToReview: id{}, userId: {}", id, userId);
        userStorage.getUser(userId);
        reviewsStorage.getReviewById(id);
        Review answer = reviewsStorage.increaseUseful(id, userId);
        log.info("- addLikeToReview : {}", answer);
        return answer;
    }

    @Override
    public Review addDislike(int id, int userId) {
        log.info("+ addDislikeToReview: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.decreaseUseful(id, userId);
        log.info("- addDislikeToReview : {}: {}", id, answer);
        return answer;
    }

    @Override
    public Review deleteLike(int id, int userId) {
        log.info("+ deleteLikeFromReview: id{}, userId: {}", id, userId);
        userStorage.getUser(userId);
        reviewsStorage.getReviewById(id);
        Review answer = reviewsStorage.decreaseUseful(id, userId);
        log.info("- deleteLikeFromReview : {}", answer);
        return answer;
    }

    @Override
    public Review deleteDislike(int id, int userId) {
        log.info("+ deleteDislike: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.increaseUseful(id, userId);
        log.info("- deleteDislike : {}", answer);
        return answer;
    }
}
