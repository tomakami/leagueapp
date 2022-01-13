package com.agh.leagueapp.views.playerdetails;


import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.GridBuilders.PlayerGridBuilder;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.players.PlayersView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Player List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/players", layout = MainLayout.class)
public class PlayerDetailsView
        extends HorizontalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    private TournamentEntity tournamentEntity;
    private PlayerEntity playerEntity = null;

    private String tournamentID;
    private final VerticalLayout listLayout, detailsLayout;
    private final Grid<GameEntity> gameGrid = new Grid<>(GameEntity.class, false);


    public PlayerDetailsView(DbService dbService){
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
            event.forwardTo(PlayerDetailsView.class,
                    new RouteParameters("tournamentID", tournamentID));
        }
        else
            tournamentID = parameter.get();

        if(!dbService.getTournamentRepository().existsById(Integer.valueOf(tournamentID))) {
            event.forwardTo(PlayersView.class);
        }
        else if (!forwarded){
            tournamentEntity = dbService.getTournamentRepository().findById(Integer.valueOf(tournamentID)).orElseThrow();
            Navigator.setTournamentID(Integer.valueOf(tournamentID));
            setupOverview();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        this.removeAll();
    }

    public void setupOverview(){
        this.setSizeFull();

        setupListLayout();
        setupDetailsLayout();

        this.add(listLayout, detailsLayout);
    }

    private void setupListLayout(){
        listLayout.setWidth("40%");
        listLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        listLayout.getStyle().set("border", "4px solid blue");
        listLayout.removeAll();

        List<Integer> teams = new ArrayList<>();
        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())
                .forEach(teamEntity -> teams.add(teamEntity.getTeamId()));

        final PlayerGridBuilder gridBuilder = new PlayerGridBuilder(dbService);
        gridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withRoleColumn("2em", "4em")
                .withTeamCardColumn(true, 1)
                .withSummonerNameColumn(true, 1)
                .withPlayerNameColumn(true, 1)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamIdIsIn(teams)));

        final HorizontalLayout buttonPanel = gridBuilder.getButtonPanel(this);
        final Grid<PlayerEntity> playerGrid = gridBuilder.getPlayerGrid();

        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, team) -> {
                            Button details = new Button();
                            details.setIcon(VaadinIcon.PLAY.create());
                            details.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                            details.addClickListener(click -> {
                                this.playerEntity = team;
                                setupDetailsLayout();
                            });

                            span.add(details);
                            span.getStyle().set("text-align","center");
                        }
                        )).setHeader("Details")
                .setWidth("5em").setFlexGrow(0);

        buttonPanel.setWidth("90%");
        buttonPanel.getStyle().set("border", "4px dotted red");
        playerGrid.setWidth("90%");
        playerGrid.getStyle().set("border", "4px dotted orange");

        listLayout.setHorizontalComponentAlignment(Alignment.CENTER, playerGrid);
        listLayout.add(buttonPanel, playerGrid);
    }
    private void setupDetailsLayout(){
        detailsLayout.removeAll();
        gameGrid.removeAllColumns();
        detailsLayout.setWidth("60%");
        detailsLayout.getStyle().set("border", "4px dotted green");

        if(this.playerEntity == null){
            detailsLayout.add(new Paragraph("haha lol empty"));
            return;
        }

        HorizontalLayout infoPanel = new HorizontalLayout();
        infoPanel.setJustifyContentMode(JustifyContentMode.EVENLY);
        infoPanel.setWidth("80%");
        infoPanel.add(
                ViewBuildUtils.headerWithContent("Name", playerEntity.getFirstName() + " " + playerEntity.getLastName()),
                ViewBuildUtils.headerWithContent("Team Name", dbService.getTeamRepository().findById(playerEntity.getTeamId()).get().getTeamName()),
                ViewBuildUtils.headerWithContent("Summoner Name", playerEntity.getSummonerName()),
                ViewBuildUtils.headerWithContent("Position", playerEntity.getPosition()));

        HorizontalLayout listPanel = new HorizontalLayout();
        listPanel.setSizeFull();
        listPanel.getStyle().set("border","4px dotted purple");


        VerticalLayout gameList = setupGameList();
        gameList.getStyle().set("border","4px solid pink");
        gameList.setWidth("80%");

        listPanel.add(
                gameList
        );


        detailsLayout.add(infoPanel, listPanel);
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
                                        (playerEntity.getTeamId(), playerEntity.getTeamId())));
    }

    private VerticalLayout setupGameList(){
        setupGameGrid();
        return new VerticalLayout(gameGrid);
    }

}
