package com.yeriix.tftstats.staticdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** Loads champion, item and trait metadata (names, costs, icons) once at startup. */
@Service
public class StaticDataService {

    private static final Logger log = LoggerFactory.getLogger(StaticDataService.class);

    private final Map<String, StaticDataFile.Entry> champions;
    private final Map<String, StaticDataFile.Entry> items;
    private final Map<String, StaticDataFile.Entry> traits;

    public StaticDataService() {
        JsonMapper mapper = JsonMapper.builder().build();
        this.champions = load(mapper, "static-data/champion.json");
        this.items = load(mapper, "static-data/item.json");
        this.traits = load(mapper, "static-data/trait.json");
        log.info("Static data loaded: {} champions, {} items, {} traits",
                champions.size(), items.size(), traits.size());
    }

    public Optional<StaticDataFile.Entry> champion(String id) {
        return Optional.ofNullable(champions.get(key(id)));
    }

    public Optional<StaticDataFile.Entry> item(String id) {
        return Optional.ofNullable(items.get(key(id)));
    }

    public Optional<StaticDataFile.Entry> trait(String id) {
        return Optional.ofNullable(traits.get(key(id)));
    }

    /** Readable name for ids that are missing from the static data. */
    public static String fallbackName(String id) {
        if (id == null) {
            return "Unknown";
        }
        return id.replaceFirst("^(TFT\\d*_|DA_\\d+_|DA_)", "")
                .replaceFirst("^Item_", "")
                .replace('_', ' ');
    }

    private static String key(String id) {
        return id == null ? "" : id.toLowerCase(Locale.ROOT);
    }

    private static Map<String, StaticDataFile.Entry> load(JsonMapper mapper, String path) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            StaticDataFile file = mapper.readValue(in, StaticDataFile.class);
            Map<String, StaticDataFile.Entry> byId = new HashMap<>();
            file.data().values().forEach(entry -> byId.putIfAbsent(key(entry.id()), entry));
            return byId;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load static data file " + path, e);
        }
    }
}