package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {
    private DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director createDirector(Director director){
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director){
        if (director.getId() <= 0 & director.getName() == null) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
        return directorStorage.updateDirector(director);
    }

    public Director getDirector(int id) {
        return directorStorage.getDirector(id)
                .orElseThrow(() -> new NotFoundException("такого режиссера не существует"));
    }

    public List<Director> getAllDirectors() {
        return directorStorage.getListDirectors();
    }

    public void removeDirector(int id){
        if (id <= 0) {
            throw new NotFoundException("ID режиссера меньше или равно 0");
        }
        directorStorage.removeDirector(id);
    }
}
