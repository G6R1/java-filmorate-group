package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RateMpa;

import java.util.List;

@Service
public class MpaService {
    private MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public RateMpa getMpa(int id) {
        return mpaStorage.getRateMpa(id)
                .orElseThrow(() -> new NotFoundException("такого рейтинга не существует"));
    }

    public List<RateMpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }
}