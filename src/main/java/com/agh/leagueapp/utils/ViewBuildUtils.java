package com.agh.leagueapp.utils;

import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class ViewBuildUtils {
    public static Div headerWithContent(String header, String content){
        H4 title = new H4(header);
        title.getStyle().set("margin", "0px");
        title.getStyle().set("text-align","center");

        Paragraph cont = new Paragraph(content);
        cont.getStyle().set("margin", "0px");
        cont.getStyle().set("text-align","center");

        Div div = new Div();
        div.add(title, cont);
        return div;
    }

    public static ComponentRenderer<Span, PlayerEntity> roleIconRenderer(String size){
                return new ComponentRenderer<>(Span::new, (span, player) -> {
                    String position = player.getPosition();
                    Image roleIcon;
                    if(position == null) position="";
                    switch (position){
                        case "Top":
                            roleIcon = new Image(LeagueAppConst.TOP, "Top");
                            break;
                        case "Jungle":
                            roleIcon = new Image(LeagueAppConst.JUNGLE, "Jungle");
                            break;
                        case "Middle":
                            roleIcon = new Image(LeagueAppConst.MIDDLE, "Middle");
                            break;
                        case "Bottom":
                            roleIcon = new Image(LeagueAppConst.BOTTOM, "Bottom");
                            break;
                        case "Support":
                            roleIcon = new Image(LeagueAppConst.UTILITY, "Support");
                            break;
                        case "Fill":
                            roleIcon = new Image(LeagueAppConst.FILL, "Fill");
                            break;
                        default:
                            roleIcon = new Image(LeagueAppConst.UNSELECTED, "Unselected");
                    }
                    roleIcon.setWidth(size);
                    roleIcon.setHeight(size);
                    span.add(roleIcon);
                });
    }

    public static HorizontalLayout StatRow(String value1, String title, String value2){
        HorizontalLayout row = new HorizontalLayout();
        row.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        H4 middle = new H4(title);
        middle.setWidth("25%");
        middle.getStyle().set("text-align", "center");
        row.add(new Paragraph(value1),
                middle,
                new Paragraph(value2)
        );
        return row;
    }
}
