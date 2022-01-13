package com.agh.leagueapp.backend.repositories;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import no.stelar7.api.r4j.pojo.val.match.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Integer> {

    long countPlayerEntitiesByTeamId(Integer teamId);

    Collection<PlayerEntity> findPlayerEntitiesByTeamId(Integer teamId);

    Collection<PlayerEntity>findPlayerEntitiesByTeamIdIsIn(Collection<Integer> teamId);
}
