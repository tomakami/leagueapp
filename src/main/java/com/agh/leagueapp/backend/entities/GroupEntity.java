package com.agh.leagueapp.backend.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "group", schema = "public", catalog = "league_db")
public class GroupEntity {
    private Integer groupId;
    private String groupName;
    private Integer tournamentId;
    private TournamentEntity tournamentByTournamentId;
    private Collection<MatchEntity> matchesByGroupId;

    @Id
    @Column(name = "group_id", nullable = false)
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "group_name", nullable = true, length = -1)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Basic
    @Column(name = "tournament_id", nullable = true)
    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupEntity that = (GroupEntity) o;

        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) return false;
        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null) return false;
        if (tournamentId != null ? !tournamentId.equals(that.tournamentId) : that.tournamentId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (tournamentId != null ? tournamentId.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "tournament_id", referencedColumnName = "tournament_id", insertable = false, updatable = false)
    public TournamentEntity getTournamentByTournamentId() {
        return tournamentByTournamentId;
    }

    public void setTournamentByTournamentId(TournamentEntity tournamentByTournamentId) {
        this.tournamentByTournamentId = tournamentByTournamentId;
    }

    @OneToMany(mappedBy = "groupByGroupId")
    public Collection<MatchEntity> getMatchesByGroupId() {
        return matchesByGroupId;
    }

    public void setMatchesByGroupId(Collection<MatchEntity> matchesByGroupId) {
        this.matchesByGroupId = matchesByGroupId;
    }
}
