package hits;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EndpointHitController {

    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EndpointHitService service;

    @PostMapping("/hit")
    public EndpointHit addEndpointHit(@RequestBody EndpointHit endpointHit) {
        return service.addEndpointHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @NotNull String start,
                                    @RequestParam @NotNull String end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        return service.getStats(uris, LocalDateTime.parse(start, DATE), LocalDateTime.parse(end, DATE), unique);
    }

}
