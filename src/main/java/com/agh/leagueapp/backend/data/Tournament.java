package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.pojo.lol.tournament.TournamentRegistrationParameters;

public class Tournament {

    private final TournamentEntity entity;

    public Tournament(TournamentEntity entity, DbService dbService){
        this.entity = entity;
    }

    public TournamentEntity getEntity(){
        return entity;
    }

    private void InitializeInAPI(TournamentEntity tournamentEntity){
        var tournament = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getTournamentAPI(true).registerTournament(new TournamentRegistrationParameters("sssss", 423));
    }
}
