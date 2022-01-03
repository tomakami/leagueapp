package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;

public class Tournament {

    private final TournamentEntity entity;
    private final DbService dbService;

    public Tournament(TournamentEntity entity, DbService dbService){
        this.entity = entity;
        this.dbService = dbService;
    }

    public TournamentEntity getEntity(){
        return entity;
    }
}
