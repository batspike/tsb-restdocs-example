package com.samcancode.repositories;

import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.samcancode.domain.Beer;

public interface BeerRepository extends PagingAndSortingRepository<Beer, UUID> {
}