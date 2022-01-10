package com.agh.leagueapp.views.tournament;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.utils.ViewBuildUtils;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.teamdetails.TeamDetailsView;
import com.agh.leagueapp.views.teams.AllTeamsView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.*;

import java.util.Optional;

@PageTitle("Tournament List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/overview", layout = MainLayout.class)
public class TournamentView
        extends VerticalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver{

    private String tournamentID;

    private final DbService dbService;
    private TournamentEntity tournamentEntity;

    private final Dialog details = new Dialog();

    public TournamentView(DbService dbService){
        this.dbService = dbService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean forwarded = false;
        Optional<String> parameter = event.getRouteParameters().get("tournamentID");

        if(parameter.isEmpty()) {
            forwarded = true;
            tournamentID = Navigator.getTournamentID().toString();
            event.forwardTo(TournamentView.class,
                    new RouteParameters("tournamentID", tournamentID));
        }
        else
            tournamentID = parameter.get();

        if(!dbService.getTournamentRepository().existsById(Integer.valueOf(tournamentID))) {
            event.forwardTo(AllTeamsView.class);
        }
        else if (!forwarded){
            tournamentEntity = dbService.getTournamentRepository().findById(Integer.valueOf(tournamentID)).orElseThrow();
            Navigator.setTournamentID(Integer.valueOf(tournamentID));
            setupOverview();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event){
        this.removeAll();
    }

    private void setupOverview() {
        this.setAlignItems(Alignment.CENTER);
        this.setHeightFull();
        setupDetailsDialog();
        setupHeaderLayout();
        setupMainLayout();
    }

    private void setupHeaderLayout(){
        Button detailsButton = new Button("Details");
        detailsButton.addClickListener(buttonClickEvent -> {
            this.details.open();
        });
        detailsButton.setWidth("10em");

        Span filler = new Span();
        filler.setWidth("10em");

        H2 name = new H2(tournamentEntity.getTournamentName());
        name.getStyle().set("margin", "0px");

        H4 region = new H4(tournamentEntity.getRegion().prettyName());
        region.getStyle().set("margin", "0px");

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.getStyle().set("border", "4px dotted orange");
        headerContent.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        headerContent.setWidth("70%");
        headerContent.setPadding(false);
        headerContent.setMargin(false);
        headerContent.setSpacing(false);
        headerContent.add(
                name,
                region);

        HorizontalLayout header = new HorizontalLayout();
        header.getStyle().set("border", "4px dotted blue");
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        header.add(filler, headerContent, detailsButton);

        this.add(header);
    }

    private void setupMainLayout(){
        VerticalLayout leftPart = new VerticalLayout();
        leftPart.getStyle().set("border", "4px dotted green");
        leftPart.setWidthFull();
        leftPart.setHeightFull();
        leftPart.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        leftPart.add(
                setupLinkButton("See detailed team list", TeamDetailsView.class, new RouteParameters("tournamentID", tournamentID)),
                setupTeamGrid()
        );

        VerticalLayout middlePart = new VerticalLayout();
        middlePart.getStyle().set("border", "4px dotted brown");
        middlePart.setWidthFull();
        middlePart.setHeightFull();

        VerticalLayout rightPart = new VerticalLayout();
        rightPart.getStyle().set("border", "4px dotted blue");
        rightPart.setWidthFull();
        rightPart.setHeightFull();

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.getStyle().set("border", "4px dotted red");
        mainLayout.setWidthFull();
        mainLayout.setHeightFull();

        mainLayout.add(leftPart,middlePart,rightPart);

        this.add(mainLayout);
    }

    private Grid<TeamEntity> setupTeamGrid(){
        Grid<TeamEntity> teamGrid = new Grid<>(TeamEntity.class, false);
        teamGrid.setSelectionMode(Grid.SelectionMode.NONE);

        teamGrid.addColumn(TeamEntity::getTeamTag).setHeader("Tag")
                .setAutoWidth(true).setFlexGrow(1);

        teamGrid.addColumn(TeamEntity::getTeamName).setHeader("Team Name")
                .setAutoWidth(true).setFlexGrow(3);

        teamGrid.addColumn(
                        new ComponentRenderer<>(Paragraph::new, (p, team) -> {
                            String temp;
                            p.getStyle().set("text-align", "center");
                            try{
                                temp = String.valueOf(dbService.getPlayerRepository().countPlayerEntitiesByTeamId(team.getTeamId()));
                            }catch(Exception e){
                                temp = "";
                            }
                            p.setText(temp);

                        }
                        )).setHeader("Players")
                .setWidth("5em").setFlexGrow(0);

        teamGrid.addColumn(
                        new ComponentRenderer<>(Paragraph::new, (p, team) -> {
                            String temp;
                            p.getStyle().set("text-align", "center");
                            try{
                                temp = String.valueOf(
                                        dbService.getGameRepository().
                                                countGameEntitiesByBlueTeamId(team.getTeamId()) +
                                                dbService.getGameRepository().
                                                        countGameEntitiesByRedTeamId(team.getTeamId()));
                            }catch(Exception e){
                                temp = "";
                            }
                            p.setText(temp);

                        }
                        )).setHeader("Games Played")
                .setWidth("5em").setFlexGrow(0);

        teamGrid.setDataProvider(new ListDataProvider<>(
                dbService.getTeamRepository().findAllByTournamentId(tournamentEntity.getTournamentId())));

        teamGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS, GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);

        return teamGrid;
    }

    private RouterLink setupLinkButton(String t, Class<? extends Component> c, RouteParameters params){
        Button teamListButton = new Button(t);
        RouterLink teamListLink = new RouterLink("", c, params);
        teamListLink.add(teamListButton);

        return teamListLink;
    }

    private void setupDetailsDialog(){
        details.setMaxWidth("25%");
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);

        layout.add(new H3(tournamentEntity.getTournamentName()));

        layout.add(ViewBuildUtils.headerWithContent("Region", tournamentEntity.getRegion().prettyName()));
        layout.add(ViewBuildUtils.headerWithContent("Description", tournamentEntity.getComment()));

        layout.add(new HorizontalLayout(
                ViewBuildUtils.headerWithContent("ID", tournamentID), new Span(),
                ViewBuildUtils.headerWithContent("API ID", tournamentEntity.getApiId()), new Span(),
                ViewBuildUtils.headerWithContent("Team Size", tournamentEntity.getTeamSize().toString())
        ));

        layout.add(new HorizontalLayout(
                ViewBuildUtils.headerWithContent("Provider ID", tournamentEntity.getProviderId().toString()), new Span(),
                ViewBuildUtils.headerWithContent("Provider URL", tournamentEntity.getProviderUrl())
        ));

        Button close = new Button("Close");
        close.addClickListener(click -> details.close());
        layout.add(close);

        details.add(layout);
    }


}
