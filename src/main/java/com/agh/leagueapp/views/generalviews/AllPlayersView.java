package com.agh.leagueapp.views.generalviews;


import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.PlayerGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Player List")
@Route(value = LeagueAppConst.PAGE_PLAYERS, layout = MainLayout.class)
public class AllPlayersView
        extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    public AllPlayersView(DbService dbService) {
        this.dbService = dbService;

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        setupView();
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        this.removeAll();
    }

    private void setupView(){

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of all registered players"));

        PlayerGridBuilder playerGridBuilder = new PlayerGridBuilder(dbService);
        List<Integer> teamIds = new ArrayList<>();
        dbService.getTeamRepository().findAll().forEach(team -> teamIds.add(team.getTeamId()));

        playerGridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withIdColumn()
                .withRoleColumn("2em", "4em")
                .withTeamCardColumn(true, 1)
                .withSummonerNameColumn(true, 1)
                .withPlayerNameColumn(true, 1)
                .withDataProvider(new ListDataProvider<>(dbService.getPlayerRepository().findAll()))
                .withDataByTeamIds(teamIds);

        Grid<PlayerEntity> playerGrid = playerGridBuilder.getPlayerGrid();
        playerGrid.setWidth("45%");

        HorizontalLayout buttonPanel = playerGridBuilder.getButtonPanel(this);
        buttonPanel.setWidth("45%");
        buttonPanel.setJustifyContentMode(JustifyContentMode.END);
        buttonPanel.setPadding(false);

        add(buttonPanel, playerGrid);
        this.setHorizontalComponentAlignment(Alignment.CENTER, playerGrid);
    }
}