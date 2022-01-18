package com.agh.leagueapp.views.detailedviews;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.data.GameConfig;
import com.agh.leagueapp.backend.data.StatGetter;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.GameGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.generalviews.AllTournamentsView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;
import no.stelar7.api.r4j.basic.constants.types.lol.LaneType;
import no.stelar7.api.r4j.basic.constants.types.lol.TeamType;
import no.stelar7.api.r4j.impl.lol.raw.DDragonAPI;
import no.stelar7.api.r4j.impl.lol.raw.ImageAPI;
import no.stelar7.api.r4j.pojo.lol.match.v5.ChampionBan;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PageTitle("Game List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/games", layout = MainLayout.class)
public class GameDetailsView
        extends HorizontalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    private TournamentEntity tournamentEntity;
    private GameEntity gameEntity = null;
    private StatGetter statGetter;

    private String tournamentID;
    private final VerticalLayout listLayout, detailsLayout;


    public GameDetailsView(DbService dbService){
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
            event.forwardTo(GameDetailsView.class,
                    new RouteParameters("tournamentID", tournamentID));
        }
        else
            tournamentID = parameter.get();

        if(!dbService.getTournamentRepository().existsById(Integer.valueOf(tournamentID))) {
            event.forwardTo(AllTournamentsView.class);
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

    private void setupListLayout() {
        listLayout.setWidth("40%");
        listLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        listLayout.removeAll();

        GameGridBuilder gameGridBuilder = new GameGridBuilder(dbService, tournamentEntity);

        gameGridBuilder
                .withIdColumn()
                .withBlueTeamCardColumn(true, 1, true)
                .withWinnerTagColumn()
                .withRedTeamCardColumn(true, 1, true)
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(dbService.getGameRepository().findAllById(findGameIds())))
                .withDataByTeamIds(findTeamIds());

        final HorizontalLayout buttonPanel = gameGridBuilder.getButtonPanel(this);
        final Grid<GameEntity> gameGrid = gameGridBuilder.getGameGrid();

        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Button details = new Button();
                            details.setIcon(VaadinIcon.PLAY.create());
                            details.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                            details.addClickListener(click -> {
                                this.gameEntity = game;
                                setupDetailsLayout();
                            });

                            span.add(details);
                            span.getStyle().set("text-align","center");

                            if(!game.getEnded()){
                                Button simulate = new Button();
                                simulate.setIcon(VaadinIcon.GAMEPAD.create());
                                simulate.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                                simulate.addClickListener(click -> {
                                    gameEntity = GameConfig.SimulateResult(gameEntity, tournamentEntity.getRegion());
                                    try{
                                        dbService.getGameRepository().save(gameEntity);
                                    }catch(Exception e){
                                        com.vaadin.flow.component.notification.Notification.show("Error occurred during saving to database.", 5000, Notification.Position.MIDDLE).open();
                                    }
                                });
                                span.add(simulate);
                            }
                        }
                        )).setHeader("Details")
                .setWidth("5em").setFlexGrow(0);

        buttonPanel.setWidth("90%");
        gameGrid.setWidth("90%");
        gameGrid.getStyle().set("border", "1px solid gray");

        listLayout.setHorizontalComponentAlignment(Alignment.CENTER, gameGrid);
        listLayout.add(buttonPanel, gameGrid);
    }
    private void setupDetailsLayout() {
        detailsLayout.removeAll();
        detailsLayout.setWidth("60%");

        if (this.gameEntity == null){
            H2 info = new H2("Choose Game to see detailed information.");
            info.setSizeFull();
            info.getStyle().set("text-align", "center");
            detailsLayout.add(info);
            return;
        }


        HorizontalLayout infoPanel = new HorizontalLayout();
        infoPanel.setJustifyContentMode(JustifyContentMode.EVENLY);
        infoPanel.setWidth("80%");
        infoPanel.add(
                ViewBuildUtils.headerWithContent("Match ID", gameEntity.getMatchId()),
                ViewBuildUtils.headerWithContent("Tournament Code", gameEntity.getTournamentCode()),
                ViewBuildUtils.headerWithContent("Blue Team", dbService.getTeamRepository().findById(gameEntity.getBlueTeamId()).get().getTeamName()),
                ViewBuildUtils.headerWithContent("Red Team", dbService.getTeamRepository().findById(gameEntity.getRedTeamId()).get().getTeamName()));

        HorizontalLayout listPanel = new HorizontalLayout();
        listPanel.setSizeFull();
        listPanel.getStyle().set("border","1px solid gray");

        if(!this.gameEntity.getEnded()){
            H3 info = new H3("Game is scheduled. Awaiting results.");
            info.setSizeFull();
            info.getStyle().set("text-align", "center");
            listPanel.add(info);
        }
        else{
            listPanel.add(setupStats());
        }
        detailsLayout.add(infoPanel, listPanel);
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

    private VerticalLayout setupStats(){
        statGetter = new StatGetter(gameEntity.getMatchId(), tournamentEntity.getRegion());

        VerticalLayout statLayout = new VerticalLayout();
        statLayout.getStyle().set("border","1px solid grey");
        statLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        statLayout.add(
                setupHeaderStatRow(),
                setupPicksRow(),
                ViewBuildUtils.StatRow(
                        statGetter.getTeamKda(TeamType.BLUE),
                        "KDA",
                        statGetter.getTeamKda(TeamType.RED)),
                ViewBuildUtils.StatRow(
                        String.valueOf(statGetter.getTeamGold(TeamType.BLUE)),
                        "Gold",
                        String.valueOf(statGetter.getTeamGold(TeamType.RED))),
                ViewBuildUtils.StatRow(
                        String.valueOf(statGetter.getTeamTowers(TeamType.BLUE)),
                        "Turrets",
                        String.valueOf(statGetter.getTeamTowers(TeamType.RED))),
                ViewBuildUtils.StatRow(
                        String.valueOf(statGetter.getTeamDrakes(TeamType.BLUE)),
                        "Drakes",
                        String.valueOf(statGetter.getTeamDrakes(TeamType.RED))),
                ViewBuildUtils.StatRow(
                        String.valueOf(statGetter.getTeamElders(TeamType.BLUE)),
                        "Elder Dragons",
                        String.valueOf(statGetter.getTeamElders(TeamType.RED))),
                ViewBuildUtils.StatRow(
                        String.valueOf(statGetter.getTeamNashors(TeamType.BLUE)),
                        "Baron Nashors",
                        String.valueOf(statGetter.getTeamNashors(TeamType.RED))),
                setupBansRow()
        );
        return statLayout;
    }

    private HorizontalLayout setupHeaderStatRow(){
        HorizontalLayout headerStatRow = new HorizontalLayout();
        TeamEntity blueTeam = dbService.getTeamRepository().findById(gameEntity.getBlueTeamId()).get();
        TeamEntity redTeam = dbService.getTeamRepository().findById(gameEntity.getRedTeamId()).get();
        String time = statGetter.getMatch().getGameDurationAsDuration().toMinutes() + ":" +
                ((statGetter.getMatch().getGameDurationAsDuration().toSecondsPart() < 10) ?
                "0" : "") + statGetter.getMatch().getGameDurationAsDuration().toSecondsPart();


        Div blue = ViewBuildUtils.headerWithContent(blueTeam.getTeamTag(), gameEntity.getBlueWin() ? "Victory" : "Defeat");
        Div red = ViewBuildUtils.headerWithContent(redTeam.getTeamTag(), !gameEntity.getBlueWin() ? "Victory" : "Defeat");
        Div pause = ViewBuildUtils.headerWithContent("vs", time);
        pause.getStyle().set("text-align", "center");

        headerStatRow.add(blue, pause, red);
        return headerStatRow;
    }

    private HorizontalLayout setupPicksRow(){
        HorizontalLayout picksRow = new HorizontalLayout();
        picksRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        picksRow.setWidthFull();
        picksRow.setJustifyContentMode(JustifyContentMode.CENTER);

        for(LaneType lane : LeagueAppConst.LANES){
            MatchParticipant participant = statGetter.getParticipantByRoleAndSide(lane, TeamType.BLUE);

            Image img = new Image(ImageAPI.getInstance().getSquare(
                    DDragonAPI.getInstance().getChampion(
                            participant.getChampionId()
                    ).getKey(), null), "");
            img.setHeight("3em");
            img.setWidth("3em");
            picksRow.add(img);
        }

        H4 title = new H4("Picks");
        title.setWidth("25%");
        title.getStyle().set("text-align", "center");
        picksRow.add(title);

        for(LaneType lane : LeagueAppConst.LANES){
            MatchParticipant participant = statGetter.getParticipantByRoleAndSide(lane, TeamType.RED);

            Image img = new Image(ImageAPI.getInstance().getSquare(
                    DDragonAPI.getInstance().getChampion(
                            participant.getChampionId()
                    ).getKey(), null), "");
            img.setHeight("3em");
            img.setWidth("3em");
            picksRow.add(img);
        }
        return picksRow;
    }

    private HorizontalLayout setupBansRow(){
        HorizontalLayout bansRow = new HorizontalLayout();
        bansRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        bansRow.setWidthFull();
        bansRow.setJustifyContentMode(JustifyContentMode.CENTER);

        for(ChampionBan ban : statGetter.getMatch().getTeams().get(0).getBans()){
            try{
                Image img = new Image(ImageAPI.getInstance().getSquare(
                        DDragonAPI.getInstance().getChampion(
                                ban.getChampionId()
                        ).getKey(), null), "");
                img.setHeight("2em");
                img.setWidth("2em");
                bansRow.add(img);
            } catch (Exception ignored) {}
        }

        H4 title = new H4("Bans");
        title.setWidth("25%");
        title.getStyle().set("text-align", "center");
        bansRow.add(title);

        for(ChampionBan ban : statGetter.getMatch().getTeams().get(1).getBans()){
            try{
                Image img = new Image(ImageAPI.getInstance().getSquare(
                        DDragonAPI.getInstance().getChampion(
                                ban.getChampionId()
                        ).getKey(), null), "");
                img.setHeight("2em");
                img.setWidth("2em");
                bansRow.add(img);
            } catch (Exception ignored) {}
        }
        return bansRow;
    }
}
