package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.TestUtils;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.constants.types.lol.*;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.raw.MatchV5API;
import no.stelar7.api.r4j.impl.lol.raw.TournamentAPI;
import no.stelar7.api.r4j.pojo.lol.tournament.TournamentCodeParameters;
import no.stelar7.api.r4j.pojo.lol.tournament.TournamentCodeUpdateParameters;

public class GameConfig {

    public static GameEntity InitializeInAPI (GameEntity entity,
                                              boolean test,
                                              TournamentEntity tournament,
                                              TeamEntity blueTeam,
                                              TeamEntity redTeam){

        MatchV5API matchAPI = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getMatchAPI();
        TournamentAPI tournamentAPI = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getTournamentAPI(LeagueAppConst.USE_STUB);

        TournamentCodeParameters params = new TournamentCodeParameters(
                new TournamentCodeUpdateParameters(
                        null,
                        TournamentMapType.SUMMONERS_RIFT,
                        TournamentPickType.TOURNAMENT_DRAFT,
                        TournamentSpectatorType.ALL
                ),"", tournament.getTeamSize()
        );

        String tournamentCode;
        if(!test)
            tournamentCode = tournamentAPI.generateTournamentCodes
                (params, Long.parseLong(tournament.getApiId()), 1).get(0);
        else
            tournamentCode = TestUtils.GenerateRandomTournamentCode(tournament.getRegion().getValue());

        entity.setBlueTeamId(blueTeam.getTeamId());
        entity.setRedTeamId(redTeam.getTeamId());
        entity.setTournamentCode(tournamentCode);

        if (test) entity.setMatchId(TestUtils.GenerateRandomMatchId());
        else entity.setMatchId(String.valueOf(
                tournamentAPI.getMatchIds(tournament.getRegion(), tournamentCode).get(0)));

        return entity;
    }

}
