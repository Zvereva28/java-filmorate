package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewsService {
    List<Review> getAllReviews(int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReview(int id);

    Review getReview(int id);

    List<Review> getReviewsByFilmId(Integer id, int count);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);
}
