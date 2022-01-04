package com.agh.leagueapp.views.tournamentlist;

import com.agh.leagueapp.backend.data.TournamentConfig;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.TournamentRepository;
import com.agh.leagueapp.utils.LeagueAppConst;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;


public class TournamentDetails extends FormLayout {

    private final TournamentRepository tournamentRepository;
    private final TournamentEntity tournament;
    private final boolean isEditing;

    private final Dialog dialog = new Dialog();

    private final TextField tournamentName = new TextField("Tournament Name");
    private final Select<LeagueShard> regionSelect = new Select<>();
    private final Select<Integer> teamSizeSelect = new Select<>();
    private final TextArea comment = new TextArea("Description");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");

    private final Binder<TournamentEntity> binder = new Binder<>(TournamentEntity.class);

    public TournamentDetails(TournamentRepository tournamentRepository, TournamentEntity tournament){
        this.tournamentRepository = tournamentRepository;
        isEditing = tournament != null;
        this.tournament = isEditing ? tournament : new TournamentEntity();

        setupForm();
        Bind();
        dialog.add(this);
    }

    private void setupForm(){
        regionSelect.setLabel("Region");
        regionSelect.setItemLabelGenerator(LeagueShard::prettyName);
        regionSelect.setItems(LeagueAppConst.validRegions);
        regionSelect.setEmptySelectionAllowed(false);
        if(isEditing) regionSelect.setEnabled(false);

        teamSizeSelect.setLabel("Team size");
        teamSizeSelect.setItems(5,4,3,2,1);
        teamSizeSelect.setPlaceholder("5");
        if(isEditing) teamSizeSelect.setEnabled(false);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(buttonClickEvent -> SaveAction());

        cancel.addClickListener(buttonClickEvent -> CancelAction());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(buttonClickEvent -> DeleteAction());

        this.add(tournamentName, regionSelect, teamSizeSelect, comment, save, cancel);
        if (isEditing) this.add(delete);
        this.setColspan(comment, 3);
        this.setResponsiveSteps(new ResponsiveStep("0", 3));
    }


    private void Bind(){
        binder.setBean(tournament);

        binder.forField(tournamentName)
                .asRequired("Tournament name is required")
                .withValidator(new StringLengthValidator("Name should be longer than 3 and shorter than 25 characters", 3, 25))
                .bind(
                        TournamentEntity::getTournamentName,
                        TournamentEntity::setTournamentName
                );

        binder.forField(regionSelect)
                .asRequired("Region is required")
                .bind(
                        TournamentEntity::getRegion,
                        TournamentEntity::setRegion
                );

        binder.forField(comment)
                .bind(
                        TournamentEntity::getComment,
                        TournamentEntity::setComment
                );

        binder.forField(teamSizeSelect)
                .withNullRepresentation(5)
                .bind(
                        TournamentEntity::getTeamSize,
                        TournamentEntity::setTeamSize
                );
    }

    private void SaveAction(){
        if(binder.validate().isOk()){
            try{
                // Double saving to database is needed in order to generate ID, which is later used in initialization through API
                tournamentRepository.save(binder.getBean());
                TournamentEntity initializedEntity = TournamentConfig.InitializeInAPI(binder.getBean());
                tournamentRepository.save(initializedEntity);
            }catch (Exception e) {
                Notification.show("Error occurred during saving to database.", 30, Notification.Position.MIDDLE).open();
                System.out.println("ERROR IN TOURNAMENT DETAILS\n\n" + e.toString() + "\n\n");
            }
            CancelAction();
        }
    }

    private void CancelAction(){
        dialog.close();
        tournamentName.clear();
        regionSelect.clear();
        comment.clear();
        teamSizeSelect.clear();
    }

    private void DeleteAction(){
        // TODO: ask for confirmation before delete
        try{
            tournamentRepository.delete(binder.getBean());
        }catch (Exception e) {
            Notification.show("Error occurred during deleting from database.", 30, Notification.Position.MIDDLE).open();
        }
        CancelAction();
    }

    public Dialog getDialog(){
        return dialog;
    }
}
