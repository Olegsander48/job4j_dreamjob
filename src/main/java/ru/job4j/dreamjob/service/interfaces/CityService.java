package ru.job4j.dreamjob.service.interfaces;

import ru.job4j.dreamjob.model.City;

import java.util.Collection;

public interface CityService {
    Collection<City> findAll();
}
