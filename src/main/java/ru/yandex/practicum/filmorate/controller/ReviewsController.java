package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.dto.ReviewsDTO;
import ru.yandex.practicum.filmorate.model.mappers.FilmorateMapper;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;
    private final FilmorateMapper mapper = Mappers.getMapper(FilmorateMapper.class);

    @PostMapping
    public ReviewsDTO addReview(@Valid @RequestBody ReviewsDTO reviewsDTO) {
        return mapper.reviewToDto(reviewsService.addReview(mapper.dtoToReview(reviewsDTO)));
    }

    @GetMapping
    public List<ReviewsDTO> getAllReviews(@RequestParam(value = "filmId", required = false) Integer filmId,
                                      @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {

        return reviewsService.getReviewsByFilmId(filmId, count)
                .stream()
                .map(mapper::reviewToDto)
                .collect(toList());
    }

    @PutMapping
    public ReviewsDTO updateReview(@Valid @RequestBody ReviewsDTO reviewsDTO) {
        return mapper.reviewToDto(reviewsService.updateReview(mapper.dtoToReview(reviewsDTO)));
    }

    @DeleteMapping("/{id}")
    public ReviewsDTO deleteReview(@PathVariable int id) {
        return mapper.reviewToDto(reviewsService.deleteReview(id));
    }

    @GetMapping("/{id}")
    public ReviewsDTO getReview(@PathVariable int id) {
        return mapper.reviewToDto(reviewsService.getReview(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewsDTO addLike(@PathVariable int id, @PathVariable int userId) {
        return mapper.reviewToDto(reviewsService.addLike(id, userId));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewsDTO addDislike(@PathVariable int id, @PathVariable int userId) {
        return mapper.reviewToDto(reviewsService.addDislike(id, userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewsDTO deleteLike(@PathVariable int id, @PathVariable int userId) {
        return mapper.reviewToDto(reviewsService.deleteLike(id, userId));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewsDTO deleteDislike(@PathVariable int id, @PathVariable int userId) {
        return mapper.reviewToDto(reviewsService.deleteDislike(id, userId));
    }
}
