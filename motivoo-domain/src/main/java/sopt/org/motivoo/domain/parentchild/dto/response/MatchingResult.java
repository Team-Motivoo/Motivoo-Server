package sopt.org.motivoo.domain.parentchild.dto.response;

public record MatchingResult(
        boolean isMatched,
        Long userId,
        Long opponentUserId
){

}
