package com.agh.leagueapp.utils.GridBuilders;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.views.tournaments.TournamentDetails;
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

public class TournamentGridBuilder {

    private Grid<TournamentEntity> tournamentGrid;
    private final DbService dbService;

    private DataProvider<TournamentEntity, ?> dataProvider;

    public TournamentGridBuilder(DbService dbService){
        this.dbService = dbService;
        this.reset();
    }


    // General methods

    public void reset(){
        this.tournamentGrid = new Grid<>(TournamentEntity.class, false);
    }

    public void resetData(){
        tournamentGrid.setDataProvider(dataProvider);
    }

    public Grid<TournamentEntity> getTournamentGrid(){
        return tournamentGrid;
    }


    // Attributes

    public TournamentGridBuilder withSelectionMode(Grid.SelectionMode selectionMode){
        tournamentGrid.setSelectionMode(selectionMode);
        return this;
    }

    public TournamentGridBuilder withThemeVariants(GridVariant... variants){
        tournamentGrid.addThemeVariants(variants);
        return this;
    }

    public TournamentGridBuilder withDataProvider(DataProvider<TournamentEntity, ?> dataProvider){
        this.dataProvider = dataProvider;
        resetData();
        return this;
    }


    // Columns

    public TournamentGridBuilder withIdColumn(){
        tournamentGrid.addColumn(TournamentEntity::getTournamentId)
                .setHeader("ID")
                .setWidth("3em")
                .setFlexGrow(0);
        return this;
    }

    public TournamentGridBuilder withRegionColumn(){
        tournamentGrid.addColumn(TournamentEntity::getRegion)
                .setHeader("Region")
                .setWidth("6em");
        return this;
    }

    public TournamentGridBuilder withTournamentName(boolean autoWidth, int flexGrow){
        tournamentGrid.addColumn(TournamentEntity::getTournamentName)
                .setHeader("Tournament Name")
                .setAutoWidth(autoWidth)
                .setFlexGrow(flexGrow);
        return this;
    }

    public TournamentGridBuilder withDescriptionColumn(String width, int flexGrow){
        tournamentGrid.addColumn(TournamentEntity::getComment)
                .setHeader("Description")
                .setWidth(width)
                .setFlexGrow(flexGrow);
        return this;
    }

    public TournamentGridBuilder withTeamSizeColumn(){
        tournamentGrid.addColumn(TournamentEntity::getTeamSize)
                .setHeader("Team Size")
                .setWidth("5em");
        return this;
    }

    public TournamentGridBuilder withTeamCountColumn(){
        tournamentGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, tournament) -> {
                            String temp;
                            try{
                                temp = String.valueOf(dbService.getTeamRepository()
                                        .countTeamEntitiesByTournamentId(tournament.getTournamentId()));
                            }catch(Exception e){
                                temp = "";
                            }
                            span.getStyle().set("text-align","center");
                            span.setText(temp);
                        }))
                .setHeader("Teams")
                .setWidth("5em");
        return this;
    }


    // Buttons

    public Button getNewTournamentButton(HasComponents view){
        Button newTournament = new Button();

        newTournament.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newTournament.setIcon(VaadinIcon.PLUS.create());

        newTournament.addClickListener(click -> {
            TournamentDetails details = new TournamentDetails(
                    dbService.getTournamentRepository(),null);
            Dialog dialog = details.getDialog();
            dialog.addOpenedChangeListener(change -> {
                if (change.isOpened()) resetData();
            });
            dialog.setWidth("40%");
            view.add(dialog);
            dialog.open();
        });
        return newTournament;
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

        tournamentGrid.addSelectionListener((SelectionListener<Grid<TournamentEntity>, TournamentEntity>)
                selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        edit.addClickListener(buttonClickEvent -> {
            TournamentDetails details = new TournamentDetails(dbService.getTournamentRepository(),
                            tournamentGrid.getSelectedItems().stream().findFirst().orElse(new TournamentEntity()));
            Dialog dialog = details.getDialog();
            dialog.addOpenedChangeListener(change -> {
                if (!change.isOpened()) resetData();
            });
            dialog.setWidth("40%");
            view.add(dialog);
            dialog.open();
        });
        return edit;
    }

    public HorizontalLayout getButtonPanel(HasComponents view){
        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonPanel.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        buttonPanel.add(
                getNewTournamentButton(view),
                getEditButton(view),
                getRefreshButton()
        );
        return buttonPanel;
    }
}
