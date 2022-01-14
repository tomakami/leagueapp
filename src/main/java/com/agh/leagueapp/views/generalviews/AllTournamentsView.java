package com.agh.leagueapp.views.generalviews;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.TournamentGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.detailedviews.TournamentOverview;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

@PageTitle("Tournament List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS, layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
public class AllTournamentsView
        extends VerticalLayout {

    private final DbService dbService;
    private final Navigator navigator;

    public AllTournamentsView(DbService dbService, Navigator navigator) {
        this.dbService = dbService;
        this.navigator = navigator;

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of all registered tournaments"));

        TournamentGridBuilder tournamentGridBuilder = new TournamentGridBuilder(dbService);

        tournamentGridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withIdColumn()
                .withRegionColumn()
                .withTournamentName(true, 0)
                .withDescriptionColumn("20em", 1)
                .withTeamCountColumn()
                .withDataProvider(new ListDataProvider<>(dbService.getTournamentRepository().findAll()))
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        Grid<TournamentEntity> tournamentGrid = tournamentGridBuilder.getTournamentGrid();
        tournamentGrid.setWidth("50%");

        HorizontalLayout buttonPanel = tournamentGridBuilder.getButtonPanel(this);
        buttonPanel.setWidth("50%");
        buttonPanel.setPadding(false);
        add(buttonPanel);

        tournamentGrid.addColumn(
                new ComponentRenderer<>(Span::new, (span, tournament) -> {

                    Button select = new Button();
                    select.addThemeVariants(ButtonVariant.LUMO_ICON,
                            ButtonVariant.LUMO_TERTIARY,
                            ButtonVariant.LUMO_SUCCESS);
                    select.addClickListener(e -> Navigator.setTournamentID(tournament.getTournamentId()));
                    select.setIcon(new Icon(VaadinIcon.CHECK));
                    select.setWidth("2ep");

                    RouterLink link = new RouterLink("", TournamentOverview.class,
                            new RouteParameters("tournamentID", tournament.getTournamentId().toString()));

                    link.add(select);

                    span.add(link);
                })).setHeader("Select")
                .setWidth("6em").setFlexGrow(0);

        setHorizontalComponentAlignment(Alignment.CENTER, tournamentGrid);
        add(tournamentGrid);
    }
}
