package com.agh.leagueapp.views.forms;

import com.agh.leagueapp.backend.data.GameConfig;
import com.agh.leagueapp.backend.entities.GameEntity;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.DbService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameForm extends FormLayout {

    private final DbService dbService;
    private final GameEntity game;
    private final TournamentEntity tournament;
    private final boolean isEditing;

    private final Dialog dialog = new Dialog();

    private final Select<TeamEntity> blueTeam = new Select<>();
    private final Select<TeamEntity> redTeam = new Select<>();

    private final List<Select<PlayerEntity>> blueTeamParticipants = new ArrayList<>();
    private final List<Select<PlayerEntity>> redTeamParticipants = new ArrayList<>();

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");


    private final Binder<GameEntity> binder = new Binder<>(GameEntity.class);

    public GameForm(DbService dbService, TournamentEntity tournament, GameEntity game){
        this.dbService = dbService;
        this.tournament = tournament;
        isEditing = game != null;
        this.game = isEditing ? game : new GameEntity();

        setupForm();
        Bind();
        dialog.add(this);
    }

    private void setupForm(){
        blueTeam.setLabel("Blue Team");
        blueTeam.setItems(dbService.getTeamRepository().findAllByTournamentId(tournament.getTournamentId()));
        blueTeam.setItemLabelGenerator(TeamEntity::getTeamName);
        blueTeam.setEmptySelectionAllowed(false);

        redTeam.setLabel("Red Team");
        redTeam.setItems(dbService.getTeamRepository().findAllByTournamentId(tournament.getTournamentId()));
        redTeam.setItemLabelGenerator(TeamEntity::getTeamName);
        redTeam.setEmptySelectionAllowed(false);

        this.add(blueTeam, new Span(), redTeam);

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(buttonClickEvent -> SaveAction());

        cancel.addClickListener(buttonClickEvent -> CancelAction());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(buttonClickEvent -> DeleteAction());

        this.add(save, isEditing ? delete : new Span(), cancel);
        this.setResponsiveSteps(new ResponsiveStep("0", 3));
    }

    private void Bind(){
        binder.setBean(game);

        binder.forField(blueTeam)
                .asRequired("Team assignment is required.")
                .withConverter(new Converter<TeamEntity, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(TeamEntity entity, ValueContext valueContext) {
                        try {
                            return Result.ok(entity.getTeamId());
                        } catch (NumberFormatException e) {
                            return Result.error("Error");
                        }
                    }
                    @Override
                    public TeamEntity convertToPresentation(Integer teamId, ValueContext valueContext) {
                        if(teamId == null) return null;
                        return dbService.getTeamRepository().findById(teamId).orElse(null);
                    }
                })
                .bind(
                        GameEntity::getBlueTeamId,
                        GameEntity::setBlueTeamId
                );

        binder.forField(redTeam)
                .asRequired("Team assignment is required.")
                .withConverter(new Converter<TeamEntity, Integer>() {
                    @Override
                    public Result<Integer> convertToModel(TeamEntity entity, ValueContext valueContext) {
                        try {
                            return Result.ok(entity.getTeamId());
                        } catch (NumberFormatException e) {
                            return Result.error("Error");
                        }
                    }
                    @Override
                    public TeamEntity convertToPresentation(Integer teamId, ValueContext valueContext) {
                        if(teamId == null) return null;
                        return dbService.getTeamRepository().findById(teamId).orElse(null);
                    }
                })
                .withValidator((Validator<Integer>) (integer, valueContext) -> {
                    if(!Objects.equals(blueTeam.getValue().getTeamId(), redTeam.getValue().getTeamId()))
                        return ValidationResult.ok();
                    else
                        return ValidationResult.error("Blue and Red teams cannot be the same.");
                })
                .bind(
                        GameEntity::getRedTeamId,
                        GameEntity::setRedTeamId
                );
    }

    private void SaveAction(){
        if(binder.validate().isOk()){
            GameEntity game;
            try{
                game = GameConfig.InitializeInAPI(binder.getBean(), true,
                        tournament, blueTeam.getValue(), redTeam.getValue());
                try{
                    dbService.getGameRepository().save(game);
                    CancelAction();
                }catch (Exception e) {
                    e.printStackTrace();
                    Notification.show("Error occurred during saving to database.", 5000, Notification.Position.MIDDLE).open();
                }
            }
            catch (no.stelar7.api.r4j.basic.exceptions.APINoValidResponseException noValidResponseException){
                noValidResponseException.printStackTrace();
                Notification.show("There was a problem with connecting to Riot API.\n" +
                        "Possible reason in invalid/outdated API key in which case it should be renewed \n" +
                        "or problem regarding Riot servers in which case please try again in few minutes.\n" +
                        noValidResponseException.getMessage(), 5000, Notification.Position.MIDDLE);
            }
        }
    }

    private void CancelAction(){
        dialog.close();
        blueTeam.clear();
        redTeam.clear();
    }

    private void DeleteAction() {
        try{
            dbService.getGameRepository().delete(binder.getBean());
        }catch (Exception e) {
            Notification.show("Error occurred during deleting from database.", 3, Notification.Position.MIDDLE).open();
        }
        CancelAction();
    }

    public Dialog getDialog(){
        return dialog;
    }

}
