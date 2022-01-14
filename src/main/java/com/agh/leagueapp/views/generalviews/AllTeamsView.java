package com.agh.leagueapp.views.generalviews;

import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.GridBuilders.TeamGridBuilder;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;


@PageTitle("Team List")
@Route(value = LeagueAppConst.PAGE_TEAMS, layout = MainLayout.class)
public class AllTeamsView extends VerticalLayout {

    public AllTeamsView(DbService dbService) {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.START);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        add(new H2("List of all registered teams"));

        TeamGridBuilder teamGridBuilder = buildTeamGrid(dbService);

        Grid<TeamEntity> teamGrid = teamGridBuilder.getTeamGrid();
        teamGrid.setWidth("50%");

        HorizontalLayout buttonPanel = teamGridBuilder.getButtonPanel(this);
        buttonPanel.setWidth("50%");
        buttonPanel.setPadding(false);

        add(buttonPanel, teamGrid);
        setHorizontalComponentAlignment(Alignment.CENTER, teamGrid);
    }

    private TeamGridBuilder buildTeamGrid(DbService dbService){
        TeamGridBuilder teamGridBuilder = new TeamGridBuilder(dbService);

        List<Integer> tournamentIds = new ArrayList<>();
        for(TournamentEntity entity : dbService.getTournamentRepository().findAll())
            tournamentIds.add(entity.getTournamentId());

        teamGridBuilder
                .withSelectionMode(Grid.SelectionMode.SINGLE)
                .withIdColumn()
                .withTournamentNameColumn(true,1)
                .withTagColumn()
                .withTeamNameColumn(true,1)
                .withMailAddress(true,1)
                .withPlayerCountColumn()
                .withDataProvider(new ListDataProvider<>(dbService.getTeamRepository().findAll()))
                .withDataByTournamentId(tournamentIds)
                .withThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        teamGridBuilder.getTeamGrid().addColumn(
                        new ComponentRenderer<>(Span::new, (span, team) -> {
                            Button members = new Button();
                            members.addThemeVariants(ButtonVariant.LUMO_ICON,
                                    ButtonVariant.LUMO_TERTIARY);
                            members.addClickListener(e ->{
                                Dialog dialog =
                                        new Dialog(new TeamOverview(team, dbService));
                                dialog.setWidth("40%");
                                add(dialog);
                                dialog.open();
                            });
                            members.setIcon(new Icon(VaadinIcon.USERS));
                            members.setWidth("2ep");

                            span.add(members);
                        }))
                .setHeader("Details")
                .setFlexGrow(0)
                .setWidth("5em");

        return teamGridBuilder;
    }
}
