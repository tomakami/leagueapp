package com.agh.leagueapp.utils.GridBuilders;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.players.PlayerDetails;
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
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.SelectionListener;

import java.util.Optional;

public class PlayerGridBuilder {

    private Grid<PlayerEntity> playerGrid;
    private final DbService dbService;

    private DataProvider<PlayerEntity, ?> dataProvider;

    public PlayerGridBuilder(DbService dbService){
        this.dbService = dbService;
        this.reset();
    }


    // General methods

    public void reset(){
        this.playerGrid = new Grid<>(PlayerEntity.class, false);
    }

    public void resetData(){
        playerGrid.setDataProvider(dataProvider);
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
        this.dataProvider = dataProvider;
        resetData();
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
                            //VerticalLayout template = new VerticalLayout(new H5(name), new Paragraph(player.getIndexNumber()));
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
             PlayerDetails details = new PlayerDetails(
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
        Button refresh = new Button();

        refresh.addThemeVariants(ButtonVariant.LUMO_LARGE);
        refresh.setIcon(VaadinIcon.REFRESH.create());
        refresh.addClickListener(buttonClickEvent -> {
            resetData();
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
            PlayerDetails details = new PlayerDetails(
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
