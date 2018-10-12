package scripts.usa.api.responder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class Session {
    public final Map<String, String> vars;
    public final String botid = "b0dafd24ee35a477";

    public Session() {
	vars = new LinkedHashMap<String, String>();
	vars.put("botid", botid);
	vars.put("custid", UUID.randomUUID().toString());
    }
}
