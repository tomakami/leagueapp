package com.agh.leagueapp.views.teams;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.repositories.PlayerRepository;
import com.agh.leagueapp.backend.repositories.TournamentRepository;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class TeamOverview extends VerticalLayout {

    private final TeamEntity teamEntity;
    private final TournamentRepository tournamentRepository;
    private final PlayerRepository playerRepository;

    public TeamOverview(TeamEntity teamEntity, TournamentRepository tournamentRepository, PlayerRepository playerRepository){
        this.teamEntity = teamEntity;
        this.tournamentRepository = tournamentRepository;
        this.playerRepository = playerRepository;

        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        setContent();
    }

    private void setContent(){
        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        this.setWidth("100%");
        var tournamentName = tournamentRepository.findById(teamEntity.getTournamentId());
        this.add(new H3(tournamentName.isPresent() ? tournamentName.get().getTournamentName() : "Tournament Name not found."));

        HorizontalLayout titleRow = new HorizontalLayout();
        H3 teamName = new H3(teamEntity.getTeamName());
        teamName.getStyle().set("text-align", "center");
        H3 teamTag = new H3(teamEntity.getTeamTag());
        teamTag.getStyle().set("text-align", "center");

        teamTag.getStyle().set("border","4px solid blue");
        teamName.getStyle().set("border","4px solid green");
        titleRow.getStyle().set("border","4px solid black");

        titleRow.setWidth("90%");
        titleRow.addAndExpand(teamTag, teamName);

        HorizontalLayout infoRow = new HorizontalLayout();
        infoRow.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        infoRow.setPadding(false);
        TextField count = new TextField();
        count.setReadOnly(true);
        count.setWidth("2em");
        count.setValue(playerRepository.countPlayerEntitiesByTeamId(teamEntity.getTeamId()) + " ");
        Icon icon = new Icon(VaadinIcon.USERS);
        Paragraph contactMail = new Paragraph(teamEntity.getMailAddress());
        contactMail.setTitle("Email address");
        contactMail.getStyle().set("text-align", "right");

        count.getStyle().set("border","4px solid blue");
        icon.getStyle().set("border","4px solid green");
        contactMail.getStyle().set("border","4px solid red");
        infoRow.getStyle().set("border","4px solid black");

        infoRow.add(count, icon);
        infoRow.addAndExpand(contactMail);

        Grid<PlayerEntity> grid = new Grid<>(PlayerEntity.class, false);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setWidth("80%");

        grid.addColumn(PlayerEntity::getPlayerId).setHeader("ID")
                .setWidth("3em").setFlexGrow(0);

        grid.addColumn(
                new ComponentRenderer<>(Span::new, (span, player) -> {
                    String position = player.getPosition();
                    Image roleIcon;
                    if(position == null) position="";
                    switch (position){
                        case "Top":
                            roleIcon = LeagueAppConst.TOP;
                            break;
                        case "Jungle":
                            roleIcon = LeagueAppConst.JUNGLE;
                            break;
                        case "Middle":
                            roleIcon = LeagueAppConst.MIDDLE;
                            break;
                        case "Bottom":
                            roleIcon = LeagueAppConst.BOTTOM;
                            break;
                        case "Support":
                            roleIcon = LeagueAppConst.UTILITY;
                            break;
                        case "Fill":
                            roleIcon = LeagueAppConst.FILL;
                            break;
                        default:
                            roleIcon = LeagueAppConst.UNSELECTED;
                    }
                    roleIcon.setWidth("4em");
                    roleIcon.setHeight("4em");
                    span.add(roleIcon);
                })).setHeader("Role")
                .setWidth("5em").setFlexGrow(0);

        grid.addColumn(PlayerEntity::getSummonerName).setHeader("Summoner Name")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(
                new ComponentRenderer<>(Span::new, (span, player) -> {
                    String name = player.getFirstName() + " " + player.getLastName();
                    VerticalLayout template = new VerticalLayout(new H5(name), new Paragraph(player.getIndexNumber()));
                    span.add(template);
                }
                )).setHeader("Player")
                .setAutoWidth(true)
                .setFlexGrow(1);


        setHorizontalComponentAlignment(Alignment.CENTER, grid);

        grid.setDataProvider(
                new ListDataProvider<>(
                        playerRepository.findPlayerEntitiesByTeamId(
                                teamEntity.getTeamId())));

        grid.getStyle().set("border", "4px solid black");

        this.add(titleRow, infoRow, grid);
    }
}
