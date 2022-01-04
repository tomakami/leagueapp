package com.agh.leagueapp.utils;

import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;

import java.util.Arrays;
import java.util.List;

public class LeagueAppConst {

    public static final String API_KEY = "RGAPI-a4a276df-9dcd-41b7-babd-27d1d92eff17";
    public static final boolean USE_STUB = true;
    public static final List<LeagueShard> validRegions = Arrays.asList(
            LeagueShard.BR1, LeagueShard.EUN1, LeagueShard.EUW1, LeagueShard.JP1,
            LeagueShard.LA1, LeagueShard.LA2, LeagueShard.NA1, LeagueShard.PBE1,
            LeagueShard.RU, LeagueShard.TR1);
}
