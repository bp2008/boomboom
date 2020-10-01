package com.brian.boomboom.input;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.Vector3;
import com.brian.boomboom.BoomBoomGame;
import com.brian.boomboom.world.WorldMap;

public class InputManager implements InputProcessor, ControllerListener
{
	private WorldMap worldMap;
	public String debug = "";
	public ArrayList<Controller> allControllers;
	public ArrayList<PovDirection> previousPovDirection;
	public int numControllers;

	public InputManager()
	{
		allControllers = new ArrayList<Controller>();
		previousPovDirection = new ArrayList<PovDirection>();
		boolean addedWii = false;
		for (Controller controller : Controllers.getControllers())
		{
			if(WiiController.nameCheck(controller))
			{
				if(addedWii)
					continue;
				addedWii = true;
			}
			allControllers.add(controller);
			previousPovDirection.add(PovDirection.center);
		}
		Controllers.addListener(this);
		numControllers = allControllers.size();
	}

	public void Shutdown()
	{
		Controllers.removeListener(this);
	}

	@Override
	public boolean keyDown(int keycode)
	{
		boolean handled = false;

		if (worldMap == null)
			return handled;

		for (int i = 0; i < worldMap.players.length && !handled; i++)
		{
			handled = true;
			KeyboardScheme kbScheme = worldMap.players[i].getKeyboardScheme();
			if (keycode == kbScheme.Up)
			{
				worldMap.players[i].QueueMove(Direction.Up, false);
			} else if (keycode == kbScheme.Down)
			{
				worldMap.players[i].QueueMove(Direction.Down, false);
			} else if (keycode == kbScheme.Left)
			{
				worldMap.players[i].QueueMove(Direction.Left, false);
			} else if (keycode == kbScheme.Right)
			{
				worldMap.players[i].QueueMove(Direction.Right, false);
			} else if (keycode == kbScheme.FaceUp)
			{
				worldMap.players[i].QueueMove(Direction.Up, true);
			} else if (keycode == kbScheme.FaceDown)
			{
				worldMap.players[i].QueueMove(Direction.Down, true);
			} else if (keycode == kbScheme.FaceLeft)
			{
				worldMap.players[i].QueueMove(Direction.Left, true);
			} else if (keycode == kbScheme.FaceRight)
			{
				worldMap.players[i].QueueMove(Direction.Right, true);
			} else if (keycode == kbScheme.Bomb)
			{
				worldMap.players[i].Input_Bomb();
			} else if (keycode == kbScheme.Use)
			{
				worldMap.players[i].Input_Use();
			} else if (keycode == kbScheme.Back)
			{
				worldMap.players[i].Input_Back();
			} else
				handled = false;
		}
		return handled;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}

	public void setWorld(WorldMap worldMap)
	{
		this.worldMap = worldMap;

		if (worldMap == null)
			return;

		if (worldMap.players.length > 0)
			worldMap.players[0].setKeyboardScheme(new KeyboardScheme(0, Keys.W, Keys.S, Keys.A, Keys.D, Keys.Z, Keys.X,
					Keys.C, Keys.V, Keys.Q, Keys.E, Keys.R));
		if (worldMap.players.length > 1)
			worldMap.players[1].setKeyboardScheme(new KeyboardScheme(1, Keys.I, Keys.K, Keys.J, Keys.L, Keys.B, Keys.N,
					Keys.M, Keys.COMMA, Keys.U, Keys.O, Keys.P));
		if (worldMap.players.length > 2)
			worldMap.players[2].setKeyboardScheme(new KeyboardScheme(1, Keys.I, Keys.K, Keys.J, Keys.L, Keys.B, Keys.N,
					Keys.M, Keys.COMMA, Keys.U, Keys.O, Keys.P));
		if (worldMap.players.length > 3)
			worldMap.players[3].setKeyboardScheme(new KeyboardScheme(1, Keys.I, Keys.K, Keys.J, Keys.L, Keys.B, Keys.N,
					Keys.M, Keys.COMMA, Keys.U, Keys.O, Keys.P));
	}

	/**
	 * Updates gamepad states.
	 */
	public void Update()
	{
		// BoomBoom now uses a hybrid event/polling driven input scheme. No work needs to be done in this Update()
		// function.
	}

