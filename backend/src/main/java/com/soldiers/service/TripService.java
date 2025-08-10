package com.soldiers.service;

import com.soldiers.entity.Trip;
import com.soldiers.entity.Trip.TripStatus;
import com.soldiers.entity.User;
import com.soldiers.entity.Budget;
import com.soldiers.entity.Player;
import com.soldiers.entity.Team;

import com.soldiers.repository.TripRepository;
import com.soldiers.repository.UserRepository;
import com.soldiers.repository.PlayerRepository;
import com.soldiers.repository.TeamRepository;
import com.soldiers.dto.request.TripRequest;

import com.soldiers.dto.response.TripResponse;
import com.soldiers.dto.response.BudgetResponse;
import com.soldiers.service.BudgetService;
import com.soldiers.service.PlayerService;
import com.soldiers.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetService budgetService;
    
    @Autowired
    private PlayerRepository playerRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    

    
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getTripsByStatus(TripStatus status) {
        return tripRepository.findByStatusOrderByDepartureDateDesc(status).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getUpcomingTrips() {
        return tripRepository.findUpcomingTrips(LocalDateTime.now()).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TripResponse> getPastTrips() {
        return tripRepository.findPastTrips(LocalDateTime.now()).stream()
                .map(TripResponse::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        return new TripResponse(trip);
    }
    
    @Transactional
    public TripResponse createTrip(TripRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        Trip trip = new Trip();
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setReturnDate(request.getReturnDate());
        trip.setStatus(request.getStatus());
        trip.setUser(user);
        trip.setNotes(request.getNotes());
        trip.setInitialCost(request.getInitialCost() != null ? request.getInitialCost() : BigDecimal.ZERO);
        trip.setTotalCost(BigDecimal.ZERO); // Garantir que totalCost seja inicializado
        
        // Adicionar jogadores se especificados
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + playerId));
                trip.addPlayer(player);
            }
        }
        
        // Adicionar times se especificados
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findById(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                trip.addTeam(team);
            }
        }
        
        Trip savedTrip = tripRepository.save(trip);
        
        // Removido: criação duplicada do gasto inicial no orçamento geral
        // Agora apenas o TripController cria via TripBudgetService
        
        return new TripResponse(savedTrip);
    }
    
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        
        trip.setDestination(request.getDestination());
        trip.setDescription(request.getDescription());
        trip.setDepartureDate(request.getDepartureDate());
        trip.setReturnDate(request.getReturnDate());
        trip.setStatus(request.getStatus());
        trip.setNotes(request.getNotes());
        trip.setInitialCost(request.getInitialCost() != null ? request.getInitialCost() : BigDecimal.ZERO);
        
        // Limpar e adicionar jogadores
        trip.getPlayers().clear();
        if (request.getPlayerIds() != null && !request.getPlayerIds().isEmpty()) {
            for (Long playerId : request.getPlayerIds()) {
                Player player = playerRepository.findById(playerId)
                        .orElseThrow(() -> new RuntimeException("Jogador não encontrado: " + playerId));
                trip.addPlayer(player);
            }
        }
        
        // Limpar e adicionar times
        trip.getTeams().clear();
        if (request.getTeamIds() != null && !request.getTeamIds().isEmpty()) {
            for (Long teamId : request.getTeamIds()) {
                Team team = teamRepository.findById(teamId)
                        .orElseThrow(() -> new RuntimeException("Time não encontrado: " + teamId));
                trip.addTeam(team);
            }
        }
        
        Trip updatedTrip = tripRepository.save(trip);
        return new TripResponse(updatedTrip);
    }
    
    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada"));
        
        // Remove os gastos relacionados do orçamento geral antes de excluir a viagem
        removeTripExpensesFromGeneralBudget(trip);
        
        tripRepository.delete(trip);
    }
    
    /**
     * Remove todos os gastos relacionados à viagem do orçamento geral
     */
    private void removeTripExpensesFromGeneralBudget(Trip trip) {
        try {
            // Busca todas as entradas no orçamento geral relacionadas a esta viagem
            List<BudgetResponse> allBudgets = budgetService.getAllBudgets();
            List<BudgetResponse> tripBudgets = allBudgets.stream()
                .filter(response -> {
                    // Filtra entradas relacionadas à viagem
                    String notes = response.getNotes();
                    String description = response.getDescription();
                    
                    return (notes != null && notes.contains("viagem ID: " + trip.getId())) ||
                           (description != null && description.contains("Viagem para " + trip.getDestination()));
                })
                .collect(Collectors.toList());
            
            // Remove cada entrada encontrada usando o método de força
            for (BudgetResponse budgetResponse : tripBudgets) {
                try {
                    budgetService.forceDeleteTripRelatedBudget(budgetResponse.getId());
                    System.out.println("Removida entrada do orçamento geral ID: " + budgetResponse.getId() + 
                                     " para viagem ID: " + trip.getId());
                } catch (Exception e) {
                    // Se não conseguir remover uma entrada específica, apenas loga o erro mas continua
                    System.err.println("Erro ao remover entrada do orçamento geral ID: " + budgetResponse.getId() + 
                                     " para viagem ID: " + trip.getId() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Processadas " + tripBudgets.size() + " entradas do orçamento geral para viagem ID: " + trip.getId());
            
        } catch (Exception e) {
            // Se houver erro geral, apenas loga mas não falha a exclusão da viagem
            System.err.println("Erro ao processar remoção de gastos da viagem do orçamento geral: " + e.getMessage());
        }
    }
}
