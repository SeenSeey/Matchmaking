package edu.demo.game_gateway.handler;

import org.springframework.web.socket.WebSocketSession;

class PlayerSessionInfo {
    private final String playerId;
    private final String ticketId;
    private final WebSocketSession session;
    private String matchId;
    private final String region;

    public PlayerSessionInfo(String playerId, String ticketId, WebSocketSession session, String region) {
        this.playerId = playerId;
        this.ticketId = ticketId;
        this.session = session;
        this.region = region;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getRegion() {
        return region;
    }
}
