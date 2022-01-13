package com.agh.leagueapp.views.games;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.tournaments.TournamentListView;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.Optional;

@PageTitle("Game List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/games", layout = MainLayout.class)
public class GameDetailsView
        extends HorizontalLayout
        implements BeforeEnterObserver, BeforeLeaveObserver {

    private final DbService dbService;

    private TournamentEntity tournamentEntity;
    private GameEntity gameEntity = null;

    private String tournamentID;
    private final VerticalLayout listLayout, detailsLayout;


    public GameDetailsView(DbService dbService){
        this.dbService = dbService;
        this.listLayout = new VerticalLayout();
        this.detailsLayout = new VerticalLayout();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean forwarded = false;
        Optional<String> parameter = event.getRouteParameters().get("tournamentID");

        if(parameter.isEmpty()) {
            forwarded = true;
            tournamentID = Navigator.getTournamentID().toString();
            event.forwardTo(GameDetailsView.class,
                    new RouteParameters("tournamentID", tournamentID));
        }
        else
            tournamentID = parameter.get();

        if(!dbService.getTournamentRepository().existsById(Integer.valueOf(tournamentID))) {
            event.forwardTo(TournamentListView.class);
        }
        else if (!forwarded){
            tournamentEntity = dbService.getTournamentRepository().findById(Integer.valueOf(tournamentID)).orElseThrow();
            Navigator.setTournamentID(Integer.valueOf(tournamentID));
            setupOverview();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        this.removeAll();
    }

    public void setupOverview(){
        this.setSizeFull();

        setupListLayout();
        setupDetailsLayout();

        this.add(listLayout, detailsLayout);
    }

    private void setupListLayout() {
        listLayout.setWidth("40%");
        listLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        listLayout.getStyle().set("border", "4px solid blue");
        listLayout.removeAll();
    }
    private void setupDetailsLayout() {
        detailsLayout.removeAll();
        detailsLayout.setWidth("60%");
        detailsLayout.getStyle().set("border", "4px dotted green");

        if (this.gameEntity == null) {
            detailsLayout.add(new Paragraph("haha lol empty"));
            return;
        }
    }
}
