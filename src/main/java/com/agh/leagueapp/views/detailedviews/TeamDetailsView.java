package com.agh.leagueapp.views.detailedviews;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.GameGridBuilder;
import com.agh.leagueapp.utils.GridBuilders.PlayerGridBuilder;
import com.agh.leagueapp.utils.GridBuilders.TeamGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.generalviews.AllTeamsView;
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

import java.util.List;
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
        listLayout.removeAll();

        final TeamGridBuilder teamGridBuilder = new TeamGridBuilder(dbService);

        teamGridBuilder
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withTagColumn()
                .withTeamNameColumn(true, 1)
                .withMailAddress(true, 1)
                .withPlayerCountColumn()
                .withDataProvider(new ListDataProvider<>(
                        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())))
                .withDataByTournamentId(List.of(this.tournamentEntity.getTournamentId()));

        final HorizontalLayout buttonPanel = teamGridBuilder.getButtonPanel(this);
        final Grid<TeamEntity> teamGrid = teamGridBuilder.getTeamGrid();

        teamGrid.addColumn(
                new ComponentRenderer<>(Span::new, (span, team) -> {
                    Button details = new Button();
                    details.setIcon(VaadinIcon.PLAY.create());
                    details.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                    details.addClickListener(click -> {
                        this.teamEntity = team;
                        setupDetailsLayout();
                    });

                    span.add(details);
                    span.getStyle().set("text-align","center");
                }
                )).setHeader("Details")
                .setWidth("5em").setFlexGrow(0);

        buttonPanel.setWidth("80%");
        teamGrid.setWidth("80%");
        teamGrid.getStyle().set("border", "1px solid grey");

        listLayout.setHorizontalComponentAlignment(Alignment.CENTER, teamGrid);
        listLayout.add(buttonPanel, teamGrid);
    }
    private void setupDetailsLayout(){
        detailsLayout.removeAll();
        gameGrid.removeAllColumns();
        detailsLayout.setWidth("60%");

        if(this.teamEntity == null){
            H2 info = new H2("Choose Team to see detailed information.");
            info.setSizeFull();
            info.getStyle().set("text-align", "center");
            detailsLayout.add(info);
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

        VerticalLayout playerListLayout = setupPlayerList();
        playerListLayout.setWidth("80%");

        VerticalLayout gameListLayout = setupGameList();
        gameListLayout.setWidthFull();

        listPanel.add(
                playerListLayout,
                gameListLayout
        );


        detailsLayout.add(infoPanel, listPanel);
    }

    private VerticalLayout setupPlayerList(){
        final PlayerGridBuilder gridBuilder = new PlayerGridBuilder(dbService);
        gridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withRoleColumn("2em", "4em")
                .withSummonerNameColumn(true, 1)
                .withPlayerNameColumn(true, 1)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamId(
                                teamEntity.getTeamId())));

        final HorizontalLayout buttonPanel = gridBuilder.getButtonPanel(this);
        final Grid<PlayerEntity> playerGrid = gridBuilder.getPlayerGrid();

        buttonPanel.setWidthFull();
        playerGrid.setWidthFull();
        playerGrid.getStyle().set("border", "1px solid grey");

        return new VerticalLayout(buttonPanel, playerGrid);
    }

    private VerticalLayout setupGameList(){
        final GameGridBuilder gameGridBuilder = new GameGridBuilder(dbService, tournamentEntity);
        gameGridBuilder
                .withStatusColumn()
                .withResultColumn(teamEntity.getTeamId())
                .withOpponentCardColumn(true, 1, teamEntity.getTeamId(), true)
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getGameRepository().
                                findAllByBlueTeamIdOrRedTeamId
                                        (teamEntity.getTeamId(), teamEntity.getTeamId())));

        final Grid<GameEntity> gameGrid = gameGridBuilder.getGameGrid();
        gameGrid.getStyle().set("border", "1px solid grey");

        return new VerticalLayout(gameGrid);
    }
}