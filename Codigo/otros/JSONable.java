package otros;

import org.json.simple.JSONObject;

public interface JSONable {
	
	public void SetJSONObject(JSONObject json);
	public JSONObject toJSON();
	public Object getPrimaryKey();
}
