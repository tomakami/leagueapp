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
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Tournament List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/overview", layout = MainLayout.class)
public class TournamentOverview
        extends VerticalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver{

    private String tournamentID;

    private final DbService dbService;
    private TournamentEntity tournamentEntity;

    private final Dialog details = new Dialog();

    public TournamentOverview(DbService dbService){
        this.dbService = dbService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean forwarded = false;
        Optional<String> parameter = event.getRouteParameters().get("tournamentID");

        if(parameter.isEmpty()) {
            forwarded = true;
            tournamentID = Navigator.getTournamentID().toString();
            event.forwardTo(TournamentOverview.class,
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
    public void beforeLeave(BeforeLeaveEvent event){
        this.removeAll();
    }

    private void setupOverview() {
        this.setAlignItems(Alignment.CENTER);
        this.setHeightFull();
        setupDetailsDialog();
        setupHeaderLayout();
        setupMainLayout();
    }

    private void setupHeaderLayout(){
        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(buttonClickEvent -> this.details.open());
        detailsButton.setWidth("10em");

        Span filler = new Span();
        filler.setWidth("10em");

        H2 name = new H2(tournamentEntity.getTournamentName());
        name.getStyle().set("margin", "0px");

        H4 region = new H4(tournamentEntity.getRegion().prettyName());
        region.getStyle().set("margin", "0px");

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        headerContent.setWidth("70%");
        headerContent.setPadding(false);
        headerContent.setMargin(false);
        headerContent.setSpacing(false);
        headerContent.add(
                name,
                region);

        HorizontalLayout header = new HorizontalLayout();
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        header.add(filler, headerContent, detailsButton);

        this.add(header);
    }

    private void setupMainLayout(){
        VerticalLayout leftPart = new VerticalLayout();
        leftPart.setWidthFull();
        leftPart.setHeightFull();
        leftPart.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        leftPart.add(
                setupLinkButton("See detailed team list", TeamDetailsView.class, new RouteParameters("tournamentID", tournamentID)),
                setupTeamGrid()
        );

        VerticalLayout middlePart = new VerticalLayout();
        middlePart.setWidthFull();
        middlePart.setHeightFull();
        middlePart.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        middlePart.add(
                setupLinkButton("See detailed player list", PlayerDetailsView.class, new RouteParameters("tournamentID", tournamentID)),
                setupPlayerGrid()
        );

        VerticalLayout rightPart = new VerticalLayout();
        rightPart.setWidthFull();
        rightPart.setHeightFull();
        rightPart.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        rightPart.add(
                setupLinkButton("See detailed game list", GameDetailsView.class, new RouteParameters("tournamentID", tournamentID)),
                setupGameGrid()
        );


        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();
        mainLayout.add(leftPart, middlePart, rightPart);
        this.add(mainLayout);
    }

    private Grid<TeamEntity> setupTeamGrid(){
        TeamGridBuilder teamGridBuilder = new TeamGridBuilder(dbService);

        teamGridBuilder
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withTagColumn()
                .withTeamNameColumn(true, 1)
                .withPlayerCountColumn()
                .withGameCountColumn()
                .withWinLoseColumn()
                .withDataProvider(new ListDataProvider<>(
                        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())))
                .withDataByTournamentId(List.of(tournamentEntity.getTournamentId()));

        Grid<TeamEntity> teamGrid = teamGridBuilder.getTeamGrid();
        teamGrid.getStyle().set("border", "1px solid grey");

        return teamGrid;
    }

    private Grid<PlayerEntity> setupPlayerGrid(){
        PlayerGridBuilder playerGridBuilder = new PlayerGridBuilder(dbService);

        List<Integer> teams = new ArrayList<>();
        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())
                .forEach(teamEntity -> teams.add(teamEntity.getTeamId()));

        playerGridBuilder
                .withRoleColumn("2em", "4em")
                .withTeamTagColumn()
                .withSummonerNameColumn(true, 1)
                .withPlayerNameColumn(true, 1)
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(
                        dbService.getPlayerRepository().findPlayerEntitiesByTeamIdIsIn(teams)
                ));

        Grid<PlayerEntity> playerGrid = playerGridBuilder.getPlayerGrid();
        playerGrid.getStyle().set("border", "1px solid grey");

        return playerGrid;
    }

    private Grid<GameEntity> setupGameGrid(){
        GameGridBuilder gameGridBuilder = new GameGridBuilder(dbService, tournamentEntity);

        gameGridBuilder
                .withStatusColumn()
                .withBlueTeamCardColumn(true, 1, true)
                .withWinnerTagColumn()
                .withRedTeamCardColumn(true, 1, true)
                .withSelectionMode(Grid.SelectionMode.NONE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(dbService.getGameRepository().findAllById(findGameIds())))
                .withDataByTeamIds(findTeamIds());

        Grid<GameEntity> gameGrid = gameGridBuilder.getGameGrid();
        gameGrid.getStyle().set("border", "1px solid grey");

        return gameGrid;
    }

    private RouterLink setupLinkButton(String t, Class<? extends Component> c, RouteParameters params){
        Button teamListButton = new Button(t);
        RouterLink teamListLink = new RouterLink("", c, params);
        teamListLink.add(teamListButton);

        return teamListLink;
    }

    private void setupDetailsDialog(){
        details.setMaxWidth("25%");
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        layout.add(new H3(tournamentEntity.getTournamentName()));

        layout.add(ViewBuildUtils.headerWithContent("Region", tournamentEntity.getRegion().prettyName()));
        layout.add(ViewBuildUtils.headerWithContent("Description", tournamentEntity.getComment()));

        layout.add(new HorizontalLayout(
                ViewBuildUtils.headerWithContent("ID", tournamentID), new Span(),
                ViewBuildUtils.headerWithContent("API ID", tournamentEntity.getApiId()), new Span(),
                ViewBuildUtils.headerWithContent("Team Size", tournamentEntity.getTeamSize().toString())
        ));

        layout.add(new HorizontalLayout(
                ViewBuildUtils.headerWithContent("Provider ID", tournamentEntity.getProviderId().toString()), new Span(),
                ViewBuildUtils.headerWithContent("Provider URL", tournamentEntity.getProviderUrl())
        ));

        Button close = new Button("Close");
        close.addClickListener(click -> details.close());
        layout.add(close);

        details.add(layout);
    }

    private List<Integer> findTeamIds(){
        List<Integer> teams = new ArrayList<>();
        dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())
                .forEach(teamEntity -> teams.add(teamEntity.getTeamId()));
        return teams;
    }

    private List<Integer> findGameIds(){
        List<Integer> games = new ArrayList<>();
        for (Integer id : findTeamIds())
            dbService.getGameRepository().findAllByBlueTeamIdOrRedTeamId(id, id)
                    .forEach(game -> games.add(game.getGameId()));

        return games;
    }

}
