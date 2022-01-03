package com.agh.leagueapp.backend.entities;

import javax.persistence.*;

@Entity
@Table(name = "game", schema = "public", catalog = "league_db")
public class GameEntity {

    private Integer gameId;
    private Integer matchId;
    private Integer blueTeamId;
    private Integer redTeamId;
    private String tournamentCode;
    private MatchEntity matchByMatchId;
    private TeamEntity teamByBlueTeamId;
    private TeamEntity teamByRedTeamId;

    public GameEntity() {

    }

    public GameEntity(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id", nullable = false)
    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    @Basic
    @Column(name = "match_id", nullable = true)
    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    @Basic
    @Column(name = "blue_team_id", nullable = true)
    public Integer getBlueTeamId() {
        return blueTeamId;
    }

    public void setBlueTeamId(Integer blueTeamId) {
        this.blueTeamId = blueTeamId;
    }

    @Basic
    @Column(name = "red_team_id", nullable = true)
    public Integer getRedTeamId() {
        return redTeamId;
    }

    public void setRedTeamId(Integer redTeamId) {
        this.redTeamId = redTeamId;
    }

    @Basic
    @Column(name = "tournament_code", nullable = true, length = -1)
    public String getTournamentCode() {
        return tournamentCode;
    }

    public void setTournamentCode(String tournamentCode) {
        this.tournamentCode = tournamentCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameEntity that = (GameEntity) o;

        if (gameId != null ? !gameId.equals(that.gameId) : that.gameId != null) return false;
        if (matchId != null ? !matchId.equals(that.matchId) : that.matchId != null) return false;
        if (blueTeamId != null ? !blueTeamId.equals(that.blueTeamId) : that.blueTeamId != null) return false;
        if (redTeamId != null ? !redTeamId.equals(that.redTeamId) : that.redTeamId != null) return false;
        if (tournamentCode != null ? !tournamentCode.equals(that.tournamentCode) : that.tournamentCode != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gameId != null ? gameId.hashCode() : 0;
        result = 31 * result + (matchId != null ? matchId.hashCode() : 0);
        result = 31 * result + (blueTeamId != null ? blueTeamId.hashCode() : 0);
        result = 31 * result + (redTeamId != null ? redTeamId.hashCode() : 0);
        result = 31 * result + (tournamentCode != null ? tournamentCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "gameId=" + gameId +
                ", tournamentCode='" + tournamentCode + '\'' +
                '}';
    }

    @ManyToOne
    @JoinColumn(name = "match_id", referencedColumnName = "match_id", insertable = false, updatable = false)
    public MatchEntity getMatchByMatchId() {
        return matchByMatchId;
    }

    public void setMatchByMatchId(MatchEntity matchByMatchId) {
        this.matchByMatchId = matchByMatchId;
    }

    @ManyToOne
    @JoinColumn(name = "blue_team_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    public TeamEntity getTeamByBlueTeamId() {
        return teamByBlueTeamId;
    }

    public void setTeamByBlueTeamId(TeamEntity teamByBlueTeamId) {
        this.teamByBlueTeamId = teamByBlueTeamId;
    }

    @ManyToOne
    @JoinColumn(name = "red_team_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    public TeamEntity getTeamByRedTeamId() {
        return teamByRedTeamId;
    }

    public void setTeamByRedTeamId(TeamEntity teamByRedTeamId) {
        this.teamByRedTeamId = teamByRedTeamId;
    }
}
