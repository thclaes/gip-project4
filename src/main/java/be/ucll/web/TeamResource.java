package be.ucll.web;
import be.ucll.dao.TeamRepository;
import be.ucll.dto.TeamDTO;
import be.ucll.models.Organisation;
import be.ucll.models.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/team")
public class TeamResource {

    private TeamRepository teamRepository;
    @Autowired
    public TeamResource(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PostMapping("")
    public ResponseEntity<Team> createTeam(@RequestBody TeamDTO teamDTO) throws TeamAlreadyExists, OrganisationNotFound, TeamNameIsNullOrEmpty, TeamOrganisationIsNullOrEmpty {
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) throw new TeamNameIsNullOrEmpty();

        if (teamDTO.getOrganisationId() == null || teamDTO.getOrganisationId() <= 0) throw new TeamOrganisationIsNullOrEmpty();

        if (teamRepository.findTeamByNameIgnoreCase(teamDTO.getName()).isPresent()) throw new TeamAlreadyExists(teamDTO.getName());

        if (organisationRepository.findById(teamDTO.getOrganisationId()).isPresent()){

            Organisation organisation = organisationRepository.findById(teamDTO.getOrganisationId()).get();

            Team newTeam = teamRepository.save(new Team.TeamBuilder()
                    .name(teamDTO.getName())
                    .build());
            return ResponseEntity.status(HttpStatus.CREATED).body(newTeam);

        }
        throw new OrganisationNotFound();
    }

    // team Updaten
    @PutMapping  ("/{id}")                         // localhost:8080/team?id=
    public ResponseEntity<Team> updateTeam(@PathVariable("id") Long id, @RequestBody TeamDTO teamDTO) throws TeamNotFound, TeamNameIsNullOrEmpty, OrganisationNotFound, TeamAlreadyExists {
        Team team;
        if (id <= 0) throw new TeamNotFound();
        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) throw new TeamNameIsNullOrEmpty();
        if (teamDTO.getOrganisationId() == null || teamDTO.getOrganisationId() <= 0) throw new OrganisationNotFound();
        if (teamRepository.findById(id).isPresent()){
             team = teamRepository.findById(id).get();
            if (!team.getName().equals(teamDTO.getName())){
                teamALreadyExists(teamDTO.getName());
                team.setName(teamDTO.getName());
            }
        }else{
            throw new TeamNotFound(teamDTO.getName());
        }

        if (!team.getOrganisation().getId().equals(teamDTO.getOrganisationId())){

            if (organisationRepository.findById(teamDTO.getOrganisationId()).isPresent()){
                Organisation organisation = organisationRepository.findById(teamDTO.getOrganisationId()).get();
                team.setOrganisation(organisation);
            }else{
                throw new OrganisationNotFound();
            }
        }
        teamRepository.save(team);
        return ResponseEntity.status(HttpStatus.OK).body(team);
    }
/*
    @GetMapping("{id}") // teamname veranderen naar id
    public ResponseEntity<TeamDTO> getTeam(@PathVariable("id") Long id)  throws TeamNotFound {
        if (id <= 0) throw new TeamNotFound();
        //controleren of team in onze db bestaat
        if(teamRepository.findById(id).isPresent()) { 
            //team opvragen en teruggeven
            Team team = teamRepository.findById(id).get();
            return ResponseEntity.status(HttpStatus.OK).body(new TeamDTO(team.getName(), team.getOrganisation().getName()));
        }
        throw new NotFoundException();
    }

    @DeleteMapping // id veranderd
    public ResponseEntity deleteTeam(@PathVariable("id") String teamName) throws TeamNotFound {
        //We gaan controleren of het team waarvan de teamName gegeven is, wel bestaat in onze db
        if(teamRepository.findTeamByNameIgnoreCase(teamName).isPresent()) {

            //Zo ja, dan verwijderen we deze
            teamRepository.delete(teamRepository.findTeamByNameIgnoreCase(teamName).get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        //zo niet => exception
        throw new TeamNotFound(teamName);
    }

     */

    private void teamALreadyExists(String teamName) throws TeamAlreadyExists{
        if (teamRepository.findTeamByNameIgnoreCase(teamName).isPresent()){
            throw new TeamAlreadyExists(teamName);
        }
    }

}

