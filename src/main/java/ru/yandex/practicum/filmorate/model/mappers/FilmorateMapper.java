package ru.yandex.practicum.filmorate.model.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.dto.DirectorDTO;
import ru.yandex.practicum.filmorate.model.dto.FeedEventDTO;
import ru.yandex.practicum.filmorate.model.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.dto.GenresDTO;
import ru.yandex.practicum.filmorate.model.dto.MpaDTO;
import ru.yandex.practicum.filmorate.model.dto.ReviewsDTO;
import ru.yandex.practicum.filmorate.model.dto.UsersDTO;

@Mapper
public interface FilmorateMapper {
    FilmDTO filmToDto(Film film);
    Film dtoToFilm(FilmDTO filmDTO);
    DirectorDTO directorToDto(Director director);
    Director dtoToDirector(DirectorDTO directorDTO);
    GenresDTO genresToDto(Genres genres);
    MpaDTO mpaToDto(Mpa mpa);
    ReviewsDTO reviewToDto(Review review);
    Review dtoToReview(ReviewsDTO reviewsDTO);
    UsersDTO userToDto(User user);
    User dtoToUser(UsersDTO usersDTO);
    FeedEventDTO feedEventToDto(FeedEvent feedEvent);
}
