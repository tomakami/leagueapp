package com.agh.leagueapp.utils;

import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.constants.types.lol.GameQueueType;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchListBuilder;
import no.stelar7.api.r4j.impl.lol.raw.SummonerAPI;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {

    public static String GenerateRandomMatchId(){
        SummonerAPI summonerAPI = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getSummonerAPI();

        Summoner testSummoner =
                summonerAPI.getSummonerByName(LeagueAppConst.TEST_SUMMONER_REGION, LeagueAppConst.TEST_SUMMONER_NAME);

        MatchListBuilder matchListBuilder = testSummoner.getLeagueGames();
        matchListBuilder
                .withBeginIndex(0)
                .withQueue(GameQueueType.RANKED_SOLO_5X5)
                .withCount(LeagueAppConst.TEST_GAME_COUNT);

        int randomIndex = ThreadLocalRandom.current().nextInt(0, LeagueAppConst.TEST_GAME_COUNT + 1);

        return matchListBuilder.get().get(randomIndex);
    }

    public static String GenerateRandomTournamentCode(String region){
        long code1 = ThreadLocalRandom.current().nextInt((int) Math.pow(10, 5), (int) Math.pow(10, 6));
        long code2 = ThreadLocalRandom.current().nextInt((int) Math.pow(10, 5), (int) Math.pow(10, 6));

        return
                region + "_" + code1 + code2;
    }


}
