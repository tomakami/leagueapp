package com.agh.leagueapp.utils;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;

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
}
