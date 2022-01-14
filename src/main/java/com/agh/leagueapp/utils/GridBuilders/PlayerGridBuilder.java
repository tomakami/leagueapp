package com.agh.leagueapp.utils.GridBuilders;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.forms.PlayerForm;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;

import java.util.List;
import java.util.Optional;

public class PlayerGridBuilder {

    private Grid<PlayerEntity> playerGrid;
    private List<Integer> teamIds;
    private Button refresh;
    private final DbService dbService;

    public PlayerGridBuilder(DbService dbService){
        this.dbService = dbService;
        this.reset();
    }


    // General methods

    public void reset(){
        this.playerGrid = new Grid<>(PlayerEntity.class, false);
        this.refresh = null;
    }

    public void resetData(){
        getRefreshButton().click();
    }

    public Grid<PlayerEntity> getPlayerGrid(){
        return playerGrid;
    }


    // Attributes

    public PlayerGridBuilder withSelectionMode(Grid.SelectionMode selectionMode){
        playerGrid.setSelectionMode(selectionMode);
        return this;
    }

    public PlayerGridBuilder withThemeVariants(GridVariant... variants){
        playerGrid.addThemeVariants(variants);
        return this;
    }

    public PlayerGridBuilder withDataProvider(DataProvider<PlayerEntity, ?> dataProvider){
        playerGrid.setDataProvider(dataProvider);
        return this;
    }

    public PlayerGridBuilder withDataByTeamIds(List<Integer> teamIds){
        this.teamIds = teamIds;
        return this;
    }


    // Columns

    public PlayerGridBuilder withIdColumn(){
        playerGrid.addColumn(PlayerEntity::getPlayerId).setHeader("ID")
                .setWidth("3em").setFlexGrow(0);
        return this;
    }

    public PlayerGridBuilder withRoleColumn(String size, String columnWidth){
        playerGrid.addColumn(ViewBuildUtils.roleIconRenderer(size)).setHeader("Role")
                .setWidth(columnWidth).setFlexGrow(0);
        return this;
    }

    public PlayerGridBuilder withTeamCardColumn(boolean autoWidth, int flexGrow){
        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(player.getTeamId());
                            if(teamEntity.isPresent())
                                span.add(
                                        ViewBuildUtils.headerWithContent(
                                                teamEntity.get().getTeamTag(),
                                                teamEntity.get().getTeamName()));
                            else span.setText("Team not found");
                        }
                        )).setHeader("Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public PlayerGridBuilder withTeamTagColumn(){
        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(player.getTeamId());
                            span.setText(teamEntity.isPresent() ? teamEntity.get().getTeamTag() : "Null");
                        }
                        )).setHeader("Tag")
                .setAutoWidth(true);
        return this;
    }

    public PlayerGridBuilder withTeamNameColumn(boolean autoWidth, int flexGrow){
        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            Optional<TeamEntity> teamEntity = dbService.getTeamRepository().findById(player.getTeamId());
                            span.setText(teamEntity.isPresent() ? teamEntity.get().getTeamName() : "Team not found");
                        }
                        )).setHeader("Team")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public PlayerGridBuilder withSummonerNameColumn(boolean autoWidth, int flexGrow){
        playerGrid.addColumn(PlayerEntity::getSummonerName).setHeader("Summoner Name")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }

    public PlayerGridBuilder withPlayerNameColumn(boolean autoWidth, int flexGrow){
        playerGrid.addColumn(
                        new ComponentRenderer<>(Span::new, (span, player) -> {
                            String name = player.getFirstName() + " " + player.getLastName();
                            Div template = ViewBuildUtils.headerWithContent(
                                    name, "Index: " + player.getIndexNumber()
                            );
                            span.add(template);
                        }
                        )).setHeader("Player")
                .setFlexGrow(flexGrow)
                .setAutoWidth(autoWidth);
        return this;
    }


    // Buttons

    public Button getNewPlayerButton(HasComponents view){
        Button newPlayer = new Button();

         newPlayer.addThemeVariants(ButtonVariant.LUMO_LARGE);
         newPlayer.setIcon(VaadinIcon.PLUS.create());

         newPlayer.addClickListener(click -> {
             PlayerForm details = new PlayerForm(
                     dbService.getTournamentRepository(), dbService.getTeamRepository(),
                     dbService.getPlayerRepository(), null);
             Dialog dialog = details.getDialog();
             dialog.addOpenedChangeListener(change -> {
                 if (change.isOpened()) resetData();
             });
             dialog.setWidth("40%");
             view.add(dialog);
             dialog.open();
         });
         return newPlayer;
    }

    public Button getRefreshButton(){
        if(refresh != null) return refresh;

        refresh = new Button();
        refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
        refresh.setIcon(VaadinIcon.REFRESH.create());
        refresh.addClickListener(click ->{
            this.withDataProvider((new ListDataProvider<>(
                    dbService.getPlayerRepository().findPlayerEntitiesByTeamIdIsIn(teamIds)
            )));
        });
        return refresh;
    }

    public Button getDeleteButton(){
        Button delete = new Button();

        delete.addThemeVariants(ButtonVariant.LUMO_LARGE);
        delete.setIcon(VaadinIcon.TRASH.create());
        delete.setEnabled(false);

        playerGrid.addSelectionListener((SelectionListener<Grid<PlayerEntity>, PlayerEntity>)
                selectionEvent -> delete.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        delete.addClickListener(buttonClickEvent -> {
                    try {
                        dbService.getPlayerRepository().delete(
                                playerGrid.getSelectedItems().stream().findFirst().orElseThrow());
                        resetData();
                    }catch (Exception e){
                        Notification.show("Failed to delete player from database.", 4000, Notification.Position.MIDDLE);
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

        playerGrid.addSelectionListener((SelectionListener<Grid<PlayerEntity>, PlayerEntity>)
                selectionEvent -> edit.setEnabled(selectionEvent.getFirstSelectedItem().isPresent()));

        edit.addClickListener(buttonClickEvent -> {
            PlayerForm details = new PlayerForm(
                    dbService.getTournamentRepository(), dbService.getTeamRepository(), dbService.getPlayerRepository(),
                    playerGrid.getSelectedItems().stream().findFirst().orElse(new PlayerEntity()));
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
                getNewPlayerButton(view),
                getEditButton(view),
                getDeleteButton(),
                getRefreshButton()
        );

        return buttonPanel;
    }
}
