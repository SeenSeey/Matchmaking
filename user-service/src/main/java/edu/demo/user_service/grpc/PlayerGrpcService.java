package edu.demo.user_service.grpc;

import edu.demo.user_service.model.Player;
import edu.demo.user_service.proto.*;
import edu.demo.user_service.service.PlayerService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class PlayerGrpcService extends PlayerServiceGrpc.PlayerServiceImplBase {

    private final PlayerService playerService;

    @Autowired
    public PlayerGrpcService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Override
    public void createPlayer(CreatePlayerRequest request,
                             StreamObserver<PlayerResponse> responseObserver) {
        try {
            Player player = playerService.createPlayer(
                    request.getNickname(),
                    request.getRegion(),
                    request.getRating()
            );

            PlayerResponse response = PlayerResponse.newBuilder()
                    .setPlayerId(player.getId())
                    .setNickname(player.getNickname())
                    .setRating(player.getRating())
                    .setRegion(player.getRegion())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to create player: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getPlayerById(GetPlayerByIdRequest request,
                              StreamObserver<PlayerResponse> responseObserver) {
        try {
            Player player = playerService.getPlayerById(request.getPlayerId());

            PlayerResponse response = PlayerResponse.newBuilder()
                    .setPlayerId(player.getId())
                    .setNickname(player.getNickname())
                    .setRating(player.getRating())
                    .setRegion(player.getRegion())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            responseObserver.onError(io.grpc.Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Failed to get player: " + e.getMessage())
                    .asRuntimeException());
        }
    }
}
