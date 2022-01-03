package com.agh.leagueapp.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.agh.leagueapp.backend.entities.GameEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameEntity, Integer> {


}
