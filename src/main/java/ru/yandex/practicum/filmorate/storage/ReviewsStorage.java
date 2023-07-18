package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewsStorage {
    List<Review> getAllReviews();

    Review addReview(Review review);

    Review updateReview(Review review);

    Review deleteReview(int id);

    Review getReviewById(int id);

    List<Review> getReviewsByFilmId(int id, int count);

    Review increaseUseful(int id, int userId);

    Review decreaseUseful(int id, int userId);
}
