package com.agh.leagueapp.backend.repositories;

import org.springframework.stereotype.Service;

@Service
public class DbService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;

    public DbService(GameRepository gameRepository,
                     PlayerRepository playerRepository,
                     TeamRepository teamRepository,
                     TournamentRepository tournamentRepository) {

        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public GameRepository getGameRepository() {
        return gameRepository;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public TeamRepository getTeamRepository() {
        return teamRepository;
    }

    public TournamentRepository getTournamentRepository() {
        return tournamentRepository;
    }
}
