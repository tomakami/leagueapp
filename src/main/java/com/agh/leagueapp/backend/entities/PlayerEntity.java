package com.agh.leagueapp.backend.entities;

import javax.persistence.*;

@Entity
@Table(name = "player", schema = "public", catalog = "league_db")
public class PlayerEntity {
    private Integer playerId;
    private String firstName;
    private String lastName;
    private String indexNumber;
    private Integer teamId;
    private String puuid;
    private String summonerName;
    private String summonerId;
    private String accountId;
    private String position;
    private TeamEntity teamByTeamId;

    @Id
    @Column(name = "player_id", nullable = false)
    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    @Basic
    @Column(name = "first_name", nullable = true, length = -1)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Basic
    @Column(name = "last_name", nullable = true, length = -1)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Basic
    @Column(name = "index_number", nullable = true, length = -1)
    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    @Basic
    @Column(name = "team_id", nullable = true)
    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Basic
    @Column(name = "puuid", nullable = true, length = -1)
    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    @Basic
    @Column(name = "summoner_name", nullable = true, length = -1)
    public String getSummonerName() {
        return summonerName;
    }

    public void setSummonerName(String summonerName) {
        this.summonerName = summonerName;
    }

    @Basic
    @Column(name = "summoner_id", nullable = true, length = -1)
    public String getSummonerId() {
        return summonerId;
    }

    public void setSummonerId(String summonerId) {
        this.summonerId = summonerId;
    }

    @Basic
    @Column(name = "account_id", nullable = true, length = -1)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Basic
    @Column(name = "position", nullable = true, length = -1)
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerEntity that = (PlayerEntity) o;

        if (playerId != null ? !playerId.equals(that.playerId) : that.playerId != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (indexNumber != null ? !indexNumber.equals(that.indexNumber) : that.indexNumber != null) return false;
        if (teamId != null ? !teamId.equals(that.teamId) : that.teamId != null) return false;
        if (puuid != null ? !puuid.equals(that.puuid) : that.puuid != null) return false;
        if (summonerName != null ? !summonerName.equals(that.summonerName) : that.summonerName != null) return false;
        if (summonerId != null ? !summonerId.equals(that.summonerId) : that.summonerId != null) return false;
        if (accountId != null ? !accountId.equals(that.accountId) : that.accountId != null) return false;
        if (position != null ? !position.equals(that.position) : that.position != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playerId != null ? playerId.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (indexNumber != null ? indexNumber.hashCode() : 0);
        result = 31 * result + (teamId != null ? teamId.hashCode() : 0);
        result = 31 * result + (puuid != null ? puuid.hashCode() : 0);
        result = 31 * result + (summonerName != null ? summonerName.hashCode() : 0);
        result = 31 * result + (summonerId != null ? summonerId.hashCode() : 0);
        result = 31 * result + (accountId != null ? accountId.hashCode() : 0);
        result = 31 * result + (position != null ? position.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "team_id", insertable = false, updatable = false)
    public TeamEntity getTeamByTeamId() {
        return teamByTeamId;
    }

    public void setTeamByTeamId(TeamEntity teamByTeamId) {
        this.teamByTeamId = teamByTeamId;
    }
}
