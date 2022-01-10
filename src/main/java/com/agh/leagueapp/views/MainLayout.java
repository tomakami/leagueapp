package com.agh.leagueapp.views;

import com.agh.leagueapp.backend.Navigator;
import com.agh.leagueapp.views.players.PlayersView;
import com.agh.leagueapp.views.teamdetails.TeamDetailsView;
import com.agh.leagueapp.views.teams.AllTeamsView;
import com.agh.leagueapp.views.tournament.TournamentView;
import com.agh.leagueapp.views.tournaments.TournamentListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@PWA(name = "LeagueTournamentApp", shortName = "LeagueTournamentApp", enableInstallPrompt = false)
@Theme(themeFolder = "leaguetournamentapp")
@PageTitle("LeagueTournamentApp")
public class MainLayout extends AppLayout {

    public final Navigator navigator;

    private Tabs menu;
    private final HorizontalLayout centeredMenu;
    private final Tabs basicMenu, detailedMenu;

    public MainLayout(Navigator navigator) {
        this.navigator = navigator;
        this.setDrawerOpened(false);
        basicMenu = createBasicMenu();
        detailedMenu = createDetailedMenu();

        centeredMenu = new HorizontalLayout();
        centeredMenu.setSizeFull();
        centeredMenu.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);


        basicMenu.getStyle().set("border", "4px dotted orange");
        detailedMenu.getStyle().set("border", "4px dotted blue");
        menu = basicMenu;

        basicMenu.setSelectedTab(null);
        detailedMenu.setSelectedTab(null);

        Image logo = new Image("icons/lol_icon.png", "LoL");
        logo.setMaxHeight("2em");
        logo.setMaxWidth("2em");

        centeredMenu.getStyle().set("border", "4px solid red");
        centeredMenu.add(basicMenu, logo, detailedMenu);
        centeredMenu.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, logo);

        this.addToNavbar(true, centeredMenu);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        RouteConfiguration configuration = RouteConfiguration.forSessionScope();

        if (configuration.isRouteRegistered(this.getContent().getClass())) {
            String target = configuration.getUrl(this.getContent().getClass());

            if(this.getContent().getClass() == TournamentView.class
                    || this.getContent().getClass() == TeamDetailsView.class){
                menu = detailedMenu;
                basicMenu.setSelectedTab(null);
            }
            else{
                menu = basicMenu;
                detailedMenu.setSelectedTab(null);
            }

            Optional< Component > tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        } else {
            menu.setSelectedTab(null);
        }
    }

    private static Tabs createBasicMenu() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(createTab(VaadinIcon.TROPHY, "Tournaments", TournamentListView.class));
        tabs.add(createTab(VaadinIcon.GROUP,"All Teams", AllTeamsView.class));
        tabs.add(createTab(VaadinIcon.USER,"All Players", PlayersView.class));

        return tabs;
    }

    private static Tabs createDetailedMenu(){
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(createTab(VaadinIcon.SEARCH, "Overview", TournamentView.class));
        tabs.add(createTab(VaadinIcon.GROUP,"Teams", TeamDetailsView.class));
        tabs.add(createTab(VaadinIcon.USERS,"Players", PlayersView.class));

        return tabs;
    }

    private static Tab createTab(VaadinIcon icon, String title, Class<? extends Component> viewClass) {
        return createTab(populateLink(new RouterLink(null, viewClass), icon, title));
    }

    private static Tab createTab(Component content) {
        final Tab tab = new Tab();
        tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        tab.add(content);
        return tab;
    }

    private static <T extends HasComponents> T populateLink(T a, VaadinIcon icon, String title) {
        a.add(icon.create());
        a.add(title);
        return a;
    }
}
