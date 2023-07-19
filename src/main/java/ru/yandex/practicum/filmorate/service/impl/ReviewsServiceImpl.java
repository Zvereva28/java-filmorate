package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;
import ru.yandex.practicum.filmorate.storage.ReviewsStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewsServiceImpl implements ReviewsService {
    private final ReviewsStorage reviewsStorage;

    public ReviewsServiceImpl(ReviewsStorage reviewsStorage) {
        this.reviewsStorage = reviewsStorage;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        log.info("getAllReviews");
        List<Review> answer = reviewsStorage.getAllReviews(count);
        log.info("all reviews: {}", answer);
        return answer;
    }

    @Override
    public Review addReview(Review review) {
        log.info("+ addReview: {}", review);
        Review answer = reviewsStorage.addReview(review);
        log.info("created review: {}", answer);
        return answer;
    }

    @Override
    public Review updateReview(Review review) {
        log.info("updateReview: {}", review);
        Review answer = reviewsStorage.updateReview(review);
        log.info("updated review: {}", answer);
        return answer;
    }

    @Override
    public Review deleteReview(int id) {
        log.info("-deleteReview id: {}", id);
        Review answer = reviewsStorage.deleteReview(id);
        log.info("deleted review: {}", answer);
        return answer;
    }

    @Override
    public Review getReviewById(int id) {
        log.info("+ getReviewById: {}", id);
        Review answer = reviewsStorage.getReviewById(id);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {
        log.info("+ getReviewsByFilmId: id{}, count: {}", id, count);
        List<Review> answer = reviewsStorage.getReviewsByFilmId(id, count);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }

    @Override
    public Review addLikeToReview(int id, int userId) {
        log.info("+ addLikeToReview: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.increaseUseful(id, userId);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }

    @Override
    public Review addDislikeToReview(int id, int userId) {
        log.info("+ addDislikeToReview: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.decreaseUseful(id, userId);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }

    @Override
    public Review deleteLikeFromReview(int id, int userId) {
        log.info("- deleteLikeFromReview: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.decreaseUseful(id, userId);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }

    @Override
    public Review deleteDislikeFromReview(int id, int userId) {
        log.info("- deleteDislikeFromReview: id{}, userId: {}", id, userId);
        Review answer = reviewsStorage.increaseUseful(id, userId);
        log.info("review id: {}: {}", id, answer);
        return answer;
    }
}
