package com.samcancode.web.mappers;

import org.mapstruct.Mapper;

import com.samcancode.domain.Beer;
import com.samcancode.web.model.BeerDto;

@Mapper(uses = {DateMapper.class})
public interface BeerMapper {

    BeerDto BeerToBeerDto(Beer beer);

    Beer BeerDtoToBeer(BeerDto dto);
}