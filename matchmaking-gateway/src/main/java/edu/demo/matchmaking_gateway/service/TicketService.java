package edu.demo.matchmaking_gateway.service;

import com.example.matcmaking_api.dto.player.PlayerResponse;

public interface TicketService {

    void saveTicket(String ticketId, PlayerResponse playerInfo);

    String getTicket(String ticketId);

}
