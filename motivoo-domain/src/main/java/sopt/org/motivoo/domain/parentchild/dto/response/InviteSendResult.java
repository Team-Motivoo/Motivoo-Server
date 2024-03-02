package sopt.org.motivoo.domain.parentchild.dto.response;

public record InviteSendResult(
        Long userId,
        boolean isMatched,
        String inviteCode,
        Long parentchildId
){

}
