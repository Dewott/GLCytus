package glcytus.graphics;

import glcytus.util.ResourceLoader;
import glcytus.util.SpriteState;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MorphingAnimation {
	public double endtime = 0;
	public HashMap<String, LinkedList<Transform>> data = new HashMap<String, LinkedList<Transform>>();

	public MorphingAnimation(String folder, String filename, boolean loop) throws Exception {
		JSONObject anim = ResourceLoader.loadJSONObjectFromFile(folder, filename);
		boolean alignmentEnd = false;
		if (anim.containsKey("Alignment"))
			alignmentEnd = anim.getString("Alignment").equals("End");
		JSONObject keyframe = anim.getJSONObject("Keyframe");

		HashMap<String, SpriteState> states = new HashMap<String, SpriteState>();
		for (Map.Entry<String, Object> entry : keyframe.entrySet()) {
			String name = entry.getKey();
			JSONObject obj = (JSONObject) entry.getValue();
			SpriteState s = new SpriteState();
			if (obj.containsKey("X"))
				s.x = obj.getDoubleValue("X");
			if (obj.containsKey("Y"))
				s.y = obj.getDoubleValue("Y");
			if (obj.containsKey("Rotation"))
				s.rotationAngle[0] = Math.toRadians(obj.getDoubleValue("Rotation"));
			if (obj.containsKey("ScaleX"))
				s.sx = obj.getDoubleValue("ScaleX");
			if (obj.containsKey("ScaleY"))
				s.sy = obj.getDoubleValue("ScaleY");
			if (obj.containsKey("Red"))
				s.color[0] = obj.getDoubleValue("Red");
			if (obj.containsKey("Green"))
				s.color[1] = obj.getDoubleValue("Green");
			if (obj.containsKey("Blue"))
				s.color[2] = obj.getDoubleValue("Blue");
			if (obj.containsKey("Alpha"))
				s.color[3] = obj.getDoubleValue("Alpha");
			states.put(name, s);
		}

		JSONObject anim1 = anim.getJSONObject("Animations");
		for (Map.Entry<String, Object> entry : anim1.entrySet()) {
			String name = entry.getKey();
			JSONObject trans = (JSONObject) entry.getValue();
			SpriteState s = states.get(name);

			for (Map.Entry<String, Object> entry2 : trans.entrySet()) {
				String str = entry2.getKey();
				JSONArray arr = (JSONArray) entry2.getValue();
				int type = 0;
				double sval = 0, time = 0;
				switch (str) {
				case "X":
					type = Transform.TRANS_X;
					sval = s.x;
					break;
				case "Y":
					type = Transform.TRANS_Y;
					sval = s.y;
					break;
				case "Alpha":
					type = Transform.ALPHA;
					sval = s.color[3];
					break;
				case "Rotation":
					type = Transform.ROTATION;
					sval = s.rotationAngle[0];
					break;
				case "ScaleX":
					type = Transform.SX;
					sval = s.sx;
					break;
				case "ScaleY":
					type = Transform.SY;
					sval = s.sy;
					break;
				case "Red":
					type = Transform.COLOR_RED;
					sval = s.color[0];
					break;
				case "Green":
					type = Transform.COLOR_GREEN;
					sval = s.color[1];
					break;
				case "Blue":
					type = Transform.COLOR_BLUE;
					sval = s.color[2];
					break;
				}
				LinkedList<Transform> translist = new LinkedList<Transform>();
				LoopTransform lt = new LoopTransform();
				for (Object obj : arr) {
					JSONObject child = (JSONObject) obj;
					double dur = child.getDoubleValue("Duration");
					double delta = child.getDoubleValue("Delta");
					if (type == Transform.ROTATION)
						delta = Math.toRadians(delta);
					Transform t = new Transform(type, Transform.LINEAR, time, time + dur, sval, sval + delta, false,
							false);
					if (loop)
						lt.addTransform(t);
					else
						translist.add(t);
					time += dur;
					sval += delta;
				}
				if (loop)
					translist.add(lt);
				else {
					// translist.getFirst().lborder = true;
					translist.getLast().rborder = true;
				}
				if (time > endtime)
					endtime = time;
				if (this.data.containsKey(name)) {
					LinkedList<Transform> old = this.data.get(name);
					old.addAll(translist);
				} else
					this.data.put(name, translist);
			}
		}
	}

	public void use(String name, Sprite s, double offset) {
		LinkedList<Transform> trans = data.get(name);
		if (trans != null)
			for (Transform t : trans) {
				Transform copy = t.clone();
				if (t instanceof LoopTransform)
					for (Transform tc : ((LoopTransform) copy).child) {
						tc.stime += offset;
						tc.etime += offset;
					}
				copy.stime += offset;
				copy.etime += offset;
				s.addTransform(copy);
			}
	}

	public void use(HashMap<String, Sprite> elements, double offset) {
		for (Map.Entry<String, Sprite> entry : elements.entrySet())
			use(entry.getKey(), entry.getValue(), offset);
	}
}