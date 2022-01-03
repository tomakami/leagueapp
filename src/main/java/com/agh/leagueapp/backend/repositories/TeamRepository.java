package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Integer> {

    long countTeamEntitiesByTournamentId(Integer tournamentId);
}
