package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.GameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Integer> {

    long countGameEntitiesByBlueTeamId(Integer teamId);

    long countGameEntitiesByRedTeamId(Integer teamId);

    List<GameEntity> findAllByBlueTeamIdOrRedTeamId(Integer blueId, Integer redId);

}
