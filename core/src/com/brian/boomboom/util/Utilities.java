package com.brian.boomboom.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.badlogic.gdx.graphics.Color;

public class Utilities
{
	public static long getTimeInMs()
	{
		Calendar c = Calendar.getInstance();
		long currentTime = c.getTimeInMillis();
		return currentTime;
	}

	public static Color Color(int r, int g, int b)
	{
		return new Color(r / 255f, g / 255f, b / 255f, 255f);
	}

	public static int Clamp(int i, int min, int max)
	{
		return Math.max(Math.min(i, max), min);
	}

	/**
	 * Converts the specified byte array to a String using UTF-8 encoding.
	 * 
	 * @param data
	 *            A byte array representing String data in UTF-8 encoding.
	 * @return The String represented by the byte array. Returns an empty String in the event of an encoding error.
	 */
	public static String ToString(byte[] data)
	{
		if (data != null && data.length > 0)
			try
			{
				return new String(data, "UTF-8");
			}
			catch (UnsupportedEncodingException ex)
			{
				BBLog.debug(ex, Utilities.class);
			}
		return "";
	}

	/**
	 * Writes the specified data to the file, deleting any previously existing file content.
	 * 
	 * @param fullPath
	 *            Full path to the file.
	 * @param bytes
	 *            The bytes to write.
	 * @throws Exception
	 */
	public static void WriteFile(String fullPath, byte[] bytes) throws Exception
	{
		WriteFile(fullPath, bytes, false);
	}

	/**
	 * Writes the specified data to the file, optionally appending to the existing file content.
	 * 
	 * @param fullPath
	 *            Full path to the file.
	 * @param bytes
	 *            The bytes to write.
	 * @param append
	 *            If true, the existing file content will not be deleted.
	 * @throws Exception
	 */
	public static void WriteFile(String fullPath, byte[] bytes, boolean append) throws Exception
	{
		File file = new File(fullPath);
		if (file.exists() && !append)
			file.delete();
		else
		{
			String parentDir = file.getParent();
			if (!EnsureDirectoryExists(parentDir))
			{
				BBLog.debug("Could not ensure that directory exists in Utilities.WriteFile("
						+ (parentDir == null ? "null" : parentDir) + ")", Utilities.class);
				throw new Exception("Could not access path " + file.getAbsolutePath());
			}
		}
		FileOutputStream outFile = null;
		try
		{
			outFile = new FileOutputStream(file, append);
			outFile.write(bytes);
			outFile.flush();
		}
		catch (Exception ex)
		{
			BBLog.debug(ex, Utilities.class, file.getName());
			if (outFile != null)
			{
				try
				{
					outFile.close();
				}
				catch (Exception e2)
				{
					BBLog.debug(e2, Utilities.class, file.getName());
					throw new Exception("Exception thrown when closing file " + file.getName(), e2);
				}
			}
			throw new Exception("Exception thrown when writing file " + file.getName(), ex);
		}
		finally
		{
			try
			{
				if (outFile != null)
					outFile.close();
			}
			catch (Exception ex)
			{
				// Ignore
			}
		}
	}

	/**
	 * Writes the specified String to the file, deleting any previously existing file content.
	 * 
	 * @param fullPath
	 *            Full path to the file.
	 * @param text
	 *            The text to write.
	 * @throws Exception
	 */
	public static void WriteTextFile(String fullPath, String text) throws Exception
	{
		WriteTextFile(fullPath, text, false);
	}

	/**
	 * Writes the specified String to the file, optionally appending to instead of deleting old content.
	 * 
	 * @param fullPath
	 *            Full path to the file.
	 * @param text
	 *            The text to write.
	 * @param append
	 *            If true, the text will be appended to any existing data in the file.
	 * @throws Exception
	 */
	public static void WriteTextFile(String fullPath, String text, boolean append) throws Exception
	{
		if (text == null)
			text = "";
		WriteFile(fullPath, text.getBytes("UTF-8"), append);
	}

	public static String ReadTextFile(String fullPath) throws Exception
	{
		byte[] bytes = ReadFile(fullPath);
		return Utilities.ToString(bytes);
	}

	public static byte[] ReadFile(String fullPath) throws Exception
	{
		File file = new File(fullPath);
		if (!file.exists())
			return new byte[0];
		FileInputStream inFile = null;
		inFile = new FileInputStream(file);
		ByteArrayOutputStream out = null;
		try
		{
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[8192];
			int read = 0;
			while (read != -1)
			{
				out.write(buf, 0, read);
				read = inFile.read(buf, 0, 8192);
			}
			return out.toByteArray();
		}
		catch (Exception ex)
		{
			BBLog.debug(ex, Utilities.class, file.getName());
			if (inFile != null)
			{
				try
				{
					inFile.close();
				}
				catch (Exception e2)
				{
					BBLog.debug(e2, Utilities.class, file.getName());
					throw new Exception("Exception thrown when closing file " + file.getName(), e2);
				}
			}
			throw new Exception("Exception thrown when reading file " + file.getName(), ex);
		}
		finally
		{
			try
			{
				if (inFile != null)
					inFile.close();
			}
			catch (Exception ex)
			{
				// Ignore
			}
			try
			{
				if (out != null)
					out.close();
			}
			catch (Exception ex)
			{
				// Ignore
			}
		}
	}

	/**
	 * Tries to create the specified directory, returning true if the directory already exists or was created by the
	 * function.
	 * 
	 * @param path
	 * @return
	 */
	public static boolean EnsureDirectoryExists(String path)
	{
		if (path == null || path.equals(""))
			return false;
		File directory = new File(path);
		if (!directory.exists())
			return directory.mkdirs();
		return true;
	}
}
