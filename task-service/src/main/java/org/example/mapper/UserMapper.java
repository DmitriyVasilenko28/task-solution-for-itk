package org.example.mapper;

import org.example.dto.ShortUserDto;
import org.example.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    ShortUserDto mapUserToShortUserDto(User user);
}
