package com.noom.interview.fullstack.sleep.service.mapper;

import com.noom.interview.fullstack.sleep.model.User;
import com.noom.interview.fullstack.sleep.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserModelMapper {

    UserEntity modelToEntity(User user);

    User entityToModel(UserEntity entity);
}
