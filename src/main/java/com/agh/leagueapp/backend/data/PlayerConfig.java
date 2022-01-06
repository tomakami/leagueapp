package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.utils.LeagueAppConst;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.raw.SummonerAPI;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public class PlayerConfig {

    public static PlayerEntity InitializeInAPI (PlayerEntity entity, LeagueShard shard){

        SummonerAPI summonerAPI = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getSummonerAPI();

        Summoner summoner = summonerAPI.getSummonerByName(shard, entity.getSummonerName());

        entity.setPuuid(summoner.getPUUID());
        entity.setAccountId(summoner.getAccountId());
        entity.setSummonerId(summoner.getSummonerId());

        return entity;
    }
}
