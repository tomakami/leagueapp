package com.agh.leagueapp.views.players;

import com.agh.leagueapp.backend.data.PlayerConfig;
import com.agh.leagueapp.backend.entities.PlayerEntity;
import com.agh.leagueapp.backend.entities.TeamEntity;
import com.agh.leagueapp.backend.entities.TournamentEntity;
import com.agh.leagueapp.backend.repositories.PlayerRepository;
import com.agh.leagueapp.backend.repositories.TeamRepository;
import com.agh.leagueapp.backend.repositories.TournamentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
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
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;

public class PlayerDetails extends FormLayout {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final PlayerEntity player;
    private final boolean isEditing;

    private final Dialog dialog = new Dialog();

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField indexNumber = new TextField("Index Number");


    private final Select<TeamEntity> team = new Select<>();
    private final TextField tournament = new TextField("Tournament");
    private final TextField region = new TextField("Region");

    private final TextField summonerName = new TextField("Summoner Name");
    private final Select<String> role = new Select<>();

    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Button delete = new Button("Delete");


    private final Binder<PlayerEntity> binder = new Binder<>(PlayerEntity.class);

    public PlayerDetails(TournamentRepository tournamentRepository, TeamRepository teamRepository,
                         PlayerRepository playerRepository, PlayerEntity player){
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        isEditing = player != null;
        this.player = isEditing ? player : new PlayerEntity();

        setupForm();
        Bind();
        dialog.add(this);
    }

    private void setupForm(){
        team.setLabel("Team");
        team.setItems(teamRepository.findAll());
        team.setItemLabelGenerator(TeamEntity::getTeamName);
        team.setEmptySelectionAllowed(false);

        team.addValueChangeListener(click -> {
            if (team.getValue() != null) {
                region.setValue(tournamentRepository.findById(team.getValue().getTournamentId()).get().getRegion().prettyName());
                tournament.setValue(tournamentRepository.findById(team.getValue().getTournamentId()).get().getTournamentName());
            }
        });

        region.setReadOnly(true);
        tournament.setReadOnly(true);

        role.setLabel("Role");
        role.setPlaceholder("Unselected");
        role.setItems("Top", "Jungle", "Middle", "Bottom", "Support", "Fill", "Unselected");

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(buttonClickEvent -> SaveAction());

        cancel.addClickListener(buttonClickEvent -> CancelAction());

        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(buttonClickEvent -> DeleteAction());

        this.add(firstName, lastName, indexNumber, team, region, tournament, summonerName, role, new Span(), save, cancel);
        if (isEditing) this.add(delete);
        this.setResponsiveSteps(new ResponsiveStep("0", 3));
    }

    private void Bind(){
        binder.setBean(player);


        binder.forField(firstName)
                .asRequired("Name is required")
                .bind(
                        PlayerEntity::getFirstName,
                        PlayerEntity::setFirstName
                );

        binder.forField(lastName)
                .asRequired("Name is required")
                .bind(
                        PlayerEntity::getLastName,
                        PlayerEntity::setLastName
                );

        binder.forField(indexNumber)
                .bind(
                        PlayerEntity::getIndexNumber,
                        PlayerEntity::setIndexNumber
                );

        binder.forField(team)
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
                        return teamRepository.findById(teamId).orElse(null);
                    }
                })
                .bind(
                        PlayerEntity::getTeamId,
                        PlayerEntity::setTeamId
                );

        binder.forField(summonerName)
                .asRequired("Summoner Name required")
                .bind(
                        PlayerEntity::getSummonerName,
                        PlayerEntity::setSummonerName
                );

        binder.forField(role)
                .bind(
                        PlayerEntity::getPosition,
                        PlayerEntity::setPosition
                );

    }

    private void SaveAction(){
        if(binder.validate().isOk()){
            LeagueShard shard = LeagueShard.fromString(region.getValue()).orElse(LeagueShard.UNKNOWN);
            PlayerEntity player;
            try{
                player = PlayerConfig.InitializeInAPI(binder.getBean(), shard);

                try{
                    playerRepository.save(player);
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
        firstName.clear();
        lastName.clear();
        indexNumber.clear();
        team.clear();
        region.clear();
        tournament.clear();
    }

    private void DeleteAction() {
        // TODO: ask for confirmation before delete
        try{
            playerRepository.delete(binder.getBean());
        }catch (Exception e) {
            Notification.show("Error occurred during deleting from database.", 3, Notification.Position.MIDDLE).open();
        }
        CancelAction();
    }

    public Dialog getDialog(){
        return dialog;
    }

}
