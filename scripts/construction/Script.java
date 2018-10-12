package scripts.construction;

import java.awt.Graphics;
import java.awt.Graphics2D;

import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import scripts.construction.data.Furniture;
import scripts.construction.data.Vars;
import scripts.construction.paint.Paint;
import scripts.construction.tasks.Build;
import scripts.construction.tasks.Phials;
import scripts.usa.api.antiban.ABC;
import scripts.usa.framework.ScriptVars;
import scripts.usa.framework.task.TaskScript;
import scripts.usa.painting.PaintUtils;
import scripts.usa.painting.Painter;

@ScriptManifest(authors = { "Usa" }, category = "Construction", name = "USA Construction")
public class Script extends TaskScript implements Painting {

	Painter painter;

	@Override
	protected ScriptVars setVars() {
		return new Vars(this);
	}

	@Override
	public void onStart() {
		ABC.setSleepReaction(false);
		painter = new Paint();
		Vars.get().furniture = Furniture.OAK_LARDER;
		this.getTaskSet().addAll(new Build(), new Phials());
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPaint(Graphics g) {
		try {
			Graphics2D g2 = PaintUtils.create2D(g);
			painter.onPaint(g2);
			g2.dispose();
		}
		catch (Exception e) {
		}
	}

}
