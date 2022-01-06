package com.agh.leagueapp.views.teams;

import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.repositories.PlayerRepository;
import com.agh.leagueapp.backend.repositories.TournamentRepository;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import java.util.ArrayList;
import java.util.List;

public class TeamOverviewBuilder {

    private final TeamEntity teamEntity;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    private final Dialog dialog = new Dialog();
    private final Tabs menu;
    private final VerticalLayout content = new VerticalLayout();

    public TeamOverviewBuilder(TeamEntity teamEntity, TournamentRepository tournamentRepository, PlayerRepository playerRepository){
        this.teamEntity = teamEntity;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;

        menu = createMenuTabs();
        menu.addSelectedChangeListener(event -> {
            setContent(event.getSelectedTab());
        });

        dialog.add(menu);
    }

    private void setContent(Tab tab){
        content.removeAll();
        content.add(new Paragraph("XDDDDDDDDXDXDXDXDXXDD" + tab.getLabel()));
    }

    private Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private Tab[] getAvailableTabs(){
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(getOverview());
        // TODO: get players tabs and one for adding new ones

        return tabs.toArray(new Tab[tabs.size()]);
    }

    private Tab getOverview(){
        final Tab overview = new Tab("Overview");
        overview.add(new TeamOverview(teamEntity, tournamentRepository, playerRepository));
        return overview;
    }

    public Dialog getDialog(){
        return dialog;
    }
}
