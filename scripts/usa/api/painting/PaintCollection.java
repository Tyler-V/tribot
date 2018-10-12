package scripts.usa.api.painting;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import scripts.usa.api.painting.components.PaintContainer;
import scripts.usa.api.painting.components.PaintVisibilityButton;
import scripts.usa.api.painting.interfaces.Paintable;

public class PaintCollection {

	private final List<Paintable> paintables;
	private final List<PaintContainer> paintContainers;
	private boolean visible = true;

	public PaintCollection() {
		this.paintables = new ArrayList<Paintable>();
		this.paintContainers = new ArrayList<PaintContainer>();
	}

	public final boolean isVisible() {
		return this.visible;
	}

	public final void toggleVisibility() {
		this.visible = !this.visible;
	}

	public final PaintVisibilityButton getVisibilityButton() {
		return (PaintVisibilityButton) getPaintables().stream().filter(p -> p instanceof PaintVisibilityButton).findFirst().get();
	}

	public final List<Paintable> getPaintables() {
		return this.paintables;
	}

	public final void add(Paintable... paintables) {
		getPaintables().addAll(Arrays.asList(paintables));
	}

	public final void remove(Paintable paintable) {
		getPaintables().remove(paintable);
	}

	public final List<PaintContainer> getPaintContainers() {
		return this.paintContainers;
	}

	public final void add(PaintContainer... paintBox) {
		getPaintContainers().addAll(Arrays.asList(paintBox));
	}

	public final void remove(PaintContainer paintBox) {
		getPaintContainers().remove(paintBox);
	}

	public final void draw(Graphics2D g2) {
		if (isVisible()) {
			Stream.concat(getPaintables().stream(), getPaintContainers().stream()).forEach(paintable -> paintable.paint(g2));
		}
		else {
			PaintVisibilityButton paintVisibilityButton = getVisibilityButton();
			if (paintVisibilityButton != null)
				paintVisibilityButton.paint(g2);
		}
	}
}
