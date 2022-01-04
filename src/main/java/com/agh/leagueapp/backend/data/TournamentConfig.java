package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.utils.LeagueAppConst;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.raw.TournamentAPI;
import no.stelar7.api.r4j.pojo.lol.tournament.ProviderRegistrationParameters;
import no.stelar7.api.r4j.pojo.lol.tournament.TournamentRegistrationParameters;

public class TournamentConfig {

    public static TournamentEntity InitializeInAPI(TournamentEntity entity){

        if(entity.getTeamSize() == null) entity.setTeamSize(5);

        if(entity.getApiId() != null) return entity;

        TournamentAPI tournamentApi = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getTournamentAPI(LeagueAppConst.USE_STUB);

        String provider_url = "http://test/" + entity.getTournamentId();

        long providerID = tournamentApi.registerAsProvider(
                new ProviderRegistrationParameters(
                        entity.getRegion(),
                        provider_url)
        );

        long apiID = tournamentApi.registerTournament(
                new TournamentRegistrationParameters(
                        entity.getTournamentName(),
                        providerID
                )
        );

        if(LeagueAppConst.USE_STUB) entity.setComment("Created via Stub Tournament API \n " + entity.getComment());
        entity.setProviderUrl(provider_url);
        entity.setProviderId((int) providerID);
        entity.setApiId(String.valueOf(apiID));

        return entity;
    }
}
