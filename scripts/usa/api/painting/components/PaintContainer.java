package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.models.PaintColumns;

public class PaintContainer implements Paintable {
	private final PaintColumns paintColumns;
	private final Rectangle bounds;
	private final int padding;
	private final List<PaintBoxString> paintBoxStrings;
	private Color textColor;
	private Color backgroundColor;
	private Color borderColor;

	public PaintContainer(PaintColumns paintColumns, Rectangle bounds, int padding, Color textColor, Color backgroundColor, Color borderColor,
			PaintBoxString... paintBoxStrings) {
		this.paintColumns = paintColumns;
		this.bounds = bounds;
		this.padding = padding;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.paintBoxStrings = new ArrayList<>(Arrays.asList(paintBoxStrings));
		setColors();
		setPositions();
	}

	public PaintContainer(PaintColumns paintColumns, Rectangle bounds, Color textColor, Color backgroundColor, Color borderColor,
			PaintBoxString... paintBoxStrings) {
		this(paintColumns, bounds, Painter.PADDING, textColor, backgroundColor, borderColor, paintBoxStrings);
	}

	public PaintContainer(PaintColumns paintColumns, Rectangle bounds, PaintBoxString... paintBoxStrings) {
		this(paintColumns, bounds, Painter.PADDING, Painter.Colors.COLOR, Painter.Colors.BACKGROUND_COLOR, Painter.Colors.BORDER_COLOR,
				paintBoxStrings);
	}

	public PaintContainer(PaintBoxString... paintBoxStrings) {
		this(PaintColumns.AUTO, Painter.Bounds.GAME_CHAT_BOUNDS, Painter.PADDING, Painter.Colors.COLOR, Painter.Colors.BACKGROUND_COLOR,
				Painter.Colors.BORDER_COLOR, paintBoxStrings);
	}

	private void setColors() {
		this.paintBoxStrings.stream().forEach(paintBoxString -> {
			paintBoxString.setTextColor(textColor);
			paintBoxString.setBackgroundColor(backgroundColor);
			paintBoxString.setBorderColor(borderColor);
		});
	}

	private int autosizeColumns() {
		int y = bounds.y;
		int columns = 1;
		for (PaintBoxString paintBoxString : paintBoxStrings) {
			if ((y + padding + paintBoxString.getHeight()) >= (bounds.y + bounds.height)) {
				columns++;
				y = bounds.y;
			}
			y += padding + paintBoxString.getHeight();
		}
		return columns;
	}

	private void setPositions() {
		int x = bounds.x;
		int y = bounds.y;
		int columns = paintColumns.get() > 0 ? paintColumns.get() : autosizeColumns();
		int width = (bounds.width - (padding * (columns + 1))) / columns;
		for (int i = 0; i < paintBoxStrings.size(); i++) {
			PaintBoxString paintBoxString = paintBoxStrings.get(i);
			paintBoxString.setBounds(new Rectangle(x + padding, y + padding, width, paintBoxString.getHeight()));
			if (columns > 1) {
				if ((i + 1) % columns == 0) {
					x = bounds.x;
					y += padding + paintBoxString.getHeight();
				}
				else {
					x += width + padding;
				}
			}
			else {
				y += padding + paintBoxString.getHeight();
			}
		}
	}

	@Override
	public void paint(Graphics2D g2) {
		paintBoxStrings.stream().forEach(paintBoxString -> paintBoxString.paint(g2));
	}
}
