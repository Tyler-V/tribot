package scripts.usa.api2007;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Camera.ROTATION_METHOD;
import org.tribot.api2007.Player;

import scripts.usa.api.condition.Condition;

/**
 * @author Usa / Final Calibur / WastedBro
 */
public class Camera2 {

	private final int ROTATION_THRESHOLD = 30;
	private final int ANGLE_THRESHOLD = 10;
	private final int ANGLE_DEGREE_MODIFIER = 6;
	private final int CAMERA_ERROR_THRESHOLD = 3;

	public Camera2() {
		Camera.setRotationMethod(ROTATION_METHOD.ONLY_KEYS);
	}

	private enum CameraType {
		Angle,
		Rotation
	}

	/**
	 * Sets the camera angle.
	 * 
	 * @param asynchronous
	 * @param angle
	 * @param rotation
	 * @return
	 */
	public boolean setCamera(boolean asynchronous, int angle, int rotation) {
		if (isOutsideThreshold(CameraType.Angle, angle) || isOutsideThreshold(CameraType.Rotation, rotation)) {
			startThread(CameraType.Angle, angle);
			startThread(CameraType.Rotation, rotation);
			if (!asynchronous)
				waitForCamera(angle, rotation);
		}
		return true;
	}

	/**
	 * Sets the camera angle synchronously.
	 * 
	 * @param angle
	 * @param rotation
	 * @return
	 */
	public boolean setCamera(int angle, int rotation) {
		return setCamera(false, angle, rotation);
	}

	/**
	 * Sets the camera rotation asynchronously.
	 * 
	 * @param asynchronous
	 * @param rotation
	 * @return
	 */
	public boolean setRotation(boolean asynchronous, int rotation) {
		if (isOutsideThreshold(CameraType.Rotation, rotation)) {
			startThread(CameraType.Rotation, rotation);
			if (!asynchronous)
				waitFor(CameraType.Rotation, rotation);
		}
		return true;
	}

	/**
	 * Sets the camera rotation synchronously.
	 * 
	 * @param rotation
	 * @return
	 */
	public boolean setRotation(int rotation) {
		return setRotation(false, rotation);
	}

	/**
	 * Sets the Camera angle asynchronously.
	 * 
	 * @param asynchronous
	 * @param angle
	 * @return
	 */
	public boolean setAngle(boolean asynchronous, int angle) {
		if (isOutsideThreshold(CameraType.Angle, angle)) {
			startThread(CameraType.Angle, angle);
			if (!asynchronous)
				waitFor(CameraType.Angle, angle);
		}
		return true;
	}

	/**
	 * Sets the camera angle synchronously.
	 * 
	 * @param rotation
	 * @return
	 */
	public boolean setAngle(int angle) {
		return setAngle(false, angle);
	}

	/**
	 * Adjusts the camera to the target. The camera angle will be lower the further
	 * the player is away from the target.
	 * 
	 * @param asynchronous
	 * @param tile
	 * @return
	 */
	public boolean adjustTo(boolean asynchronous, Positionable tile) {
		int angle = getAngleTo(tile);
		int rotation = Camera.getTileAngle(tile) + General.random(-ROTATION_THRESHOLD, ROTATION_THRESHOLD);
		return setCamera(asynchronous, angle, rotation);
	}

	/**
	 * Adjusts the camera to the target synchronously. The camera angle will be
	 * lower the further the player is away from the target.
	 * 
	 * @param tile
	 * @return
	 */
	public boolean adjustTo(Positionable tile) {
		return adjustTo(false, tile);
	}

	/**
	 * Rotates the camera to the target asynchronously.
	 * 
	 * @param asynchronous
	 * @param tile
	 * @return
	 */
	public boolean turnTo(boolean asynchronous, Positionable tile) {
		int rotation = getRotationTo(tile);
		return setRotation(asynchronous, rotation);
	}

	/**
	 * Rotates the camera to the target synchronously.
	 * 
	 * @param asynchronous
	 * @param tile
	 * @return
	 */
	public boolean turnTo(Positionable tile) {
		return turnTo(false, tile);
	}

	/**
	 * Generates an angle 6 degrees lower for each tile from the target. Returns an
	 * angle between 33 and 100 degrees.
	 * 
	 * @param tile
	 * @return
	 */
	public int getAngleTo(Positionable tile) {
		int distance = Player.getPosition().distanceTo(tile);
		int angle = 100 - (distance * ANGLE_DEGREE_MODIFIER);
		return generateAngle(angle);
	}

	public int generateAngle(int angle) {
		return Math.max(33, Math.min(100, angle + General.random(ANGLE_DEGREE_MODIFIER, ANGLE_DEGREE_MODIFIER)));
	}

	/**
	 * Generates a random rotation value based on the camera angle to the tile.
	 * 
	 * @param tile
	 * @return
	 */
	public int getRotationTo(Positionable tile) {
		return Camera.getTileAngle(tile) + General.random(-ROTATION_THRESHOLD, ROTATION_THRESHOLD);
	}

	private boolean isOutsideThreshold(CameraType type, int value) {
		switch (type) {
			case Angle:
				return Math.abs(value - Camera.getCameraAngle()) > ANGLE_THRESHOLD;
			case Rotation:
				return Math.abs(value - Camera.getCameraRotation()) > ROTATION_THRESHOLD;
			default:
				return false;
		}
	}

	private boolean isSet(CameraType type, int value) {
		switch (type) {
			case Angle:
				return Math.abs(value - Camera.getCameraAngle()) <= CAMERA_ERROR_THRESHOLD;
			case Rotation:
				return Math.abs(value - Camera.getCameraRotation()) <= CAMERA_ERROR_THRESHOLD;
			default:
				return false;
		}
	}

	private boolean startThread(CameraType type, int value) {
		switch (type) {
			case Angle:
				Thread angle = new Thread(new AngleThread(value));
				angle.start();
				break;
			case Rotation:
				Thread rotation = new Thread(new RotationThread(value));
				rotation.start();
				break;
		}
		return true;
	}

	private boolean waitForCamera(int angle, int rotation) {
		Condition.wait(() -> isSet(CameraType.Angle, angle) && isSet(CameraType.Rotation, rotation));
		System.out.println("Synchronous camera movement complete.");
		return isSet(CameraType.Angle, angle) && isSet(CameraType.Rotation, rotation);
	}

	private boolean waitFor(CameraType type, int value) {
		Condition.wait(() -> isSet(type, value));
		System.out.println("Synchronous camera movement complete.");
		return isSet(type, value);
	}

	private class RotationThread implements Runnable {
		private int rotation;

		RotationThread(int rotation) {
			this.rotation = rotation;
		}

		public void run() {
			System.out.println("Changing Camera rotation from " + Camera.getCameraRotation() + " to " + rotation + ".");
			try {
				Camera.setCameraRotation(rotation);
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private class AngleThread implements Runnable {
		private volatile int angle;

		AngleThread(int angle) {
			this.angle = angle;
		}

		public void run() {
			System.out.println("Changing Camera angle from " + Camera.getCameraAngle() + " to " + angle + ".");
			try {
				Camera.setCameraAngle(angle);
				Thread.sleep(100);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
