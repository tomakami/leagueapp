package com.agh.leagueapp.backend;

import org.springframework.stereotype.Controller;

@Controller
public class Navigator {

    private static Integer tournamentID;

    public Navigator(){
        System.out.println("Starting navigator");
        setTournamentID(0);
    }

    public static Integer getTournamentID() {
        return tournamentID;
    }

    public static void setTournamentID(final Integer tournamentID) {
        Navigator.tournamentID =
                tournamentID < 0 ?
                        0 : tournamentID;
    }

    public static boolean isSet(){
        return tournamentID != 0;
    }

}
