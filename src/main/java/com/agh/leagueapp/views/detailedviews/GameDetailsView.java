package com.agh.leagueapp.views.detailedviews;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
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
import com.vaadin.flow.component.html.H2;
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

@PageTitle("Game List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/games", layout = MainLayout.class)
public class GameDetailsView
        extends HorizontalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    private TournamentEntity tournamentEntity;
    private GameEntity gameEntity = null;

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
                .withRedTeamCardColumn(true, 1, true)
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT)
                .withDataProvider(new ListDataProvider<>(dbService.getGameRepository().findAllById(findGameIds())))
                .withDataByTeamIds(findTeamIds());

        final HorizontalLayout buttonPanel = gameGridBuilder.getButtonPanel(this);
        final Grid<GameEntity> gameGrid = gameGridBuilder.getGameGrid();

        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, team) -> {
                            Button details = new Button();
                            details.setIcon(VaadinIcon.PLAY.create());
                            details.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SUCCESS);
                            details.addClickListener(click -> {
                                this.gameEntity = team;
                                setupDetailsLayout();
                            });

                            span.add(details);
                            span.getStyle().set("text-align","center");
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

        listPanel.add(
        );

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
}
