package com.agh.leagueapp.views.allteams;

import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.tournamentlist.TournamentDetails;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;

@PageTitle("Team List")
@Route(value = "team-list", layout = MainLayout.class)
public class AllTeamsView extends VerticalLayout {

    private final DbService dbService;
    private final Grid<TeamEntity> grid;

    public AllTeamsView(DbService dbService) {
        this.dbService = dbService;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of all signed teams"));
        add(new Paragraph("Click on + icon to add new team"));

        HorizontalLayout buttonPanel = new HorizontalLayout(
                AddNewTeamButton(),
                AddRefreshButton());
        buttonPanel.setWidth("45%");
        buttonPanel.setHeight("10%");
        buttonPanel.setPadding(false);
        add(buttonPanel);

        grid = new Grid<>(TeamEntity.class, false);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.setWidth("60%");

        grid.addColumn(TeamEntity::getTeamId).setHeader("ID")
                .setWidth("3em").setFlexGrow(0);
        grid.addColumn(TeamEntity::getTeamTag).setHeader("Tag")
                .setWidth("5em").setFlexGrow(0);
        grid.addColumn(TeamEntity::getTeamName).setHeader("Team Name")
                .setWidth("20em").setFlexGrow(0);

        grid.addColumn(
                new ComponentRenderer<>(Paragraph::new, (p, team) -> {
                    p.setText(
                            String.valueOf(dbService.getPlayerRepository().countPlayerEntitiesByTeamId(team.getTeamId())));
                }
                )).setHeader("Players")
                .setWidth("5em").setFlexGrow(0);

        grid.addColumn(
                new ComponentRenderer<>(Div::new, (div, team) -> {
                    Button select = new Button();
                    select.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY,
                            ButtonVariant.LUMO_SUCCESS);
                    select.addClickListener(e -> Notification.show("Select " + team.getTeamName()));
                    select.setIcon(new Icon(VaadinIcon.CHECK));
                    select.setWidth("2ep");

                    Button edit = new Button();
                    edit.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY);
                    edit.addClickListener(e -> Notification.show("Edit " + team.getTeamName()));
                    edit.setIcon(new Icon(VaadinIcon.EDIT));
                    edit.setWidth("2ep");

                    div.add(select,edit);
                })).setHeader("Manage")
                .setWidth("8em").setFlexGrow(0);

        grid.setDataProvider(new ListDataProvider<>(FetchTeamList()));

        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        setHorizontalComponentAlignment(Alignment.CENTER, grid);
        add(grid);
    }

    private List<TeamEntity> FetchTeamList() {
        return dbService.getTeamRepository().findAll();
    }

    private Button AddNewTeamButton(){
        Button newTeam = new Button();
        newTeam.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newTeam.setWidth("75%");
        newTeam.setHeight("100%");
        Icon plusIcon = VaadinIcon.PLUS.create();
        newTeam.setIcon(plusIcon);

        Dialog dialog = new TeamDetails(dbService.getTournamentRepository(), dbService.getTeamRepository(),null).getDialog();
        add(dialog);

        newTeam.addClickListener(buttonClickEvent -> {
                dialog.open();
        });

        return newTeam;
    }

    private Button AddRefreshButton(){
        Button refresh = new Button();
        refresh.setWidth("25%");
        refresh.setHeight("100%");
        Icon refreshIcon = VaadinIcon.REFRESH.create();
        refresh.setIcon(refreshIcon);

        refresh.addClickListener(buttonClickEvent -> {
            grid.setDataProvider(new ListDataProvider<>(FetchTeamList()));
        });

        return refresh;
    }
}
