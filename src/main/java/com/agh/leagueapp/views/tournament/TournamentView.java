package com.agh.leagueapp.views.tournament;

import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.agh.leagueapp.views.MainLayout;
import com.agh.leagueapp.views.tournaments.TournamentListView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.Optional;

@PageTitle("Tournament List")
@Route(value = LeagueAppConst.PAGE_TOURNAMENTS + "/:tournamentID?/overview", layout = MainLayout.class)
public class TournamentView
        extends VerticalLayout
        implements BeforeEnterObserver{

    private String tournamentID;
    private final DbService dbService;

    private TournamentEntity tournamentEntity;

    public TournamentView(DbService dbService){
        this.dbService = dbService;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> parameter = event.getRouteParameters().get("tournamentID");
        if(parameter.isEmpty() || !dbService.getTournamentRepository().existsById(Integer.valueOf(parameter.get()))) {
            event.forwardTo(TournamentListView.class);
        }
        else {
            tournamentID = parameter.get();
            tournamentEntity = dbService.getTournamentRepository().findById(Integer.valueOf(tournamentID)).orElseThrow();

            setupOverview();
        }
    }

    private void setupOverview() {
        this.setAlignItems(Alignment.CENTER);
        this.setHeightFull();
        setupHeaderLayout();
        setupMainLayout();
    }

    private void setupHeaderLayout(){
        Button details = new Button("Details");
        details.addClickListener(buttonClickEvent -> {
            Notification.show("Clicked details");
        });
        details.setWidth("10em");

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

        header.add(filler, headerContent, details);

        this.add(header);
    }

    private void setupMainLayout(){
        VerticalLayout leftPart = new VerticalLayout();
        leftPart.getStyle().set("border", "4px dotted green");
        leftPart.setWidthFull();
        leftPart.setHeightFull();

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
}
