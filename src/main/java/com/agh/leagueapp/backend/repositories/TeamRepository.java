package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Integer> {

    long countTeamEntitiesByTournamentId(Integer tournamentId);

    List<TeamEntity> findAllByTournamentId(Integer tournamentId);

}
