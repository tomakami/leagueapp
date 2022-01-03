package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Integer> {
}
