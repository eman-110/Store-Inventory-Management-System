package service;

import repository.IRepository;

import java.util.List;

public abstract class Service<T, ID> {
    protected IRepository<T, ID> repository;

    public Service(IRepository<T, ID> repository) {
        this.repository = repository;
    }

    public abstract List<T> findAll();

    public abstract T findById(ID id);

    public void add(T entity) {
        repository.add(entity);
    }

    public void update(T entity) {
        repository.update(entity);
    }

    public void delete(ID id) {
        repository.delete(id);
    }

    public int getTotalCount() {
        return repository.getTotalCount();
    }
}