	public boolean isDirectionPressed(Direction dir, int playerIndex)
	{
		if (BoomBoomGame.gui.BlockInput())
			return false;

		if (worldMap == null)
			return false;

		PovDirection povDir = null;
		if (allControllers.size() > playerIndex)
		{
			Controller c = allControllers.get(playerIndex);
			if (c != null)
			{
				if (Ouya.runningOnOuya)
				{
					float axisY = c.getAxis(OuyaExtra.AXIS_DPAD_VERTICAL);
					float axisX = c.getAxis(OuyaExtra.AXIS_DPAD_HORIZONTAL);
					boolean up = c.getButton(Ouya.BUTTON_DPAD_UP) || axisY < -0.5;
					boolean down = c.getButton(Ouya.BUTTON_DPAD_DOWN) || axisY > 0.5;
					boolean left = c.getButton(Ouya.BUTTON_DPAD_LEFT) || axisX < -0.5;
					boolean right = c.getButton(Ouya.BUTTON_DPAD_RIGHT) || axisX > 0.5;
					if (up && left)
						povDir = PovDirection.northWest;
					else if (up && right)
						povDir = PovDirection.northEast;
					else if (down && left)
						povDir = PovDirection.southWest;
					else if (down && right)
						povDir = PovDirection.southEast;
					else if (up)
						povDir = PovDirection.north;
					else if (down)
						povDir = PovDirection.south;
					else if (left)
						povDir = PovDirection.west;
					else if (right)
						povDir = PovDirection.east;
				} else
					povDir = c.getPov(0);
			}
		}
		switch (dir)
		{
			case Up:
				return povDir == PovDirection.north || povDir == PovDirection.northWest
						|| povDir == PovDirection.northEast;
			case Down:
				return povDir == PovDirection.south || povDir == PovDirection.southWest
						|| povDir == PovDirection.southEast;
			case Left:
				return povDir == PovDirection.west || povDir == PovDirection.northWest
						|| povDir == PovDirection.southWest;
			case Right:
				return povDir == PovDirection.east || povDir == PovDirection.northEast
						|| povDir == PovDirection.southEast;
		}
		if (worldMap.players.length > playerIndex)
		{
			KeyboardScheme kbScheme = worldMap.players[playerIndex].getKeyboardScheme();
			switch (dir)
			{
				case Up:
					return Gdx.input.isKeyPressed(kbScheme.Up);
				case Down:
					return Gdx.input.isKeyPressed(kbScheme.Down);
				case Left:
					return Gdx.input.isKeyPressed(kbScheme.Left);
				case Right:
					return Gdx.input.isKeyPressed(kbScheme.Right);
			}
		}
		return false;
	}

	private void BombButtonPressed(int playerIndex)
	{
		if (BoomBoomGame.gui.BombButtonPressed(playerIndex))
		{
			return;
		}

		if (worldMap == null)
			return;

		if (worldMap.players.length > playerIndex)
			worldMap.players[playerIndex].Input_Bomb();
	}

	private void UseButtonPressed(int playerIndex)
	{
		if (BoomBoomGame.gui.UseButtonPressed(playerIndex))
			return;

		if (worldMap == null)
			return;

		if (worldMap.players.length > playerIndex)
			worldMap.players[playerIndex].Input_Use();
	}

	private void StartButtonPressed(int playerIndex)
	{
		if (BoomBoomGame.gui.StartButtonPressed(playerIndex))
			return;
	}

	private void DirectionButtonPressed(int playerIndex, Direction dir)
	{
		if (BoomBoomGame.gui.DirectionButtonPressed(playerIndex, dir))
			return;

		if (worldMap == null)
			return;

		if (worldMap.players.length > playerIndex)
		{
			if (dir == Direction.Left)
			{
				worldMap.players[playerIndex].QueueMove(Direction.Left, false);
			} else if (dir == Direction.Right)
			{
				worldMap.players[playerIndex].QueueMove(Direction.Right, false);
			} else if (dir == Direction.Up)
			{
				worldMap.players[playerIndex].QueueMove(Direction.Up, false);
			} else if (dir == Direction.Down)
			{
				worldMap.players[playerIndex].QueueMove(Direction.Down, false);
			}
		}
	}

	// Facing directions is handled via polling now.
	// private void FaceDirectionPressed(int playerIndex, Direction dir)
	// {
	// if (dir != Direction.None)
	// worldMap.players[playerIndex].QueueMove(dir, true);
	// }

