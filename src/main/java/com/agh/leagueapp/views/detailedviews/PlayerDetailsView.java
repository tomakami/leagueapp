package com.agh.leagueapp.views.detailedviews;


import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.GameGridBuilder;
import com.agh.leagueapp.utils.GridBuilders.PlayerGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.generalviews.AllPlayersView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
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
            event.forwardTo(AllPlayersView.class);
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
        listLayout.removeAll();

        List<Integer> teams = new ArrayList<>();
        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())
                .forEach(teamEntity -> teams.add(teamEntity.getTeamId()));

        final PlayerGridBuilder playerGridBuilder = new PlayerGridBuilder(dbService);
        playerGridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withRoleColumn("2em", "4em")
                .withTeamCardColumn(true, 1)
                .withSummonerNameColumn(true, 1)
                .withPlayerNameColumn(true, 1)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamIdIsIn(teams)))
                .withDataByTeamIds(teams);

        final HorizontalLayout buttonPanel = playerGridBuilder.getButtonPanel(this);
        final Grid<PlayerEntity> playerGrid = playerGridBuilder.getPlayerGrid();
        playerGrid.getStyle().set("border", "1px solid grey");

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
        playerGrid.setWidth("90%");

        listLayout.setHorizontalComponentAlignment(Alignment.CENTER, playerGrid);
        listLayout.add(buttonPanel, playerGrid);
    }
    private void setupDetailsLayout(){
        detailsLayout.removeAll();
        gameGrid.removeAllColumns();
        detailsLayout.setWidth("60%");

        if(this.playerEntity == null){
            H2 info = new H2("Choose Player to see detailed information.");
            info.setSizeFull();
            info.getStyle().set("text-align", "center");
            detailsLayout.add(info);
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

        VerticalLayout gameListLayout = setupGameList();
        gameListLayout.setWidth("80%");

        listPanel.add(
                gameListLayout
        );


        detailsLayout.add(infoPanel, listPanel);
    }

    private VerticalLayout setupGameList(){
        final GameGridBuilder gameGridBuilder = new GameGridBuilder(dbService, tournamentEntity);
        gameGridBuilder
                .withIdColumn()
                .withResultColumn(playerEntity.getTeamId())
                .withOpponentCardColumn(true, 1, playerEntity.getTeamId(), false)
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getGameRepository().
                                findAllByBlueTeamIdOrRedTeamId
                                        (playerEntity.getTeamId(), playerEntity.getTeamId())));

        final Grid<GameEntity> gameGrid = gameGridBuilder.getGameGrid();

        gameGrid.getStyle().set("border", "1px solid grey");

        return new VerticalLayout(gameGrid);
    }

}
