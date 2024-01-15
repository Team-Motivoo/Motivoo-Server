package sopt.org.motivooServer.domain.auth.controller.apple;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKeys;


@FeignClient(name = "apple-public-verify-client", url = "https://appleid.apple.com/auth")
public interface AppleApiClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}
