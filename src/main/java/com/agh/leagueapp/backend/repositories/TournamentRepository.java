package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Integer> {
}
