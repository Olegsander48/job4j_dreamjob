package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer",
                "Requirements: your own computer, 20+ hours per week", true));
        save(new Vacancy(0, "Junior Java Developer",
                "Work in office with mentor", true));
        save(new Vacancy(0, "Junior+ Java Developer",
                "Work on fintech project", true));
        save(new Vacancy(0, "Middle Java Developer",
                "Requirements: 3+ years of experience", true));
        save(new Vacancy(0, "Middle+ Java Developer",
                "Requirements: experience in microservice architecture", true));
        save(new Vacancy(0, "Senior Java Developer",
                "Design and architecting project on AWS", true));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(),
                                                                vacancy.getTitle(),
                                                                vacancy.getDescription(),
                                                                vacancy.getVisible())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
