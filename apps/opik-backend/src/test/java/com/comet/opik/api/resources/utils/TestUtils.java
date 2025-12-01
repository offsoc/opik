package com.comet.opik.api.resources.utils;

import com.comet.opik.utils.JsonUtils;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import ru.vyarus.dropwizard.guice.test.ClientSupport;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class TestUtils {

    public static UUID getIdFromLocation(URI location) {
        return UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));
    }

    public static String toURLEncodedQueryParam(List<?> filters) {
        if (CollectionUtils.isEmpty(filters)) {
            return null;
        }
        // URLEncoder.encode() uses '+' for spaces, but for query parameters we need '%20'
        // Replace '+' with '%20' to ensure spaces are properly decoded by Jersey
        return URLEncoder.encode(JsonUtils.writeValueAsString(filters), StandardCharsets.UTF_8)
                .replace("+", "%20");
    }

    public static String getBaseUrl(ClientSupport client) {
        return "http://localhost:%d".formatted(client.getPort());
    }
}
