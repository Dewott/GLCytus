package glcytus.graphics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MorphingAnimation {
	public static double read(String filepath,
			HashMap<String, Sprite> elements, boolean loop, double offset)
			throws Exception {
		JSONObject anim = JSON.parseObject(readFile(filepath));
		JSONObject keyframe = anim.getJSONObject("Keyframe");
		double endtime = 0;
		for (Map.Entry<String, Object> entry : keyframe.entrySet()) {
			String name = entry.getKey();
			JSONObject obj = (JSONObject) entry.getValue();
			Sprite s = elements.get(name);
			if (obj.containsKey("X"))
				s.x = obj.getDoubleValue("X");
			if (obj.containsKey("Y"))
				s.y = obj.getDoubleValue("Y");
			if (obj.containsKey("Alpha"))
				s.alpha = obj.getDoubleValue("Alpha");
			if (obj.containsKey("Rotation"))
				s.angle = Math.toRadians(obj.getDoubleValue("Rotation"));
			if (obj.containsKey("ScaleX"))
				s.sx = obj.getDoubleValue("ScaleX");
			if (obj.containsKey("ScaleY"))
				s.sy = obj.getDoubleValue("ScaleY");
		}

		JSONObject anim1 = anim.getJSONObject("Animations");
		for (Map.Entry<String, Object> entry : anim1.entrySet()) {
			String name = entry.getKey();
			JSONObject trans = (JSONObject) entry.getValue();
			Sprite s = elements.get(name);

			for (Map.Entry<String, Object> entry2 : trans.entrySet()) {
				String str = entry2.getKey();
				JSONArray arr = (JSONArray) entry2.getValue();
				int type = 0;
				double sval = 0, time = offset;
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
					sval = s.alpha;
					break;
				case "Rotation":
					type = Transform.ROTATION;
					sval = s.angle;
					break;
				case "ScaleX":
					type = Transform.SX;
					sval = s.sx;
					break;
				case "ScaleY":
					type = Transform.SY;
					sval = s.sy;
					break;
				}
				LoopTransform lt = new LoopTransform();
				for (Object obj : arr) {
					JSONObject child = (JSONObject) obj;
					double dur = child.getDoubleValue("Duration");
					double delta = child.getDoubleValue("Delta");
					if (type == Transform.ROTATION)
						delta = Math.toRadians(delta);
					Transform t = new Transform(type, Transform.LINEAR, time,
							time + dur, sval, sval + delta, false, false);
					if (loop)
						lt.addTransform(t);
					else
						s.addTransform(t);
					time += dur;
					sval += delta;
				}
				if (loop)
					s.addTransform(lt);
				else {
					s.trans.getFirst().lborder = true;
					s.trans.getLast().rborder = true;
				}
				if (time > endtime)
					endtime = time;
			}
		}
		return endtime;
	}

	private static String readFile(String path) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(path));
		String str = "";
		String s = in.readLine();
		while (s != null) {
			str += s;
			s = in.readLine();
		}
		in.close();
		return str;
	}
}