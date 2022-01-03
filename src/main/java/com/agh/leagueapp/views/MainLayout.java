package com.agh.leagueapp.views;

import com.agh.leagueapp.views.about.AboutView;
import com.agh.leagueapp.views.helloworld.HelloWorldView;
import com.agh.leagueapp.views.tournamentlist.TournamentListView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
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

    private final Tabs menu;

    public MainLayout() {

        this.setDrawerOpened(false);
        menu = createMenuTabs();
        FlexLayout centeredMenu = new FlexLayout();
        centeredMenu.setSizeFull();
        centeredMenu.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        centeredMenu.setAlignItems(FlexComponent.Alignment.CENTER);

        centeredMenu.add(menu);

        this.addToNavbar(true, centeredMenu);

    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        RouteConfiguration configuration = RouteConfiguration.forSessionScope();
        if (configuration.isRouteRegistered(this.getContent().getClass())) {
            String target = configuration.getUrl(this.getContent().getClass());
            Optional< Component > tabToSelect = menu.getChildren().filter(tab -> {
                Component child = tab.getChildren().findFirst().get();
                return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
            }).findFirst();
            tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
        } else {
            menu.setSelectedTab(null);
        }
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);
        tabs.add(getAvailableTabs());
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        System.out.println("Adding tournaments tab");
        tabs.add(createTab(VaadinIcon.EDIT, "Tournaments", TournamentListView.class));
        System.out.println("Adding teams tab");
        tabs.add(createTab(VaadinIcon.CLOCK,"Teams", HelloWorldView.class));
        System.out.println("Adding players tab");
        tabs.add(createTab(VaadinIcon.USER,"Players", AboutView.class));

        return tabs.toArray(new Tab[tabs.size()]);
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
