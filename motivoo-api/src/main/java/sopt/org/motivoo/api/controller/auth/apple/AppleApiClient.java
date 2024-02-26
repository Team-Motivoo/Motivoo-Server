package sopt.org.motivooServer.domain.auth.controller.apple;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import sopt.org.motivooServer.domain.auth.dto.response.apple.ApplePublicKeys;


@FeignClient(name = "apple-public-key-client", url = "https://appleid.apple.com/auth")
public interface AppleClient {

    @GetMapping("/keys")
    ApplePublicKeys getApplePublicKeys();
}