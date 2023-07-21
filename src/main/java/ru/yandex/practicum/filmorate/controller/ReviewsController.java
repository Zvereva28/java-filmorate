package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewsService.addReview(review);
    }

    @GetMapping
    public List<Review> getAllReviews(@RequestParam(value = "filmId", required = false) Integer filmId,
                                      @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        if (filmId == null) {
            return reviewsService.getAllReviews(count);
        }

        if (filmId == 0) {
            throw new FilmNotFoundException("Фильма с id = 0 не может быть");
        }

        return reviewsService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewsService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public Review deleteReview(@PathVariable int id) {
        return reviewsService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewsService.getReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review deleteLike(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review deleteDislike(@PathVariable int id, @PathVariable int userId) {
        return reviewsService.deleteDislikeFromReview(id, userId);
    }
}
