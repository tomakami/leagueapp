package com.agh.leagueapp.backend.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "match", schema = "public", catalog = "league_db")
public class MatchEntity {
    private Integer matchId;
    private String matchName;
    private Integer groupId;
    private Integer team1Id;
    private Integer team2Id;
    private Integer bestOf;
    private Collection<GameEntity> gamesByMatchId;
    private GroupEntity groupByGroupId;
    private TeamEntity teamByTeam1Id;
    private TeamEntity teamByTeam2Id;

    @Id
    @Column(name = "match_id", nullable = false)
    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    @Basic
    @Column(name = "match_name", nullable = true, length = -1)
    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }

    @Basic
    @Column(name = "group_id", nullable = true)
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "team1_id", nullable = true)
    public Integer getTeam1Id() {
        return team1Id;
    }

    public void setTeam1Id(Integer team1Id) {
        this.team1Id = team1Id;
    }

    @Basic
    @Column(name = "team2_id", nullable = true)
    public Integer getTeam2Id() {
        return team2Id;
    }

    public void setTeam2Id(Integer team2Id) {
        this.team2Id = team2Id;
    }

    @Basic
    @Column(name = "best_of", nullable = true)
    public Integer getBestOf() {
        return bestOf;
    }

    public void setBestOf(Integer bestOf) {
        this.bestOf = bestOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchEntity that = (MatchEntity) o;

        if (matchId != null ? !matchId.equals(that.matchId) : that.matchId != null) return false;
        if (matchName != null ? !matchName.equals(that.matchName) : that.matchName != null) return false;
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (team1Id != null ? !team1Id.equals(that.team1Id) : that.team1Id != null) return false;
        if (team2Id != null ? !team2Id.equals(that.team2Id) : that.team2Id != null) return false;
        if (bestOf != null ? !bestOf.equals(that.bestOf) : that.bestOf != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = matchId != null ? matchId.hashCode() : 0;
        result = 31 * result + (matchName != null ? matchName.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (team1Id != null ? team1Id.hashCode() : 0);
        result = 31 * result + (team2Id != null ? team2Id.hashCode() : 0);
        result = 31 * result + (bestOf != null ? bestOf.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "matchByMatchId")
    public Collection<GameEntity> getGamesByMatchId() {
        return gamesByMatchId;
    }

    public void setGamesByMatchId(Collection<GameEntity> gamesByMatchId) {
        this.gamesByMatchId = gamesByMatchId;
    }

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "group_id", insertable = false, updatable = false)
    public GroupEntity getGroupByGroupId() {
        return groupByGroupId;
    }

    public void setGroupByGroupId(GroupEntity groupByGroupId) {
        this.groupByGroupId = groupByGroupId;
    }

    @ManyToOne
    @JoinColumn(name = "team1_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    public TeamEntity getTeamByTeam1Id() {
        return teamByTeam1Id;
    }

    public void setTeamByTeam1Id(TeamEntity teamByTeam1Id) {
        this.teamByTeam1Id = teamByTeam1Id;
    }

    @ManyToOne
    @JoinColumn(name = "team2_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    public TeamEntity getTeamByTeam2Id() {
        return teamByTeam2Id;
    }

    public void setTeamByTeam2Id(TeamEntity teamByTeam2Id) {
        this.teamByTeam2Id = teamByTeam2Id;
    }
}
