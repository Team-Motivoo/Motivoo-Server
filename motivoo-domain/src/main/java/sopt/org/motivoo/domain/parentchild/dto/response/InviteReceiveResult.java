package sopt.org.motivoo.domain.parentchild.dto.response;

public record InviteReceiveResult(
        Long userId,
        Long opponentUserId,
        boolean isMatched
){

}
