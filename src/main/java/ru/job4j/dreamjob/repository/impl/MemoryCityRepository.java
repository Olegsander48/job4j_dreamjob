package ru.job4j.dreamjob.repository.impl;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.repository.interfaces.CityRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MemoryCityRepository implements CityRepository {
    private final Map<Integer, City> cities = new HashMap<>();

    {
        cities.put(1, new City(1, "Москва"));
        cities.put(2, new City(2, "Санкт-Петербург"));
        cities.put(3, new City(3, "Екатеринбург"));
        cities.put(4, new City(4, "Минск"));
    }

    @Override
    public Collection<City> findAll() {
        return cities.values();
    }
}
