package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Integer> {

    long countPlayerEntitiesByTeamId(Integer teamId);

    Collection<PlayerEntity> findPlayerEntitiesByTeamId(Integer teamId);
}