	private void BackButtonPressed(int playerIndex)
	{
		if (BoomBoomGame.gui.BackButtonPressed(playerIndex))
			return;

		if (worldMap == null)
			return;

		if (worldMap.players.length > playerIndex)
			worldMap.players[playerIndex].Input_Back();
	}

	public boolean isFacingDirectionPressed(Direction dir, int playerIndex)
	{
		if (BoomBoomGame.gui.BlockInput())
			return false;

		if (getFacingDirection(playerIndex) == dir)
			return true;
		else
		{
			if (worldMap == null)
				return false;

			if (worldMap.players.length > playerIndex)
			{
				KeyboardScheme kbScheme = worldMap.players[playerIndex].getKeyboardScheme();
				if (dir == Direction.Up)
					return Gdx.input.isKeyPressed(kbScheme.FaceUp);
				if (dir == Direction.Down)
					return Gdx.input.isKeyPressed(kbScheme.FaceDown);
				if (dir == Direction.Left)
					return Gdx.input.isKeyPressed(kbScheme.FaceLeft);
				if (dir == Direction.Right)
					return Gdx.input.isKeyPressed(kbScheme.FaceRight);
				// If we get here, the direction we are checking against must be Direction.None
				// Return true only if none of the direction keys are being pressed.
				return !(Gdx.input.isKeyPressed(kbScheme.FaceUp) || Gdx.input.isKeyPressed(kbScheme.FaceDown)
						|| Gdx.input.isKeyPressed(kbScheme.FaceLeft) || Gdx.input.isKeyPressed(kbScheme.FaceRight));
			}
			return false;
		}
	}

//	public boolean isStartButtonPressed(int playerIndex)
//	{
//		if (BoomBoomGame.gui.BlockInput())
//			return false;
//
//		if (allControllers.size() > playerIndex)
//		{
//			Controller c = allControllers.get(playerIndex);
//			if (c != null)
//			{
//				if (Ouya.runningOnOuya)
//				{
//					if (c.getButton(Ouya.BUTTON_MENU))
//						return true;
//				}
//				else if (F510.IsPlaystationController((c)))
//				{
//					if(c.getButton(8) || c.getButton(9) || c.getButton(12))
//						return true;
//				}
//				else// if (F510.nameCheck(c.getName()))
//				{
//					if (c.getButton(F510.BUTTON_BACK) || c.getButton(F510.BUTTON_START))
//						return true;
//				}
//			}
//		}
//		if (worldMap == null)
//			return false;
//
//		if (worldMap.players.length > playerIndex)
//		{
//			KeyboardScheme kbScheme = worldMap.players[playerIndex].getKeyboardScheme();
//			return Gdx.input.isKeyPressed(kbScheme.Back);
//		}
//		return false;
//	}

	public boolean isActionButtonPressed(int playerIndex)
	{
		if (BoomBoomGame.gui.BlockInput())
			return false;

		if (allControllers.size() > playerIndex)
		{
			Controller c = allControllers.get(playerIndex);
			if (c != null)
			{
				if (Ouya.runningOnOuya)
				{
					if (c.getButton(Ouya.BUTTON_U) || c.getButton(Ouya.BUTTON_Y) || c.getButton(Ouya.BUTTON_L1)
							|| c.getButton(Ouya.BUTTON_L2))
						return true;
				} else// if (F510.nameCheck(c.getName()))
				{
					if (c.getButton(F510.BUTTON_X) || c.getButton(F510.BUTTON_Y) || c.getButton(F510.BUTTON_L))
						return true;
				}
			}
		}
		if (worldMap == null)
			return false;

		if (worldMap.players.length > playerIndex)
		{
			KeyboardScheme kbScheme = worldMap.players[playerIndex].getKeyboardScheme();
			return Gdx.input.isKeyPressed(kbScheme.Use);
		}
		return false;
	}

