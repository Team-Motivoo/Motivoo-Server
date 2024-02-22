package fixture;

import lombok.val;
import sopt.org.motivoo.domain.parentchild.entity.Parentchild;

public class ParentchildFixture {
    private static final boolean isMatched = true;
    private static final String inviteCode = "abcdef12";

    public static Parentchild createParentchild(){
        val parentchild = Parentchild.builder()
                .isMatched(isMatched)
                .inviteCode(inviteCode)
                .build();

        return parentchild;
    }
}
