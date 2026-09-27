package com.noom.interview.fullstack.sleep.repository;

import java.util.Collection;

public interface RepositoryBase<T> {

    T insert(T entity);

    Collection<T> fetchAll();
}
