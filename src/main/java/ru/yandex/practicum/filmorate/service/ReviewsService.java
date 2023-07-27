package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsService {
    List<Review> getAllReviews(int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(int id);

    Review getReview(int id);

    List<Review> getReviewsByFilmId(Integer id, int count);

    Review addLike(int id, int userId);

    Review addDislike(int id, int userId);

    Review deleteLike(int id, int userId);

    Review deleteDislike(int id, int userId);
}
