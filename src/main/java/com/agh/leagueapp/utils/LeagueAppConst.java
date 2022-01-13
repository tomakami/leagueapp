package com.agh.leagueapp.utils;

import com.vaadin.flow.component.html.Image;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;

import java.util.Arrays;
import java.util.List;

public class LeagueAppConst {

    public static final String TOURNAMENT_ID = "tournamentID";

    public static final String PAGE_ROOT = "";

    public static final String PAGE_TOURNAMENTS = "tournaments";
    public static final String PAGE_TEAMS = "teams";
    public static final String PAGE_PLAYERS = "players";

    public static final String API_KEY = "RGAPI-946a1856-017e-4d33-835e-09198fd4e4bb";
    public static final boolean USE_STUB = true;

    public static final List<LeagueShard> VALID_REGIONS = Arrays.asList(
            LeagueShard.BR1, LeagueShard.EUN1, LeagueShard.EUW1, LeagueShard.JP1,
            LeagueShard.LA1, LeagueShard.LA2, LeagueShard.NA1, LeagueShard.PBE1,
            LeagueShard.RU, LeagueShard.TR1);

    public static final String TOP = "positionIcons/icon-position-top.png";
    public static final String JUNGLE = "positionIcons/icon-position-jungle.png";
    public static final String MIDDLE = "positionIcons/icon-position-middle.png";
    public static final String BOTTOM = "positionIcons/icon-position-bottom.png";
    public static final String UTILITY = "positionIcons/icon-position-utility.png";
    public static final String FILL = "positionIcons/icon-position-fill.png";
    public static final String UNSELECTED = "positionIcons/icon-position-unselected.png";
}
