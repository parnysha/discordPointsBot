package org.example.springbotdiscord.repository;

import org.example.springbotdiscord.dto.Points;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PointsRepository extends CrudRepository<Points, String> {
    Points findByUserId(long userId);
    Points findByUsername(String username);
    List<Points> findAllByOrderByPointsBalanceDesc();
}
