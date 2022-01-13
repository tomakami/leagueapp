package com.agh.leagueapp.utils.GridBuilders;

import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.views.teams.TeamDetails;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;

import java.util.Optional;


public class TeamGridBuilder {

    private Grid<TeamEntity> teamGrid;
    private final DbService dbService;

    private DataProvider<TeamEntity, ?> dataProvider;

    public TeamGridBuilder(DbService dbService){
        this.dbService = dbService;
        this.reset();
    }


    // General methods

    public void reset(){
        this.teamGrid = new Grid<>(TeamEntity.class, false);
    }

    public void resetData(){
        teamGrid.setDataProvider(dataProvider);
    }

    public Grid<TeamEntity> getTeamGrid(){
        return teamGrid;
    }

    private void refresh(){

    }


    // Attributes

    public TeamGridBuilder withSelectionMode(Grid.SelectionMode selectionMode){
        teamGrid.setSelectionMode(selectionMode);
        return this;
    }

    public TeamGridBuilder withThemeVariants(GridVariant... variants){
        teamGrid.addThemeVariants(variants);
        return this;
    }

    public TeamGridBuilder withDataProvider(DataProvider<TeamEntity, ?> dataProvider){
        this.dataProvider = dataProvider;
        resetData();
        return this;
    }


    // Columns

    public TeamGridBuilder withIdColumn(){
        teamGrid.addColumn(TeamEntity::getTeamId).setHeader("ID")
                .setWidth("3em").setFlexGrow(0);
        return this;
    }

    public TeamGridBuilder withTournamentNameColumn(boolean autoWidth, int flexGrow) {
        teamGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, team) -> {
                            Optional<TournamentEntity> entity = dbService.getTournamentRepository()
                                    .findById(team.getTournamentId());

                            span.setText(entity.isPresent() ? entity.get().getTournamentName() : "Tournament not found.");
                        }))
                .setHeader("Tournament Name")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public TeamGridBuilder withTagColumn() {
        teamGrid.addColumn(TeamEntity::getTeamTag)
                .setHeader("Tag")
                .setWidth("4em");
        return this;
    }

    public TeamGridBuilder withTeamNameColumn(boolean autoWidth, int flexGrow) {
        teamGrid.addColumn(TeamEntity::getTeamName)
                .setHeader("Team Name")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public TeamGridBuilder withMailAddress(boolean autoWidth, int flexGrow) {
        teamGrid.addColumn(TeamEntity::getMailAddress)
                .setHeader("Contact Email")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public TeamGridBuilder withPlayerCountColumn() {
        teamGrid.addColumn(
                new ComponentRenderer<>(Span::new, (span, team) -> {
                    String temp;
                    try{
                        temp = String.valueOf(dbService.getPlayerRepository().countPlayerEntitiesByTeamId(team.getTeamId()));
                    }catch(Exception e){
                        temp = "";
                    }
                    span.getStyle().set("text-align","center");
                    span.setText(temp);
                }))
                .setHeader("Players")
                .setWidth("4em");
        return this;
    }

    public TeamGridBuilder withGameCountColumn(){
        teamGrid.addColumn(
                new ComponentRenderer<>(Span::new, (span, team) -> {
                    String temp;
                    try{
                        temp = String.valueOf(
                                dbService.getGameRepository()
                                        .countGameEntitiesByBlueTeamId(team.getTeamId())
                                        + dbService.getGameRepository()
                                        .countGameEntitiesByRedTeamId(team.getTeamId()));
                    }catch(Exception e){
                        temp = "";
                    }
                    span.getStyle().set("text-align", "center");
                    span.setText(temp);
                }))
                .setHeader("Games Played")
                .setAutoWidth(true);
        return this;
    }


    // Buttons

    public Button getNewTeamButton(HasComponents view){
        Button newTeam = new Button();

        newTeam.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newTeam.setIcon(VaadinIcon.PLUS.create());

        newTeam.addClickListener(click -> {
            TeamDetails details = new TeamDetails(
                    dbService.getTournamentRepository(), dbService.getTeamRepository(),null);
            Dialog dialog = details.getDialog();
            dialog.addOpenedChangeListener(change -> {
                if (change.isOpened()) resetData();
            });
            dialog.setWidth("40%");
            view.add(dialog);
            dialog.open();
        });
        return newTeam;
    }

    public Button getRefreshButton(){
        Button refresh = new Button();

        refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
        refresh.setIcon(VaadinIcon.REFRESH.create());
        refresh.addClickListener(buttonClickEvent -> resetData());
        return refresh;
    }

    public Button getEditButton(HasComponents view){
        Button edit = new Button();

        edit.addThemeVariants(ButtonVariant.LUMO_LARGE);
        edit.setIcon(VaadinIcon.WRENCH.create());
        edit.setEnabled(false);

        teamGrid.addSelectionListener((SelectionListener<Grid<TeamEntity>, TeamEntity>)
                selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        edit.addClickListener(buttonClickEvent -> {
            TeamDetails details = new TeamDetails(
                    dbService.getTournamentRepository(), dbService.getTeamRepository(),
                    teamGrid.getSelectedItems().stream().findFirst().orElse(new TeamEntity()));
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
                getNewTeamButton(view),
                getEditButton(view),
                getRefreshButton()
        );
        return buttonPanel;
    }
}
