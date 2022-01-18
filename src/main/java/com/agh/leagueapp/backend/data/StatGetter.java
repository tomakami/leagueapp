package com.agh.leagueapp.backend.data;

import com.agh.leagueapp.utils.LeagueAppConst;
import no.stelar7.api.r4j.basic.APICredentials;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.basic.constants.types.lol.*;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.lol.raw.MatchV5API;
import no.stelar7.api.r4j.pojo.lol.match.v5.*;

import java.util.List;

public class StatGetter {

    private final static MatchV5API matchAPI = new R4J(new APICredentials(LeagueAppConst.API_KEY)).getLoLAPI().getMatchAPI();

    private final LOLMatch match;
    private final LOLTimeline timeline;

    public StatGetter(String matchID, LeagueShard region){
        match = matchAPI.getMatch(region.toRegionShard(), matchID);
        timeline = matchAPI.getTimeline(region.toRegionShard(), matchID);
    }

    public MatchParticipant getParticipantByRoleAndSide(LaneType lane, TeamType teamType){
        List<MatchParticipant> participants = match.getParticipants();

        for(MatchParticipant matchParticipant : participants){
            if(matchParticipant.getTeam() == teamType && matchParticipant.getGameDeterminedPosition() == lane)
                return matchParticipant;
        }
        return null;
    }

    public int getTeamGold(TeamType teamType){
        int gold = 0;

        for(MatchParticipant participant : match.getParticipants()){
            if (participant.getTeam() == teamType){
                gold += participant.getGoldEarned();
            }
        }
        return gold;
    }

    public String getTeamKda(TeamType teamType){
        int kills = 0, deaths = 0, assists = 0;

        for(MatchParticipant participant : match.getParticipants()){
            if (participant.getTeam() == teamType){
                kills += participant.getKills();
                deaths += participant.getDeaths();
                assists += participant.getAssists();
            }
        }
        return kills + " / " + deaths + " / " + assists;
    }

    public int getTeamTowers(TeamType teamType){
        int towers = 0;

        for(TimelineFrame frame : timeline.getFrames()){
            for (TimelineFrameEvent event : frame.getEvents()){
                if(event.getTeamId() != teamType &&
                        event.getType() == EventType.BUILDING_KILL &&
                        event.getBuildingType() == BuildingType.TOWER_BUILDING){
                    towers += 1;
                }
            }
        }
        return towers;
    }

    public int getTeamDrakes(TeamType teamType){
        int drakes = 0;

        for(TimelineFrame frame : timeline.getFrames()){
            for (TimelineFrameEvent event : frame.getEvents()){
                if(event.getType() == EventType.ELITE_MONSTER_KILL &&
                        event.getMonsterType() == MonsterType.DRAGON &&
                        event.getMonsterSubType() != MonsterSubType.ELDER_DRAGON &&
                        event.getKillerTeamId() == teamType){
                    drakes += 1;
                }
            }
        }
        return drakes;
    }

    public int getTeamElders(TeamType teamType){
        int drakes = 0;

        for(TimelineFrame frame : timeline.getFrames()){
            for (TimelineFrameEvent event : frame.getEvents()){
                if(event.getType() == EventType.ELITE_MONSTER_KILL &&
                        event.getMonsterType() == MonsterType.DRAGON &&
                        event.getMonsterSubType() == MonsterSubType.ELDER_DRAGON &&
                        event.getKillerTeamId() == teamType){
                    drakes += 1;
                }
            }
        }
        return drakes;
    }

    public int getTeamNashors(TeamType teamType){
        int nashors = 0;

        for(TimelineFrame frame : timeline.getFrames()){
            for (TimelineFrameEvent event : frame.getEvents()){
                if(event.getType() == EventType.ELITE_MONSTER_KILL &&
                        event.getMonsterType() == MonsterType.BARON_NASHOR &&
                        event.getKillerTeamId() == teamType){
                    nashors += 1;
                }
            }
        }
        return nashors;
    }

    public LOLMatch getMatch(){return match;}
}
