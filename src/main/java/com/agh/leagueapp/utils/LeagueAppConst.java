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

    public static final String API_KEY = "RGAPI-fb512379-6a86-41ce-9c8c-10e45ec5c051";
    public static final boolean USE_STUB = true;

    public static final List<LeagueShard> VALID_REGIONS = Arrays.asList(
            LeagueShard.BR1, LeagueShard.EUN1, LeagueShard.EUW1, LeagueShard.JP1,
            LeagueShard.LA1, LeagueShard.LA2, LeagueShard.NA1, LeagueShard.PBE1,
            LeagueShard.RU, LeagueShard.TR1);

    public static final Image TOP = new Image("positionIcons/icon-position-top.png", "Top");
    public static final Image JUNGLE = new Image("positionIcons/icon-position-jungle.png", "Jungle");
    public static final Image MIDDLE = new Image("positionIcons/icon-position-middle.png", "Middle");
    public static final Image BOTTOM = new Image("positionIcons/icon-position-bottom.png", "Bottom");
    public static final Image UTILITY = new Image("positionIcons/icon-position-utility.png", "Support");
    public static final Image FILL = new Image("positionIcons/icon-position-fill.png", "Fill");
    public static final Image UNSELECTED = new Image("positionIcons/icon-position-unselected.png", "Unselected");
}