	/**
	 * Gets the facing direction indicated by the position of the right stick. The returned direction is the direction
	 * the stick is held most toward. A dead zone of 25% is honored.
	 *
	 * @param playerIndex
	 * @return
	 */
	public Direction getFacingDirection(int playerIndex)
	{
		if (BoomBoomGame.gui.BlockInput())
			return Direction.None;

		Direction greatestDirection = Direction.None;

		if (allControllers.size() > playerIndex)
		{
			Controller c = allControllers.get(playerIndex);
			if (c != null)
			{
				int axis_X = 0;
				int axis_Y = 0;
				if (Ouya.runningOnOuya)
				{
					axis_X = Ouya.AXIS_RIGHT_X;
					axis_Y = Ouya.AXIS_RIGHT_Y;
				}
				else if (PSController.nameCheck(c))
				{
					axis_X = PSController.AXIS_RIGHT_X;
					axis_Y = PSController.AXIS_RIGHT_Y;
				}
				else// if (F510.nameCheck(c.getName()))
				{
					axis_X = F510.AXIS_RIGHT_X;
					axis_Y = F510.AXIS_RIGHT_Y;
				}
				float xAxis = c.getAxis(axis_X);
				float yAxis = c.getAxis(axis_Y);
				float up = yAxis < 0 ? -yAxis : 0;
				float down = yAxis > 0 ? yAxis : 0;
				float left = xAxis < 0 ? -xAxis : 0;
				float right = xAxis > 0 ? xAxis : 0;

				float greatestAmount = 0;

				if (right >= 0.25 && right >= greatestAmount)
				{
					greatestDirection = Direction.Right;
					greatestAmount = right;
				}
				if (left >= 0.25 && left >= greatestAmount)
				{
					greatestDirection = Direction.Left;
					greatestAmount = left;
				}
				if (down >= 0.25 && down >= greatestAmount)
				{
					greatestDirection = Direction.Down;
					greatestAmount = down;
				}
				if (up >= 0.25 && up >= greatestAmount)
				{
					greatestDirection = Direction.Up;
					greatestAmount = up;
				}
			}
			if (greatestDirection == Direction.None)
			{

				if (worldMap == null)
					return greatestDirection;

				// Fallback to keyboard
				if (worldMap.players.length > playerIndex)
				{
					KeyboardScheme kbScheme = worldMap.players[playerIndex].getKeyboardScheme();
					if (Gdx.input.isKeyPressed(kbScheme.FaceUp))
						return Direction.Up;
					if (Gdx.input.isKeyPressed(kbScheme.FaceDown))
						return Direction.Down;
					if (Gdx.input.isKeyPressed(kbScheme.FaceLeft))
						return Direction.Left;
					if (Gdx.input.isKeyPressed(kbScheme.FaceRight))
						return Direction.Right;
				}
			}
		}
		return greatestDirection;
	}

	public float getAnalogMovementForce(int playerIndex, Direction dir)
	{
		if (BoomBoomGame.gui.BlockInput())
			return 0f;

		if (allControllers.size() > playerIndex)
		{
			Controller c = allControllers.get(playerIndex);
			if (c != null)
			{
				int axis_X = 0;
				int axis_Y = 0;
				if (Ouya.runningOnOuya)
				{
					axis_X = Ouya.AXIS_LEFT_X;
					axis_Y = Ouya.AXIS_LEFT_Y;
				}
				else if (PSController.nameCheck(c))
				{
					axis_X = PSController.AXIS_LEFT_X;
					axis_Y = PSController.AXIS_LEFT_Y;
				}
				else// if (F510.nameCheck(c.getName()))
				{
					axis_X = F510.AXIS_LEFT_X;
					axis_Y = F510.AXIS_LEFT_Y;
				}
//				else
//				{
//					return 0f;
//				}
				// Controller has been identified.
				float movementDesire = 0f;
				if (dir == Direction.Up)
				{
					movementDesire = c.getAxis(axis_Y) * -1;
				} else if (dir == Direction.Down)
				{
					movementDesire = c.getAxis(axis_Y);
				} else if (dir == Direction.Left)
				{
					movementDesire = c.getAxis(axis_X) * -1;
				} else if (dir == Direction.Right)
				{
					movementDesire = c.getAxis(axis_X);
				}
				return movementDesire;
			}
		}
		return 0f;
	}

	public float getAnalogStickDeadZone(int playerIndex)
	{
		if (allControllers.size() > playerIndex)
		{
			Controller c = allControllers.get(playerIndex);
			if (c != null)
			{
				if (F510.IsXboxController(c))
				{
					return 0.2f;
				} else
					return 0.1f;
			}
		}
		return 0.1f;
	}

