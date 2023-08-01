package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
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
        log.debug("+ getAllReviews");
        List<Review> reviews = reviewsStorage.getAllReviews(count);
        log.debug("- getAllReviews : {}", reviews);
        return reviews;
    }

    @Override
    public Review addReview(Review review) {
        log.debug("+ addReview: {}", review);
        filmStorage.getFilm(review.getFilmId());
        userStorage.getUser(review.getUserId());
        Review answer = reviewsStorage.addReview(review);
        log.debug("- addReview: {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.ADD, answer.getReviewId());
        return answer;
    }

    @Override
    public Review updateReview(Review review) {
        log.debug("+ updateReview : {}", review);
        Review answer = reviewsStorage.updateReview(review);
        log.debug("- updateReview : {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.UPDATE, answer.getReviewId());
        return answer;
    }

    @Override
    public void deleteReview(int id) {
        log.debug("+ deleteReview : id = {}", id);
        Review answer = reviewsStorage.deleteReview(id);
        log.debug("- deleteReview : {}", answer);
        feedStorage.addToFeedDb(answer.getUserId(), FeedEventType.REVIEW, FeedOperation.REMOVE, answer.getReviewId());
    }

    @Override
    public Review getReview(int id) {
        log.debug("+ getReview : {}", id);
        Review answer = reviewsStorage.getReviewById(id);
        log.debug("- getReview : {}", answer);
        return answer;
    }

    @Override
    public List<Review> getReviewsByFilmId(Integer id, int count) {
        log.debug("+ getReviewsByFilmId: id{}, count: {}", id, count);
        if (id == null) {
            return getAllReviews(count);
        }
        if (id == 0) {
            throw new FilmNotFoundException("Фильма с id = 0 не может быть");
        }
        filmStorage.getFilm(id);
        List<Review> reviews = reviewsStorage.getReviewsByFilmId(id, count);
        log.debug("- getReviewsByFilmId : {}", reviews);
        return reviews;
    }

    @Override
    public void addLike(int id, int userId) {
        log.debug("+ addLikeToReview: id{}, userId: {}", id, userId);
        userStorage.getUser(userId);
        reviewsStorage.getReviewById(id);
        reviewsStorage.increaseUseful(id, userId);
        log.debug("- addLikeToReview : id{}, userId: {}", id, userId);
    }

    @Override
    public void addDislike(int id, int userId) {
        log.debug("+ addDislikeToReview: id{}, userId: {}", id, userId);
        reviewsStorage.decreaseUseful(id, userId);
        log.debug("- addDislikeToReview : id{}, userId: {}", id, userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        log.debug("+ deleteLikeFromReview: id{}, userId: {}", id, userId);
        userStorage.getUser(userId);
        reviewsStorage.getReviewById(id);
        reviewsStorage.decreaseUseful(id, userId);
        log.debug("- deleteLikeFromReview : id{}, userId: {}", id, userId);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        log.debug("+ deleteDislike: id{}, userId: {}", id, userId);
        reviewsStorage.increaseUseful(id, userId);
        log.debug("- deleteDislike : id{}, userId: {}", id, userId);
    }
}
