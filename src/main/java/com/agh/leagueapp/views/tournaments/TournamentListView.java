package com.agh.leagueapp.views.tournaments;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.tournament.TournamentView;
import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.router.*;

import java.util.List;

@PageTitle("Tournament List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class TournamentListView
        extends VerticalLayout {

    private final DbService dbService;
    private final Grid<TournamentEntity> grid;
    private final Navigator navigator;

    public TournamentListView(DbService dbService, Navigator navigator) {
        this.dbService = dbService;
        this.navigator = navigator;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of LeagueTournamentApp Tournaments"));
        add(new Paragraph("Click on Tournament to set as active or + icon to add new tournament."));

        HorizontalLayout buttonPanel = new HorizontalLayout(
                AddNewTournamentButton(),
                AddRefreshButton());
        buttonPanel.setWidth("45%");
        buttonPanel.setHeight("10%");
        buttonPanel.setPadding(false);
        add(buttonPanel);

        grid = new Grid<>(TournamentEntity.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setWidth("60%");

        grid.addColumn(TournamentEntity::getTournamentId).setHeader("ID")
                .setWidth("4em").setFlexGrow(0);
        grid.addColumn(TournamentEntity::getRegion).setHeader("Region")
                .setWidth("5em").setFlexGrow(0);
        grid.addColumn(TournamentEntity::getTournamentName).setHeader("Tournament Name")
                .setWidth("20em").setFlexGrow(0);
        grid.addColumn(TournamentEntity::getComment).setHeader("Description");

        grid.addColumn(
                new ComponentRenderer<>(Paragraph::new, (p, tournament) -> {
                    p.setText(
                            String.valueOf(dbService.getTeamRepository().countTeamEntitiesByTournamentId(tournament.getTournamentId())));
                }
                )).setHeader("Teams")
                .setWidth("5em").setFlexGrow(0);

        grid.addColumn(
                new ComponentRenderer<>(Div::new, (div, tournament) -> {

                    Button select = new Button();
                    select.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY,
                            ButtonVariant.LUMO_SUCCESS);
                    select.addClickListener(e -> {
                        Navigator.setTournamentID(tournament.getTournamentId());
                        Notification.show("Select " + tournament.getTournamentName());
                    });
                    select.setIcon(new Icon(VaadinIcon.CHECK));
                    select.setWidth("2ep");

                    RouterLink link = new RouterLink("", (Class<? extends Component>) TournamentView.class,
                            new RouteParameters("tournamentID", tournament.getTournamentId().toString()));

                    link.add(select);

                    Button edit = new Button();
                    edit.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY);
                    edit.addClickListener(e -> {
                        Notification.show("Edit " + tournament.getTournamentName());
                        Dialog dialog = new TournamentDetails(dbService.getTournamentRepository(), tournament).getDialog();
                        add(dialog);
                        dialog.open();
                    });
                    edit.setIcon(new Icon(VaadinIcon.EDIT));
                    edit.setWidth("2ep");

                    div.add(link,edit);
                })).setHeader("Manage")
                .setWidth("8em").setFlexGrow(0);

        grid.setDataProvider(new ListDataProvider<>(FetchTournamentList()));

        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        setHorizontalComponentAlignment(Alignment.CENTER, grid);
        add(grid);
    }

    private List<TournamentEntity> FetchTournamentList() {
        return dbService.getTournamentRepository().findAll();
    }

    private Button AddNewTournamentButton(){
        Button newTournament = new Button();
        newTournament.addThemeVariants(ButtonVariant.LUMO_LARGE);
        newTournament.setWidth("75%");
        newTournament.setHeight("100%");
        Icon plusIcon = VaadinIcon.PLUS.create();
        newTournament.setIcon(plusIcon);

        newTournament.addClickListener(buttonClickEvent -> {
            TournamentDetails details = new TournamentDetails(dbService.getTournamentRepository(), null);
            Dialog dialog = details.getDialog();
            add(dialog);
            dialog.open();
        });

        return newTournament;
    }

    private Button AddRefreshButton(){
        Button refresh = new Button();
        refresh.setWidth("25%");
        refresh.setHeight("100%");
        Icon refreshIcon = VaadinIcon.REFRESH.create();
        refresh.setIcon(refreshIcon);

        refresh.addClickListener(
                buttonClickEvent ->
                        grid.setDataProvider
                                (new ListDataProvider<>(FetchTournamentList())));

        return refresh;
    }
}
