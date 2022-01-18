package com.agh.leagueapp.backend.entities;

import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;

import javax.persistence.*;

@Entity
@Table(name = "tournament", schema = "public", catalog = "league_db")
public class TournamentEntity {
    private Integer tournamentId;
    private String tournamentName;
    private String comment;
    private String api_id;
    private LeagueShard region;
    private String provider_url;
    private Integer provider_id;
    private Integer team_size;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tournament_id", nullable = false)
    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }

    @Basic
    @Column(name = "tournament_name", nullable = true, length = -1)
    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    @Basic
    @Column(name = "comment", nullable = true, length = -1)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Basic
    @Column(name = "api_id", nullable = true, length = -1)
    public String getApiId() {
        return api_id;
    }

    public void setApiId(String apiId) {
        this.api_id = apiId;
    }

    @Basic
    @Column(name = "region", nullable = true, length = -1)
    public LeagueShard getRegion() {
        return region;
    }

    public void setRegion(LeagueShard region) {
        this.region = region;
    }

    public void setRegion(String region) {
        this.region = LeagueShard.valueOf(region);
    }

    @Basic
    @Column(name = "provider_url", nullable = true, length = -1)
    public String getProviderUrl() {
        return provider_url;
    }

    public void setProviderUrl(String providerUrl) {
        this.provider_url = providerUrl;
    }

    @Basic
    @Column(name = "provider_id", nullable = true, length = -1)
    public Integer getProviderId() {
        return provider_id;
    }

    public void setProviderId(Integer provider_id) {
        this.provider_id = provider_id;
    }

    @Basic
    @Column(name = "team_size", nullable = true, length = -1)
    public Integer getTeamSize() {
        return team_size;
    }

    public void setTeamSize(Integer team_size) {
        this.team_size = team_size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TournamentEntity that = (TournamentEntity) o;

        if (tournamentId != null ? !tournamentId.equals(that.tournamentId) : that.tournamentId != null) return false;
        if (tournamentName != null ? !tournamentName.equals(that.tournamentName) : that.tournamentName != null)
            return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = tournamentId != null ? tournamentId.hashCode() : 0;
        result = 31 * result + (tournamentName != null ? tournamentName.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
