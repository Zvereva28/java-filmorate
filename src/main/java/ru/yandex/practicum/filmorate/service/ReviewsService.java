package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsService {
    List<Review> getAllReviews(int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(int id);

    Review getReviewById(int id);

    List<Review> getReviewsByFilmId(int id,  int count);

    Review addLikeToReview(int id, int userId);

    Review addDislikeToReview(int id, int userId);

    Review deleteLikeFromReview(int id, int userId);

    Review deleteDislikeFromReview(int id, int userId);
}
