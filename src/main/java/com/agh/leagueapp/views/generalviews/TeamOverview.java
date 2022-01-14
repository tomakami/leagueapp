package com.agh.leagueapp.views.generalviews;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.PlayerGridBuilder;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.Optional;

public class TeamOverview extends VerticalLayout {

    private final TeamEntity teamEntity;
    private final DbService dbService;

    public TeamOverview(TeamEntity teamEntity, DbService dbService){
        this.teamEntity = teamEntity;
        this.dbService = dbService;

        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setContent();
    }

    private void setContent(){
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setWidth("100%");
        Optional<TournamentEntity> tournamentName = dbService.getTournamentRepository().findById(teamEntity.getTournamentId());
        this.add(new H3(tournamentName.isPresent() ? tournamentName.get().getTournamentName() : "Tournament Name not found."));

        HorizontalLayout titleRow = new HorizontalLayout();
        H3 teamName = new H3(teamEntity.getTeamName());
        teamName.getStyle().set("text-align", "center");
        H3 teamTag = new H3(teamEntity.getTeamTag());
        teamTag.getStyle().set("text-align", "center");

        titleRow.setWidth("90%");
        titleRow.addAndExpand(teamTag, teamName);

        HorizontalLayout infoRow = new HorizontalLayout();
        infoRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        infoRow.setPadding(false);
        TextField count = new TextField();
        count.setReadOnly(true);
        count.setWidth("2em");
        count.setValue(dbService.getPlayerRepository().countPlayerEntitiesByTeamId(teamEntity.getTeamId()) + " ");
        Icon icon = new Icon(VaadinIcon.USERS);
        Paragraph contactMail = new Paragraph(teamEntity.getMailAddress());
        contactMail.setTitle("Email address");
        contactMail.getStyle().set("text-align", "right");

        infoRow.add(count, icon);
        infoRow.addAndExpand(contactMail);
        infoRow.setWidth("40%");

        final PlayerGridBuilder gridBuilder = new PlayerGridBuilder(dbService);
        gridBuilder
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withIdColumn()
                .withRoleColumn("2em", "4em")
                .withSummonerNameColumn(true,1)
                .withPlayerNameColumn(true,1)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamId(
                                teamEntity.getTeamId())));

        final Grid<PlayerEntity> playerGrid = gridBuilder.getPlayerGrid();
        playerGrid.setWidth("80%");

        setHorizontalComponentAlignment(Alignment.CENTER, playerGrid);

        this.add(titleRow, infoRow, playerGrid);
    }
}
