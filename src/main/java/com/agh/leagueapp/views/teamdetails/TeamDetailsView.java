package com.agh.leagueapp.views.teamdetails;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.players.PlayerDetails;
import com.agh.leagueapp.views.teams.AllTeamsView;
import com.agh.leagueapp.views.teams.TeamDetails;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;
import com.vaadin.flow.router.*;

import java.util.Optional;

@PageTitle("Team List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/teams", layout = MainLayout.class)
public class TeamDetailsView
        extends HorizontalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    private TournamentEntity tournamentEntity;
    private TeamEntity teamEntity = null;

    private String tournamentID;
    private final VerticalLayout listLayout, detailsLayout;
    private final Grid<TeamEntity> teamGrid = new Grid<>(TeamEntity.class, false);
    private final Grid<PlayerEntity> playerGrid = new Grid<>(PlayerEntity.class, false);
    private final Grid<GameEntity> gameGrid = new Grid<>(GameEntity.class, false);


    public TeamDetailsView(DbService dbService){
        this.dbService = dbService;
        this.listLayout = new VerticalLayout();
        this.detailsLayout = new VerticalLayout();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean forwarded = false;
        Optional<String> parameter = event.getRouteParameters().get("tournamentID");

        if(parameter.isEmpty()) {
            forwarded = true;
            tournamentID = Navigator.getTournamentID().toString();
            event.forwardTo(TeamDetailsView.class,
                    new RouteParameters("tournamentID", tournamentID));
        }
        else
            tournamentID = parameter.get();

        if(!dbService.getTournamentRepository().existsById(Integer.valueOf(tournamentID))) {
            event.forwardTo(AllTeamsView.class);
        }
        else if (!forwarded){
            tournamentEntity = dbService.getTournamentRepository().findById(Integer.valueOf(tournamentID)).orElseThrow();
            Navigator.setTournamentID(Integer.valueOf(tournamentID));
            setupOverview();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        this.removeAll();
        listLayout.removeAll();
        detailsLayout.removeAll();
    }

    private void setupOverview(){
        this.setSizeFull();

        setupListLayout();
        setupDetailsLayout();

        this.add(listLayout, detailsLayout);
    }

    private void setupListLayout(){
        listLayout.setWidth("40%");
        listLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        listLayout.getStyle().set("border", "4px dotted blue");
        listLayout.removeAll();

        setupTeamButtonPanel();
        setupTeamGrid();
    }

    private void setupTeamButtonPanel(){
        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.getStyle().set("border", "4px dotted red");
        buttonPanel.setJustifyContentMode(JustifyContentMode.END);
        buttonPanel.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        buttonPanel.setWidth("80%");

        Button refresh = new Button();
        {
            refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
            refresh.setIcon(VaadinIcon.REFRESH.create());

            refresh.addClickListener(buttonClickEvent ->
                    teamGrid.setDataProvider(new ListDataProvider<>(
                            dbService.getTeamRepository()
                                    .findAllByTournamentId(tournamentEntity.getTournamentId()))));
        }

        Button newTeam = new Button();
        {
            newTeam.addThemeVariants(ButtonVariant.LUMO_LARGE);
            newTeam.setIcon(VaadinIcon.PLUS.create());
            newTeam.addClickListener(click -> {
                TeamEntity team = new TeamEntity();
                team.setTournamentId(Integer.valueOf(tournamentID));
                TeamDetails details = new TeamDetails(dbService.getTournamentRepository(), dbService.getTeamRepository(), team);
                Dialog dialog = details.getDialog();
                dialog.addOpenedChangeListener(change -> {
                    if (!change.isOpened()) refresh.click();
                });
                dialog.setWidth("40%");
                add(dialog);
                dialog.open();
            });
        }

        Button edit = new Button();
        {
            edit.addThemeVariants(ButtonVariant.LUMO_LARGE);
            edit.setIcon(VaadinIcon.WRENCH.create());
            edit.setEnabled(false);

            teamGrid.addSelectionListener((SelectionListener<Grid<TeamEntity>, TeamEntity>)
                    selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

            edit.addClickListener(buttonClickEvent -> {
                        TeamDetails details = new TeamDetails(dbService.getTournamentRepository(), dbService.getTeamRepository(),
                                teamGrid.getSelectedItems().stream().findFirst().orElse(new TeamEntity()));
                        Dialog dialog = details.getDialog();
                        dialog.addOpenedChangeListener(change -> {
                            if (!change.isOpened()) refresh.click();
                        });
                        dialog.setWidth("40%");
                        add(dialog);
                        dialog.open();
                    }
            );
        }

        Button delete = new Button();
        {
            delete.addThemeVariants(ButtonVariant.LUMO_LARGE);
            delete.setIcon(VaadinIcon.TRASH.create());
            delete.setEnabled(false);

            teamGrid.addSelectionListener((SelectionListener<Grid<TeamEntity>, TeamEntity>)
                    selectionEvent -> delete.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

            delete.addClickListener(buttonClickEvent -> {
                try {
                    dbService.getTeamRepository().delete(
                            teamGrid.getSelectedItems().stream().findFirst().orElseThrow());
                    refresh.click();
                }catch (Exception e){
                    Notification.show("Failed to delete team from database.", 4000, Notification.Position.MIDDLE);
                }
            }
            );
        }

        buttonPanel.add(newTeam, edit, delete, refresh);
        listLayout.add(buttonPanel);
    }

    private void setupTeamGrid(){
        teamGrid.setWidth("80%");
        teamGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        teamGrid.addColumn(TeamEntity::getTeamTag).setHeader(" Tag")
                .setWidth("5em").setFlexGrow(0);
        teamGrid.addColumn(TeamEntity::getTeamName).setHeader("Team Name")
                .setAutoWidth(true);
        teamGrid.addColumn(TeamEntity::getMailAddress).setHeader("Email")
                .setAutoWidth(true);

        teamGrid.addColumn(
                        new ComponentRenderer<>(Paragraph::new, (p, team) -> {
                            String temp;
                            try{
                                temp = String.valueOf(dbService.getPlayerRepository().countPlayerEntitiesByTeamId(team.getTeamId()));
                            }catch(Exception e){
                                temp = "";
                            }
                            p.getStyle().set("text-align","center");
                            p.setText(temp);

                        }
                        )).setHeader("Players")
                .setWidth("5em").setFlexGrow(0);

        teamGrid.addColumn(
                        new ComponentRenderer<>(Paragraph::new, (p, team) -> {
                            Button details = new Button();
                            details.setIcon(VaadinIcon.PLAY.create());
                            details.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                            details.addClickListener(click -> {
                                this.teamEntity = team;
                                setupDetailsLayout();
                            });

                            p.add(details);
                            p.getStyle().set("text-align","center");
                        }
                        )).setHeader("Details")
                .setWidth("5em").setFlexGrow(0);

        teamGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        teamGrid.setDataProvider(new ListDataProvider<>(
                dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())));

        listLayout.add(teamGrid);
        listLayout.setHorizontalComponentAlignment(Alignment.CENTER, teamGrid);
    }

    private void setupDetailsLayout(){
        detailsLayout.removeAll();
        playerGrid.removeAllColumns();
        gameGrid.removeAllColumns();
        detailsLayout.setWidth("60%");
        detailsLayout.getStyle().set("border", "4px dotted green");

        if(this.teamEntity == null){
            detailsLayout.add(new Paragraph("haha lol empty"));
            return;
        }

        HorizontalLayout infoPanel = new HorizontalLayout();
        infoPanel.setJustifyContentMode(JustifyContentMode.EVENLY);
        infoPanel.setWidth("80%");
        infoPanel.add(
                ViewBuildUtils.headerWithContent("Tag", teamEntity.getTeamTag()),
                ViewBuildUtils.headerWithContent("Team Name", teamEntity.getTeamName()),
                ViewBuildUtils.headerWithContent("Contact Mail", teamEntity.getMailAddress()));

        HorizontalLayout listPanel = new HorizontalLayout();
        listPanel.setSizeFull();
        listPanel.getStyle().set("border","4px dotted purple");


        VerticalLayout playerList = setupPlayerList();
        playerList.getStyle().set("border","4px solid pink");
        playerList.setWidth("80%");

        VerticalLayout gameList = setupGameList();

        listPanel.add(
                playerList,
                gameList
        );


        detailsLayout.add(infoPanel, listPanel);
    }

    private VerticalLayout setupPlayerList(){
        HorizontalLayout buttonPanel = setupPlayerButtonPanel();
        setupPlayerGrid();

        return new VerticalLayout(buttonPanel, playerGrid);
    }

    private HorizontalLayout setupPlayerButtonPanel(){
        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.getStyle().set("border", "4px dotted red");
        buttonPanel.setJustifyContentMode(JustifyContentMode.END);
        buttonPanel.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        buttonPanel.setWidthFull();

        Button refresh = new Button();
        {
            refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
            refresh.setIcon(VaadinIcon.REFRESH.create());
            refresh.addClickListener(buttonClickEvent ->
                    playerGrid.setDataProvider(new ListDataProvider<>(
                            dbService.getPlayerRepository()
                                    .findPlayerEntitiesByTeamId(this.teamEntity.getTeamId()))));
        }

        Button newPlayer = new Button();
        {
            newPlayer.addThemeVariants(ButtonVariant.LUMO_LARGE);
            newPlayer.setIcon(VaadinIcon.PLUS.create());
            newPlayer.addClickListener(click -> {
                PlayerEntity player = new PlayerEntity();
                player.setTeamId(teamEntity.getTeamId());
                PlayerDetails details = new PlayerDetails(
                        dbService.getTournamentRepository(), dbService.getTeamRepository(),
                        dbService.getPlayerRepository(), player);
                Dialog dialog = details.getDialog();
                dialog.addOpenedChangeListener(change -> {
                    if (change.isOpened()) refresh.click();
                });
                dialog.setWidth("40%");
                add(dialog);
                dialog.open();
            });
        }

        Button edit = new Button();
        {
            edit.addThemeVariants(ButtonVariant.LUMO_LARGE);
            edit.setIcon(VaadinIcon.WRENCH.create());
            edit.setEnabled(false);

            playerGrid.addSelectionListener((SelectionListener<Grid<PlayerEntity>, PlayerEntity>)
                    selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

            edit.addClickListener(buttonClickEvent -> {
                        PlayerDetails details = new PlayerDetails(
                                dbService.getTournamentRepository(), dbService.getTeamRepository(), dbService.getPlayerRepository(),
                                playerGrid.getSelectedItems().stream().findFirst().orElse(new PlayerEntity()));
                        Dialog dialog = details.getDialog();
                        dialog.addOpenedChangeListener(change -> {
                            if (change.isOpened()) refresh.click();
                        });
                        dialog.setWidth("40%");
                        add(dialog);
                        dialog.open();
                    }
            );
        }

        Button delete = new Button();
        {
            delete.addThemeVariants(ButtonVariant.LUMO_LARGE);
            delete.setIcon(VaadinIcon.TRASH.create());
            delete.setEnabled(false);

            playerGrid.addSelectionListener((SelectionListener<Grid<PlayerEntity>, PlayerEntity>)
                    selectionEvent -> delete.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

            delete.addClickListener(buttonClickEvent -> {
                        try {
                            dbService.getPlayerRepository().delete(
                                    playerGrid.getSelectedItems().stream().findFirst().orElseThrow());
                            refresh.click();
                        }catch (Exception e){
                            Notification.show("Failed to delete player from database.", 4000, Notification.Position.MIDDLE);
                        }
                    }
            );
        }

        buttonPanel.add(newPlayer, edit, delete, refresh);

        return buttonPanel;
    }

    private void setupPlayerGrid(){
        playerGrid.setWidthFull();
        playerGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        playerGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        playerGrid.addColumn(
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
                .setWidth("4em").setFlexGrow(0);

        playerGrid.addColumn(PlayerEntity::getSummonerName).setHeader("Summoner Name")
                .setAutoWidth(true);

        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            String name = player.getFirstName() + " " + player.getLastName();
                            VerticalLayout template = new VerticalLayout(new H5(name), new Paragraph(player.getIndexNumber()));
                            span.add(template);
                        }
                        )).setHeader("Player")
                .setAutoWidth(true);

        playerGrid.setDataProvider(
                new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamId(
                                teamEntity.getTeamId())));

    }

    private VerticalLayout setupGameList(){
        setupGameGrid();
        return new VerticalLayout(gameGrid);
    }

    private void setupGameGrid(){
        gameGrid.setWidthFull();
        gameGrid.setSelectionMode(Grid.SelectionMode.NONE);
        gameGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            span.add(new Paragraph(game.getBlueTeamId() + " vs " + game.getRedTeamId()));
                        })).setHeader("vs")
                .setWidth("4em").setFlexGrow(0);

        gameGrid.setDataProvider(
                new ListDataProvider<>(
                        dbService.getGameRepository().
                                findAllByBlueTeamIdOrRedTeamId
                                        (teamEntity.getTeamId(), teamEntity.getTeamId())));
    }
}
