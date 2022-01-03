package com.agh.leagueapp.backend.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "team", schema = "public", catalog = "league_db")
public class TeamEntity {
    private Integer teamId;
    private String teamName;
    private String teamTag;
    private Integer tournamentId;
    private String mailAddress;
    private Collection<GameEntity> gamesByTeamId;
    private Collection<GameEntity> gamesByTeamId_0;
    private Collection<MatchEntity> matchesByTeamId;
    private Collection<MatchEntity> matchesByTeamId_0;
    private Collection<PlayerEntity> playersByTeamId;

    @Id
    @Column(name = "team_id", nullable = false)
    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Basic
    @Column(name = "team_name", nullable = false, length = -1)
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Basic
    @Column(name = "team_tag", nullable = true, length = 4)
    public String getTeamTag() {
        return teamTag;
    }

    public void setTeamTag(String teamTag) {
        this.teamTag = teamTag;
    }

    @Basic
    @Column(name = "tournament_id", nullable = true)
    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Basic
    @Column(name = "mail_address", nullable = true, length = -1)
    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TeamEntity that = (TeamEntity) o;

        if (teamId != null ? !teamId.equals(that.teamId) : that.teamId != null) return false;
        if (teamName != null ? !teamName.equals(that.teamName) : that.teamName != null) return false;
        if (teamTag != null ? !teamTag.equals(that.teamTag) : that.teamTag != null) return false;
        if (tournamentId != null ? !tournamentId.equals(that.tournamentId) : that.tournamentId != null) return false;
        if (mailAddress != null ? !mailAddress.equals(that.mailAddress) : that.mailAddress != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = teamId != null ? teamId.hashCode() : 0;
        result = 31 * result + (teamName != null ? teamName.hashCode() : 0);
        result = 31 * result + (teamTag != null ? teamTag.hashCode() : 0);
        result = 31 * result + (tournamentId != null ? tournamentId.hashCode() : 0);
        result = 31 * result + (mailAddress != null ? mailAddress.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "teamByBlueTeamId")
    public Collection<GameEntity> getGamesByTeamId() {
        return gamesByTeamId;
    }

    public void setGamesByTeamId(Collection<GameEntity> gamesByTeamId) {
        this.gamesByTeamId = gamesByTeamId;
    }

    @OneToMany(mappedBy = "teamByRedTeamId")
    public Collection<GameEntity> getGamesByTeamId_0() {
        return gamesByTeamId_0;
    }

    public void setGamesByTeamId_0(Collection<GameEntity> gamesByTeamId_0) {
        this.gamesByTeamId_0 = gamesByTeamId_0;
    }

    @OneToMany(mappedBy = "teamByTeam1Id")
    public Collection<MatchEntity> getMatchesByTeamId() {
        return matchesByTeamId;
    }

    public void setMatchesByTeamId(Collection<MatchEntity> matchesByTeamId) {
        this.matchesByTeamId = matchesByTeamId;
    }

    @OneToMany(mappedBy = "teamByTeam2Id")
    public Collection<MatchEntity> getMatchesByTeamId_0() {
        return matchesByTeamId_0;
    }

    public void setMatchesByTeamId_0(Collection<MatchEntity> matchesByTeamId_0) {
        this.matchesByTeamId_0 = matchesByTeamId_0;
    }

    @OneToMany(mappedBy = "teamByTeamId")
    public Collection<PlayerEntity> getPlayersByTeamId() {
        return playersByTeamId;
    }

    public void setPlayersByTeamId(Collection<PlayerEntity> playersByTeamId) {
        this.playersByTeamId = playersByTeamId;
    }
}
