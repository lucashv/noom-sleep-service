package com.noom.interview.fullstack.sleep.repository;

import java.sql.SQLException;
import java.util.Collection;

public interface RepositoryBase<T, TID> {

    T insert(T entity) throws SQLException;

    Collection<T> fetchAll() throws SQLException;
}
