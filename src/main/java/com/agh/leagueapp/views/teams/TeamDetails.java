package com.agh.leagueapp.views.teams;

import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.TeamRepository;
import com.agh.leagueapp.backend.repositories.TournamentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;

public class TeamDetails extends FormLayout {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final TeamEntity team;
    private final boolean isEditing;

    private final Dialog dialog = new Dialog();

    private final TextField teamName = new TextField("Team Name");
    private final TextField region = new TextField("Region");
    private final TextField teamTag = new TextField("Team Tag");
    private final Select<TournamentEntity> tournament = new Select<>();
    private final TextField mailAddress = new TextField("Contact Email");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");


    private final Binder<TeamEntity> binder = new Binder<>(TeamEntity.class);

    public TeamDetails(TournamentRepository tournamentRepository, TeamRepository teamRepository, TeamEntity team){
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        isEditing = team != null;
        this.team = isEditing ? team : new TeamEntity();

        setupForm();
        Bind();
        dialog.add(this);
    }

    private void setupForm(){
        tournament.setLabel("Tournament");
        tournament.setItems(tournamentRepository.findAll());
        tournament.setItemLabelGenerator(TournamentEntity::getTournamentName);
        tournament.setEmptySelectionAllowed(false);

        tournament.addValueChangeListener(click -> region.setValue(tournament.getValue().getRegion().prettyName()));

        region.setReadOnly(true);
        tournament.setReadOnly(isEditing);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(buttonClickEvent -> SaveAction());

        cancel.addClickListener(buttonClickEvent -> CancelAction());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(buttonClickEvent -> DeleteAction());

        this.add(teamName, teamTag, tournament, region, mailAddress, save, cancel);
        if (isEditing) this.add(delete);
        this.setColspan(teamName, 2);
        this.setColspan(tournament, 2);
        this.setColspan(mailAddress, 3);
        this.setResponsiveSteps(new ResponsiveStep("0", 3));
    }

    private void Bind(){
        binder.setBean(team);


        binder.forField(teamName)
                .asRequired("Team name is required")
                .withValidator(new StringLengthValidator("Name should be longer than 3 and shorter than 25 characters", 3, 25))
                .bind(
                        TeamEntity::getTeamName,
                        TeamEntity::setTeamName
                );

        binder.forField(teamTag)
                .asRequired("Team tag is required")
                .withValidator(new RegexpValidator("Team tag should be longer than 2 characters, shorter than 4 characters and consist of capital letters and numbers only.", "[A-Z0-9]{2,5}"))
                .bind(
                        TeamEntity::getTeamTag,
                        TeamEntity::setTeamTag
                );

        binder.forField(tournament)
                .asRequired("Tournament assignment is required.")
                .withConverter(new Converter<TournamentEntity, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(TournamentEntity entity, ValueContext valueContext) {
                        try {
                            return Result.ok(entity.getTournamentId());
                        } catch (NumberFormatException e) {
                            return Result.error("Error");
                        }
                    }
                    @Override
                    public TournamentEntity convertToPresentation(Integer tournamentId, ValueContext valueContext) {
                        if(tournamentId == null) return null;
                        return tournamentRepository.findById(tournamentId).orElse(null);
                    }
                })
                .bind(
                        TeamEntity::getTournamentId,
                        TeamEntity::setTournamentId
                );

        binder.forField(mailAddress)
                .withValidator(new EmailValidator("Enter valid email address"))
                .bind(
                        TeamEntity::getMailAddress,
                        TeamEntity::setMailAddress
                );
    }

    private void SaveAction(){
        if(binder.validate().isOk()){
            try{
                teamRepository.save(binder.getBean());
            }catch (Exception e) {
                Notification.show("Error occurred during saving to database.", 4000, Notification.Position.MIDDLE).open();
            }
            CancelAction();
        }
    }

    private void CancelAction(){
        dialog.close();
        teamName.clear();
        region.clear();
        teamTag.clear();
        mailAddress.clear();
    }

    private void DeleteAction() {
        // TODO: ask for confirmation before delete
        try{
            teamRepository.delete(binder.getBean());
        }catch (Exception e) {
            Notification.show("Error occurred during deleting from database.", 3, Notification.Position.MIDDLE).open();
        }
        CancelAction();
    }

    public Dialog getDialog(){
        return dialog;
    }

}
