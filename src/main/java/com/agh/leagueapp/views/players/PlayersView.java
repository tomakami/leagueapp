package com.agh.leagueapp.views.players;


import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.tournaments.TournamentDetails;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Player List")
@Route(value = LeagueAppConst.PAGE_PLAYERS, layout = MainLayout.class)
public class PlayersView
        extends VerticalLayout {

    private final DbService dbService;
    private final Grid<PlayerEntity> grid;

    public PlayersView(DbService dbService) {
        this.dbService = dbService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of LeagueTournamentApp Players"));
        add(new Paragraph("Click + icon to add new player."));

        HorizontalLayout buttonPanel = new HorizontalLayout(
                AddNewPlayerButton(),
                AddRefreshButton());
        buttonPanel.setWidth("45%");
        buttonPanel.setHeight("10%");
        buttonPanel.setPadding(false);
        add(buttonPanel);

        grid = new Grid<>(PlayerEntity.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setWidth("60%");

        grid.addColumn(PlayerEntity::getPlayerId).setHeader("ID")
                .setWidth("3em").setFlexGrow(0);

        grid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            String position = player.getPosition();
                            Image roleIcon;
                            if(position == null) position="";
                            switch (position){
                                case "Top":
                                    roleIcon = LeagueAppConst.TOP;
                                    break;
                                case "Jungle":
                                    roleIcon = LeagueAppConst.JUNGLE;
                                    break;
                                case "Middle":
                                    roleIcon = LeagueAppConst.MIDDLE;
                                    break;
                                case "Bottom":
                                    roleIcon = LeagueAppConst.BOTTOM;
                                    break;
                                case "Support":
                                    roleIcon = LeagueAppConst.UTILITY;
                                    break;
                                case "Fill":
                                    roleIcon = LeagueAppConst.FILL;
                                    break;
                                default:
                                    roleIcon = LeagueAppConst.UNSELECTED;
                            }
                            roleIcon.setWidth("2em");
                            roleIcon.setHeight("2em");
                            span.add(roleIcon);
                        })).setHeader("Role")
                .setWidth("5em").setFlexGrow(0);

        grid.addColumn(PlayerEntity::getSummonerName).setHeader("Summoner Name")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            String name = player.getFirstName() + " " + player.getLastName();
                            VerticalLayout template = new VerticalLayout(new H5(name), new Paragraph(player.getIndexNumber()));
                            span.add(template);
                        }
                        )).setHeader("Player")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.setDataProvider(new ListDataProvider<>(FetchPlayerList()));
        setHorizontalComponentAlignment(Alignment.CENTER, grid);

        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        grid.getStyle().set("border", "4px solid black");

        add(grid);
    }

    private List<PlayerEntity> FetchPlayerList() {
        return dbService.getPlayerRepository().findAll();
    }

    private Button AddNewPlayerButton(){
        Button newPlayer = new Button();
        newPlayer.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newPlayer.setWidth("75%");
        newPlayer.setHeight("100%");
        Icon plusIcon = VaadinIcon.PLUS.create();
        newPlayer.setIcon(plusIcon);

        newPlayer.addClickListener(buttonClickEvent -> {
            PlayerDetails details = new PlayerDetails(dbService.getTournamentRepository(),
                    dbService.getTeamRepository(), dbService.getPlayerRepository(), null);
            Dialog dialog = details.getDialog();
            add(dialog);
            dialog.open();
        });

        return newPlayer;
    }

    private Button AddRefreshButton(){
        Button refresh = new Button();
        refresh.setWidth("25%");
        refresh.setHeight("100%");
        Icon refreshIcon = VaadinIcon.REFRESH.create();
        refresh.setIcon(refreshIcon);

        refresh.addClickListener(
                buttonClickEvent ->
                        grid.setDataProvider
                                (new ListDataProvider<>(FetchPlayerList())));

        return refresh;
    }
}