	@Override
	public void connected(Controller controller)
	{
		if (allControllers.contains(controller))
			return;
		allControllers.add(controller);
		previousPovDirection.add(PovDirection.center);
		numControllers = allControllers.size();
		Gdx.app.log("CONTROLLER " + allControllers.size(), "Connected");
	}

	@Override
	public void disconnected(Controller controller)
	{
		int i = GetControllerIndex(controller);
		allControllers.remove(i);
		previousPovDirection.set(i, PovDirection.center);
		numControllers = allControllers.size();
		Gdx.app.log("CONTROLLER " + allControllers.size(), "Disconnected");
	}

	private static void OutputButtonInfo(Controller c)
	{
		try
		{
			for (int i = 0; i < 15; i++)
			{
				if(c.getButton(i))
					System.out.println(i + ": " + c.getButton(i));
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode)
	{
//		System.out.println(buttonCode);
		int i = GetControllerIndex(controller);
		if (Ouya.runningOnOuya)
		{
			if (buttonCode == Ouya.BUTTON_O || buttonCode == Ouya.BUTTON_A || buttonCode == Ouya.BUTTON_R1
					|| buttonCode == Ouya.BUTTON_R2)
				BombButtonPressed(i);
			else if (buttonCode == Ouya.BUTTON_U || buttonCode == Ouya.BUTTON_Y || buttonCode == Ouya.BUTTON_L1
					|| buttonCode == Ouya.BUTTON_L2)
				UseButtonPressed(i);
			else if (buttonCode == Ouya.BUTTON_MENU || buttonCode == OuyaExtra.BUTTON_BACK
					|| buttonCode == OuyaExtra.BUTTON_START)
				BackButtonPressed(i);
			else if (buttonCode == Ouya.BUTTON_DPAD_UP)
				DirectionButtonPressed(i, Direction.Up);
			else if (buttonCode == Ouya.BUTTON_DPAD_DOWN)
				DirectionButtonPressed(i, Direction.Down);
			else if (buttonCode == Ouya.BUTTON_DPAD_LEFT)
				DirectionButtonPressed(i, Direction.Left);
			else if (buttonCode == Ouya.BUTTON_DPAD_RIGHT)
				DirectionButtonPressed(i, Direction.Right);
			else
				return false;
			return true;
		}
		else if(WiiController.nameCheck(controller))
		{
			if (buttonCode == WiiController.BUTTON_B || buttonCode == WiiController.BUTTON_A || buttonCode == WiiController.BUTTON_R)
				BombButtonPressed(i);
			else if (buttonCode == WiiController.BUTTON_X || buttonCode == WiiController.BUTTON_Y || buttonCode == WiiController.BUTTON_L)
				UseButtonPressed(i);
			else if (buttonCode == WiiController.BUTTON_SELECT || buttonCode == WiiController.BUTTON_HOME)
				BackButtonPressed(i);
			else if (buttonCode == WiiController.BUTTON_START)
				StartButtonPressed(i);
			else
				return false;
			return true;
		}
		else if(PSController.nameCheck(controller))
		{
			if (buttonCode == PSController.BUTTON_X || buttonCode == PSController.BUTTON_CIRCLE || buttonCode == PSController.BUTTON_R)
				BombButtonPressed(i);
			else if (buttonCode == PSController.BUTTON_SQUARE || buttonCode == PSController.BUTTON_TRIANGLE || buttonCode == PSController.BUTTON_L)
				UseButtonPressed(i);
			else if (buttonCode == PSController.BUTTON_SHARE || buttonCode == PSController.BUTTON_OPTIONS|| buttonCode == PSController.BUTTON_TOUCHPAD)
				BackButtonPressed(i);
			else if (buttonCode == PSController.BUTTON_PLAYSTATION)
				StartButtonPressed(i);
			else
				return false;
			return true;
		}
		else// if (F510.nameCheck(controller.getName()))
		{
			if (buttonCode == F510.BUTTON_A || buttonCode == F510.BUTTON_B || buttonCode == F510.BUTTON_R)
				BombButtonPressed(i);
			else if (buttonCode == F510.BUTTON_X || buttonCode == F510.BUTTON_Y || buttonCode == F510.BUTTON_L)
				UseButtonPressed(i);
			else if (buttonCode == F510.BUTTON_BACK)
				BackButtonPressed(i);
			else if (buttonCode == F510.BUTTON_START)
				StartButtonPressed(i);
			else
				return false;
			return true;
		}
//		else
//		{
//			//Gdx.app.log("CONTROLLER " + (i + 1) + " (" + controller.getName() + ")", String.valueOf(buttonCode)
//			//		+ " Pressed");
//			return false;
//		}
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode)
	{
		// BoomBoom does not care about buttonUp events.
		//
		// int i = GetControllerIndex(controller);
		// Gdx.app.log("CONTROLLER " + (i + 1), String.valueOf(buttonCode) + " Released");
		// switch (buttonCode)
		// {
		// default:
		// break;
		// }
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value)
	{
		// BoomBoom does not care about axisMoved events except for 3rd party controllers where d-pad movement is handled by axes. 
		// The true analog controls are polled by BoomBoom and their events are ignored.
		//
		if (Ouya.runningOnOuya)
		{
			int i = GetControllerIndex(controller);
			if (axisCode == OuyaExtra.AXIS_DPAD_VERTICAL)
			{
				if(value < -0.5)
					DirectionButtonPressed(i, Direction.Up);
				else if(value > 0.5)
					DirectionButtonPressed(i, Direction.Down);
			}
			if (axisCode == OuyaExtra.AXIS_DPAD_HORIZONTAL)
			{
				if(value < -0.5)
					DirectionButtonPressed(i, Direction.Left);
				else if(value > 0.5)
					DirectionButtonPressed(i, Direction.Right);
			}
		}
		//Gdx.app.log("CONTROLLER " + (i + 1), axisCode + " -> " + value);
		return false;
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value)
	{
		int i = GetControllerIndex(controller);
		PovDirection previous = previousPovDirection.get(i);
		if (value == PovDirection.center)
		{
		}
		else if (value == PovDirection.west)
		{
			if (previous != PovDirection.northWest && previous != PovDirection.southWest)
				DirectionButtonPressed(i, Direction.Left);
		}
		else if (value == PovDirection.east)
		{
			if (previous != PovDirection.northEast && previous != PovDirection.southEast)
				DirectionButtonPressed(i, Direction.Right);
		}
		else if (value == PovDirection.north)
		{
			if (previous != PovDirection.northWest && previous != PovDirection.northEast)
				DirectionButtonPressed(i, Direction.Up);
		}
		else if (value == PovDirection.south)
		{
			if (previous != PovDirection.southWest && previous != PovDirection.southEast)
				DirectionButtonPressed(i, Direction.Down);
		}
		else if (value == PovDirection.northWest)
		{
			if (previous == PovDirection.north)
				DirectionButtonPressed(i, Direction.Left);
			else
				DirectionButtonPressed(i, Direction.Up);
		}
		else if (value == PovDirection.northEast)
		{
			if (previous == PovDirection.north)
				DirectionButtonPressed(i, Direction.Right);
			else
				DirectionButtonPressed(i, Direction.Up);
		}
		else if (value == PovDirection.southWest)
		{
			if (previous == PovDirection.south)
				DirectionButtonPressed(i, Direction.Left);
			else
				DirectionButtonPressed(i, Direction.Down);
		}
		else if (value == PovDirection.southEast)
		{
			if (previous == PovDirection.south)
				DirectionButtonPressed(i, Direction.Right);
			else
				DirectionButtonPressed(i, Direction.Down);
		}
		previousPovDirection.set(i, value);
		//Gdx.app.log("CONTROLLER " + (i + 1), povCode + " -> " + value.toString());
		return true;
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
	{
		//int i = GetControllerIndex(controller);
		//Gdx.app.log("CONTROLLER " + (i + 1), sliderCode + " -> " + value);
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
	{
		//int i = GetControllerIndex(controller);
		//Gdx.app.log("CONTROLLER " + (i + 1), sliderCode + " -> " + value);
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
	{
		//int i = GetControllerIndex(controller);
		//Gdx.app.log("CONTROLLER " + (i + 1), accelerometerCode + " -> " + value.x + "," + value.y + "," + value.z);
		return false;
	}

	private int GetControllerIndex(Controller controller)
	{
		int i = allControllers.indexOf(controller);
		if (i == -1)
		{
			connected(controller);
			i = allControllers.indexOf(controller);
		}
		return i;
	}
}
