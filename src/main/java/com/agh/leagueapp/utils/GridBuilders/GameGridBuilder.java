package com.agh.leagueapp.utils.GridBuilders;

import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.forms.GameForm;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameGridBuilder {

    private Grid<GameEntity> gameGrid;
    private List<Integer> teamIds;
    private Button refresh;

    private final DbService dbService;
    private final TournamentEntity tournament;

    public GameGridBuilder(DbService dbService, TournamentEntity tournament){
        this.dbService = dbService;
        this.tournament = tournament;
        this.reset();
    }


    // General methods

    public void reset(){
        this.gameGrid = new Grid<>(GameEntity.class, false);
        this.refresh = null;
    }

    public void resetData(){
        getRefreshButton().click();
    }

    public Grid<GameEntity> getGameGrid(){
        return gameGrid;
    }


    // Attributes

    public GameGridBuilder withSelectionMode(Grid.SelectionMode selectionMode){
        gameGrid.setSelectionMode(selectionMode);
        return this;
    }

    public GameGridBuilder withThemeVariants(GridVariant... variants){
        gameGrid.addThemeVariants(variants);
        return this;
    }

    public GameGridBuilder withDataProvider(DataProvider<GameEntity, ?> dataProvider){
        gameGrid.setDataProvider(dataProvider);
        return this;
    }

    public GameGridBuilder withDataByTeamIds(List<Integer> teamIds){
        this.teamIds = teamIds;
        return this;
    }


    // Columns

    public GameGridBuilder withIdColumn(){
        gameGrid.addColumn(GameEntity::getGameId)
                .setHeader("ID")
                .setWidth("3em")
                .setFlexGrow(0);
        return this;
    }

    public GameGridBuilder withBlueTeamCardColumn(boolean autoWidth, int flexGrow, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(game.getBlueTeamId());
                            if(teamEntity.isPresent())
                                span.add(
                                        ViewBuildUtils.headerWithContent(
                                                teamEntity.get().getTeamTag(),
                                                teamEntity.get().getTeamName()));
                            else span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        game.getBlueWin() ? "#75c78c" : "#db5e6b");
                        }
                        )).setHeader("Blue Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withRedTeamCardColumn(boolean autoWidth, int flexGrow, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(game.getRedTeamId());
                            if(teamEntity.isPresent())
                                span.add(
                                        ViewBuildUtils.headerWithContent(
                                                teamEntity.get().getTeamTag(),
                                                teamEntity.get().getTeamName()));
                            else span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        !game.getBlueWin() ? "#75c78c" : "#db5e6b");
                        }
                        )).setHeader("Red Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withBlueTeamNameColumn(boolean autoWidth, int flexGrow, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(game.getBlueTeamId());
                            if(teamEntity.isPresent())
                                span.setText(teamEntity.get().getTeamName());
                            else
                                span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        game.getBlueWin() ? "#75c78c" : "#db5e6b");
                        }
                        )).setHeader("Blue Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withRedTeamNameColumn(boolean autoWidth, int flexGrow, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(game.getRedTeamId());
                            if(teamEntity.isPresent())
                                span.setText(teamEntity.get().getTeamName());
                            else
                                span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        !game.getBlueWin() ? "#75c78c" : "#db5e6b");
                        }
                        )).setHeader("Red Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withTournamentCodeColumn(boolean autoWidth, int flexGrow){
        gameGrid.addColumn(GameEntity::getTournamentCode)
                .setHeader("Tournament Code")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withMatchIdColumn(boolean autoWidth, int flexGrow){
        gameGrid.addColumn(GameEntity::getMatchId)
                .setHeader("Match ID")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withResultColumn(Integer ownTeamId){
        gameGrid.addColumn(
                new ComponentRenderer<>(Span::new, (span, game) -> {
                    Integer blueTeamId = game.getBlueTeamId();

                    if(!game.getEnded()) span.setText("TBD");
                    else span.setText(
                            (ownTeamId.equals(blueTeamId) && game.getBlueWin())
                                    || (!ownTeamId.equals(blueTeamId) && !game.getBlueWin())
                                    ? "Win" : "Lose");


                    span.getStyle().set("background-color",
                            (ownTeamId.equals(blueTeamId) && game.getBlueWin())
                                    || (!ownTeamId.equals(blueTeamId) && !game.getBlueWin())
                                    ? "#75c78c" : "#db5e6b");
                }
                )).setHeader("Result")
                .setWidth("5em");

        return this;
    }

    public GameGridBuilder withOpponentNameColumn(boolean autoWidth, int flexGrow, Integer ownTeamId, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Integer opponentId = ownTeamId.equals(game.getBlueTeamId()) ?
                                    game.getRedTeamId() : game.getBlueTeamId();
                            Optional<TeamEntity> opponent =
                                    dbService.getTeamRepository().findById(opponentId);

                            if(opponent.isPresent())
                                span.setText(opponent.get().getTeamName());
                            else
                                span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        (game.getBlueWin() && ownTeamId.equals(game.getBlueTeamId())
                                                || (!game.getBlueWin() && ownTeamId.equals(game.getRedTeamId()))
                                                ? "#75c78c" : "#db5e6b"));
                        }
                        ))
                .setHeader("Opponent")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public GameGridBuilder withOpponentCardColumn(boolean autoWidth, int flexGrow, Integer ownTeamId, boolean withResultColor){
        gameGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, game) -> {
                            Integer opponentId = ownTeamId.equals(game.getBlueTeamId()) ?
                                    game.getRedTeamId() : game.getBlueTeamId();
                            Optional<TeamEntity> opponent =
                                    dbService.getTeamRepository().findById(opponentId);

                            if(opponent.isPresent())
                                span.add(
                                        ViewBuildUtils.headerWithContent(
                                                opponent.get().getTeamTag(),
                                                opponent.get().getTeamName()));
                            else span.setText("Team not found");

                            if(withResultColor && game.getEnded())
                                span.getStyle().set("background-color",
                                        (game.getBlueWin() && ownTeamId.equals(game.getBlueTeamId())
                                                || (!game.getBlueWin() && ownTeamId.equals(game.getRedTeamId()))
                                                ? "#75c78c" : "#db5e6b"));
                        }))
                .setHeader("Opponent")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }


    // Buttons

    public Button getNewGameButton(HasComponents view){
        Button newGame = new Button();
        newGame.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newGame.setIcon(VaadinIcon.PLUS.create());

        newGame.addClickListener(click -> {
            GameForm details = new GameForm(dbService, tournament, null);
            Dialog dialog = details.getDialog();
            dialog.addOpenedChangeListener(change -> {
                if (change.isOpened()) resetData();
            });
            dialog.setWidth("40%");
            view.add(dialog);
            dialog.open();
        });
        return newGame;
    }

    public Button getRefreshButton(){
        if(refresh != null) return refresh;

        refresh = new Button();
        refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
        refresh.setIcon(VaadinIcon.REFRESH.create());
        refresh.addClickListener(click -> {
            List<Integer> games = new ArrayList<>();
            for (Integer id : teamIds)
                dbService.getGameRepository().findAllByBlueTeamIdOrRedTeamId(id, id)
                        .forEach(game -> games.add(game.getGameId()));

            this.withDataProvider(new ListDataProvider<>(dbService.getGameRepository().findAllById(games)));
        });
        return refresh;
    }

    public Button getDeleteButton(){
        Button delete = new Button();

        delete.addThemeVariants(ButtonVariant.LUMO_LARGE);
        delete.setIcon(VaadinIcon.TRASH.create());
        delete.setEnabled(false);

        gameGrid.addSelectionListener((SelectionListener<Grid<GameEntity>, GameEntity>)
                selectionEvent -> delete.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        delete.addClickListener(buttonClickEvent -> {
                    try {
                        dbService.getGameRepository().delete(
                                gameGrid.getSelectedItems().stream().findFirst().orElseThrow());
                        resetData();
                    }catch (Exception e){
                        Notification.show("Failed to delete game from database.", 4000, Notification.Position.MIDDLE);
                    }
                }
        );
        return delete;
    }

    public Button getEditButton(HasComponents view){
        Button edit = new Button();

        edit.addThemeVariants(ButtonVariant.LUMO_LARGE);
        edit.setIcon(VaadinIcon.WRENCH.create());
        edit.setEnabled(false);

        gameGrid.addSelectionListener((SelectionListener<Grid<GameEntity>, GameEntity>)
                selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        edit.addClickListener(buttonClickEvent -> {
                    GameForm details = new GameForm(dbService, tournament,
                            gameGrid.getSelectedItems().stream().findFirst().orElse(new GameEntity()));
                    Dialog dialog = details.getDialog();
                    dialog.addOpenedChangeListener(change -> {
                        if (!change.isOpened()) resetData();
                    });
                    dialog.setWidth("40%");
                    view.add(dialog);
                    dialog.open();
                }
        );
        return edit;
    }

    public HorizontalLayout getButtonPanel(HasComponents view){
        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonPanel.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        buttonPanel.add(
                getNewGameButton(view),
                getEditButton(view),
                getDeleteButton(),
                getRefreshButton()
        );

        return buttonPanel;
    }
}
