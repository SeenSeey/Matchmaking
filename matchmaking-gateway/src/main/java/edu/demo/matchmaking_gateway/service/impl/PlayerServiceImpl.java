package edu.demo.matchmaking_gateway.service.impl;

import com.example.matcmaking_api.dto.player.PlayerRequest;
import com.example.matcmaking_api.dto.player.PlayerResponse;
import edu.demo.matchmaking_gateway.proto.CreatePlayerRequest;
import edu.demo.matchmaking_gateway.proto.GetPlayerByIdRequest;
import edu.demo.matchmaking_gateway.proto.PlayerServiceGrpc;
import edu.demo.matchmaking_gateway.service.PlayerService;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);
    
    @GrpcClient("user-service")
    private PlayerServiceGrpc.PlayerServiceBlockingStub playerServiceStub;

    @Override
    public PlayerResponse create(PlayerRequest request) {
        try {
            CreatePlayerRequest grpcRequest = CreatePlayerRequest.newBuilder()
                    .setNickname(request.nickname())
                    .setRegion(request.region())
                    .setRating(request.rating())
                    .build();

            edu.demo.matchmaking_gateway.proto.PlayerResponse grpcResponse = 
                    playerServiceStub.createPlayer(grpcRequest);

            PlayerResponse response = new PlayerResponse(
                    grpcResponse.getPlayerId(),
                    grpcResponse.getNickname(),
                    grpcResponse.getRating(),
                    grpcResponse.getRegion()
            );
            
            logger.info("Игрок создан: id={}, nickname={}, region={}, rating={}", 
                    response.getPlayerId(), response.getNickname(), response.getRegion(), response.getRating());
            
            return response;
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Failed to create player via gRPC: " + e.getMessage(), e);
        }
    }

    @Override
    public PlayerResponse getById(String id) {
        try {
            GetPlayerByIdRequest grpcRequest = GetPlayerByIdRequest.newBuilder()
                    .setPlayerId(id)
                    .build();

            edu.demo.matchmaking_gateway.proto.PlayerResponse grpcResponse = 
                    playerServiceStub.getPlayerById(grpcRequest);

            PlayerResponse response = new PlayerResponse(
                    grpcResponse.getPlayerId(),
                    grpcResponse.getNickname(),
                    grpcResponse.getRating(),
                    grpcResponse.getRegion()
            );
            
            logger.info("Игрок получен: id={}, nickname={}, region={}, rating={}", 
                    response.getPlayerId(), response.getNickname(), response.getRegion(), response.getRating());
            
            return response;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == io.grpc.Status.Code.NOT_FOUND) {
                throw new RuntimeException("Player not found: " + id, e);
            }
            throw new RuntimeException("Failed to get player via gRPC: " + e.getMessage(), e);
        }
    }
}